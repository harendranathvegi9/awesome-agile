package org.awesomeagile.webapp.config;

import org.awesomeagile.webapp.security.AwesomeAgileUsersConnectionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.google.connect.GoogleConnectionFactory;
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

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        GoogleConnectionFactory googleConnectionFactory = new GoogleConnectionFactory(googleClientId, googleSecret);
        googleConnectionFactory.setScope(googleScope);
        connectionFactoryConfigurer.addConnectionFactory(googleConnectionFactory);
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        //return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        return new AwesomeAgileUsersConnectionRepository();
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

}
