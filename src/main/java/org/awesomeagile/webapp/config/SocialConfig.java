package org.awesomeagile.webapp.config;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------------------------
 */

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
