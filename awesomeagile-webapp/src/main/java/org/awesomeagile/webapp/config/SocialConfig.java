package org.awesomeagile.webapp.config;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.webapp.security.AwesomeAgileConnectionSignup;
import org.awesomeagile.webapp.security.AwesomeAgileUsersConnectionRepository;
import org.awesomeagile.webapp.security.google.GoogleConfigurableConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

  @Value("${spring.social.google.clientId}")
  private String googleClientId;

  @Value("${spring.social.google.secret}")
  private String googleSecret;

  @Value("${spring.social.google.scope}")
  private String googleScope;

  @Value("${spring.social.google.oauth.url}")
  private String googleOAuthUrl;

  @Value("${spring.social.google.api.url}")
  private String googleApiUrl;

  @Autowired
  private UserRepository userRepository;

  @Override
  public void addConnectionFactories(
      ConnectionFactoryConfigurer connectionFactoryConfigurer,
      Environment environment) {
    GoogleConfigurableConnectionFactory googleConnectionFactory =
        new GoogleConfigurableConnectionFactory(
            googleClientId, googleSecret, googleOAuthUrl, googleApiUrl);
    googleConnectionFactory.setScope(googleScope);
    connectionFactoryConfigurer.addConnectionFactory(googleConnectionFactory);
  }

  @Override
  public UsersConnectionRepository getUsersConnectionRepository(
      ConnectionFactoryLocator connectionFactoryLocator) {
    return new AwesomeAgileUsersConnectionRepository(
        userRepository,
        connectionFactoryLocator,
        getConnectionSignUp());
  }

  @Override
  public UserIdSource getUserIdSource() {
    return new AuthenticationNameUserIdSource();
  }

  private ConnectionSignUp getConnectionSignUp() {
    return new AwesomeAgileConnectionSignup(userRepository);
  }
}
