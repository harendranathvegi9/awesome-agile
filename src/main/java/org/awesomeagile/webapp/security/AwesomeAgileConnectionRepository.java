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

import static org.springframework.util.CollectionUtils.toMultiValueMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionRepository implements ConnectionRepository {

  private final UserRepository userRepository;
  private final long userId;
  private final ConnectionFactoryLocator connectionFactoryLocator;

  public AwesomeAgileConnectionRepository(
      UserRepository userRepository,
      long userId,
      ConnectionFactoryLocator connectionFactoryLocator) {
    this.userRepository = userRepository;
    this.userId = userId;
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  @Override
  public MultiValueMap<String, Connection<?>> findAllConnections() {
    User user = getUser();
    ConnectionFactory connectionFactory =
        connectionFactoryLocator.getConnectionFactory(user.getAuthProviderId());
    return toMultiValueMap(ImmutableMap.of(user.getAuthProviderId(),
            (List<Connection<?>>)ImmutableList.<Connection<?>>of(
                connectionFactory.createConnection(connectionData(user)))));
  }

  @Override
  public List<Connection<?>> findConnections(String providerId) {
    return findAllConnections().get(providerId);
  }

  @Override
  public <A> List<Connection<A>> findConnections(Class<A> apiType) {
    User user = getUser();
    ConnectionFactory<A> connectionFactory =
        connectionFactoryLocator.getConnectionFactory(apiType);
    if (connectionFactory != null) {
      return ImmutableList.of(connectionFactory.createConnection(connectionData(user)));
    }
    return ImmutableList.of();
  }

  @Override
  public MultiValueMap<String, Connection<?>> findConnectionsToUsers(
      MultiValueMap<String, String> providerUserIds) {
    return toMultiValueMap(ImmutableMap.<String, List<Connection<?>>>of());
  }

  @Override
  public Connection<?> getConnection(ConnectionKey key) {
    User user = userRepository.findOneByAuthProviderUserId(
        key.getProviderId(), key.getProviderUserId());
    if (user.getId() == userId) {
      ConnectionFactory<?> connectionFactory = connectionFactoryLocator
          .getConnectionFactory(key.getProviderId());
      return connectionFactory.createConnection(connectionData(user));
    }
    throw new NoSuchConnectionException(key);
  }

  @Override
  public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
    ConnectionFactory<A> connectionFactory = getConnectionFactory(apiType);
    User user = getUser();
    String providerId = connectionFactory.getProviderId();
    if (providerId.equals(user.getAuthProviderId())
        && providerUserId.equals(user.getAuthProviderUserId())) {
      return connectionFactory.createConnection(connectionData(user));
    }
    throw new NotConnectedException(providerId);
  }

  @Override
  public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
    Connection<A> connection = findPrimaryConnection(apiType);
    if (connection == null) {
      throw new NotConnectedException(getConnectionFactory(apiType).getProviderId());
    }
    return connection;
  }

  @Override
  public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
    ConnectionFactory<A> connectionFactory = getConnectionFactory(apiType);
    User user = getUser();
    String providerId = connectionFactory.getProviderId();
    if (providerId.equals(user.getAuthProviderId())) {
      return connectionFactory.createConnection(connectionData(user));
    }
    return null;
  }

  @Override
  public void addConnection(Connection<?> connection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void updateConnection(Connection<?> connection) {
    // no-op
  }

  @Override
  public void removeConnections(String providerId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeConnection(ConnectionKey connectionKey) {
    throw new UnsupportedOperationException();
  }

  private <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
    return connectionFactoryLocator.getConnectionFactory(apiType);
  }

  private User getUser() {
    return userRepository.findOne(userId);
  }

  private static ConnectionData connectionData(User user) {
    return new ConnectionData(
        user.getAuthProviderId(),
        user.getAuthProviderUserId(),
        user.getDisplayName(),
        "",
        user.getAvatar(),
        "",
        "",
        "",
        Long.MAX_VALUE
    );
  }

}
