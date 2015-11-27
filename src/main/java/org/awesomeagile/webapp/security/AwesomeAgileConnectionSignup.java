package org.awesomeagile.webapp.security;

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
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionSignup implements ConnectionSignUp {

  private final UserRepository userRepository;

  public AwesomeAgileConnectionSignup(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public String execute(Connection<?> connection) {
    User userFromProfile = createUserFromProfile(connection);
    userRepository.save(userFromProfile);
    return userFromProfile.getPrimaryEmail();
  }

  private User createUserFromProfile(Connection<?> connection) {
    UserProfile profile = connection.fetchUserProfile();
    ConnectionKey key = connection.getKey();
    return new User()
        .setPrimaryEmail(profile.getEmail())
        .setDisplayName(connection.getDisplayName())
        .setIsVisible(true)
        .setStatus(UserStatus.ACTIVE)
        .setAvatar(connection.getImageUrl())
        .setAuthProviderId(key.getProviderId())
        .setAuthProviderUserId(key.getProviderUserId());
  }
}
