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

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;

public class AwesomeAgileUsersConnectionRepository implements UsersConnectionRepository {

  private static final Function<User, String> USER_EMAIL = new Function<User, String>() {
    @Override
    public String apply(User user) {
      return user.getPrimaryEmail();
    }
  };
  private final UserRepository userRepository;
  private final ConnectionFactoryLocator connectionFactoryLocator;
  private final ConnectionSignUp connectionSignUp;

  public AwesomeAgileUsersConnectionRepository(UserRepository userRepository,
      ConnectionFactoryLocator connectionFactoryLocator, ConnectionSignUp connectionSignUp) {
    this.userRepository = userRepository;
    this.connectionFactoryLocator = connectionFactoryLocator;
    this.connectionSignUp = connectionSignUp;
  }

  @Override
  public List<String> findUserIdsWithConnection(Connection<?> connection) {
    ConnectionKey key = connection.getKey();
    User user = userRepository.findOneByAuthProviderUserId(
        key.getProviderId(),
        key.getProviderUserId());
    if (user == null) {
      return ImmutableList.of(connectionSignUp.execute(connection));
    }
    return ImmutableList.of(user.getPrimaryEmail());
  }

  @Override
  public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
    return FluentIterable
        .from(userRepository.findOneByAuthProviderUserIds(providerId, providerUserIds))
        .transform(USER_EMAIL).toSet();
  }

  @Override
  public ConnectionRepository createConnectionRepository(String userId) {
    User user = userRepository.findOneByPrimaryEmail(userId);
    return new AwesomeAgileConnectionRepository(
        userRepository,
        user.getId(),
        connectionFactoryLocator);
  }
}
