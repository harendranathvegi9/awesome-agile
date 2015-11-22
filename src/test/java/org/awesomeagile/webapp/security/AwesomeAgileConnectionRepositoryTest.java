package org.awesomeagile.webapp.security;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionRepositoryTest {

  public static final String PROVIDER_ONE = "one";
  private ConnectionRepository connectionRepository;
  private UserRepository userRepository;
  private ConnectionFactoryLocator connectionFactoryLocator;
  private AtomicLong idProvider = new AtomicLong(1);
  private long userId;
  private long providerUserId;
  private Map<String, ConnectionFactory<?>> connectionFactories;
  private Map<Class<? extends ConnectionFactory>, ConnectionFactory> connectionFactoriesByClass;
  private ConnectionFactory connectionFactoryOne;
  private ConnectionFactory connectionFactoryTwo;

  @Before
  public void setUp() throws Exception {
    userId = idProvider.getAndIncrement();
    providerUserId = idProvider.getAndIncrement();
    userRepository = mock(UserRepository.class);
    when(userRepository.findOne(userId)).thenReturn(user());
    connectionFactoryLocator = new TestConnectionFactoryLocator();
    connectionFactoryOne = mock(TestConnectionFactoryTwo.class);
    connectionFactoryTwo = mock(ConnectionFactory.class);
    connectionFactories = ImmutableMap.of(
        PROVIDER_ONE, connectionFactoryOne,
        "two", connectionFactoryTwo);
    connectionFactoriesByClass = ImmutableMap.of(
        TestConnectionFactoryTwo.class, connectionFactoryOne);
    connectionRepository = new AwesomeAgileConnectionRepository(
        userRepository, userId, connectionFactoryLocator);
  }

  private User user() {
    User user = new User();
    user.setId(userId);
    user
        .setAuthProviderId(PROVIDER_ONE)
        .setAuthProviderUserId(PROVIDER_ONE + ":" + providerUserId)
        .setPrimaryEmail("belov.stan@gmail.com")
        .setDisplayName("stan")
        .setIsVisible(true)
        .setStatus(UserStatus.ACTIVE)
        .setAvatar("http://static.akamai.com/image.png");
    return user;
  }

  @Test
  public void testFindAllConnections() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();
    assertNotNull(allConnections);
    assertEquals(1, allConnections.size());
    List<Connection<?>> providerOneConnections = allConnections.get(PROVIDER_ONE);
    assertThat(providerOneConnections, contains(connection));
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test
  public void testFindConnections() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    List<Connection<?>> providerOneConnections =
        connectionRepository.findConnections(PROVIDER_ONE);
    assertThat(providerOneConnections, contains(connection));
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test
  public void testFindConnectionsByClass() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    List<Connection<TestConnectionFactoryOne>> providerOneConnections =
        connectionRepository.findConnections(TestConnectionFactoryOne.class);
    assertThat(providerOneConnections, contains(connection));
    ConnectionData connectionData = connectionDataCaptor.getValue();
    User user = user();
    assertConnectionData(user, connectionData);
  }

  @Test
  public void testFindConnectionsByClassNotFound() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    List<Connection<TestConnectionFactoryTwo>> providerOneConnections =
        connectionRepository.findConnections(TestConnectionFactoryTwo.class);
    assertThat(providerOneConnections, contains(connection));
    ConnectionData connectionData = connectionDataCaptor.getValue();
    User user = user();
    assertConnectionData(user, connectionData);
  }

  @Test
  public void testFindConnectionsToUsers() throws Exception {

  }

  @Test
  public void testGetConnection() throws Exception {

  }

  @Test
  public void testGetConnection1() throws Exception {

  }

  @Test
  public void testGetPrimaryConnection() throws Exception {

  }

  @Test
  public void testFindPrimaryConnection() throws Exception {

  }

  @Test
  public void testAddConnection() throws Exception {

  }

  @Test
  public void testUpdateConnection() throws Exception {

  }

  @Test
  public void testRemoveConnections() throws Exception {

  }

  @Test
  public void testRemoveConnection() throws Exception {

  }

  private static class TestConnectionFactoryOne extends ConnectionFactory {

    /**
     * Creates a new ConnectionFactory.
     *
     * @param providerId the assigned, unique id of the provider this factory creates connections to
     * (used when indexing this factory in a registry)
     * @param serviceProvider the model for the ServiceProvider used to conduct the connection
     * authorization/refresh flow and obtain a native service API instance
     * @param apiAdapter the adapter that maps common operations exposed by the ServiceProvider's API to
     * the uniform {@link Connection} model
     */
    private TestConnectionFactoryOne(String providerId,
        ServiceProvider serviceProvider,
        ApiAdapter apiAdapter) {
      super(providerId, serviceProvider, apiAdapter);
    }

    @Override
    public Connection createConnection(ConnectionData data) {
      throw new UnsupportedOperationException();
    }
  }

  private static class TestConnectionFactoryTwo extends ConnectionFactory {

    /**
     * Creates a new ConnectionFactory.
     *
     * @param providerId the assigned, unique id of the provider this factory creates connections to
     * (used when indexing this factory in a registry)
     * @param serviceProvider the model for the ServiceProvider used to conduct the connection
     * authorization/refresh flow and obtain a native service API instance
     * @param apiAdapter the adapter that maps common operations exposed by the ServiceProvider's API to
     * the uniform {@link Connection} model
     */
    private TestConnectionFactoryTwo(String providerId,
        ServiceProvider serviceProvider,
        ApiAdapter apiAdapter) {
      super(providerId, serviceProvider, apiAdapter);
    }

    @Override
    public Connection createConnection(ConnectionData data) {
      throw new UnsupportedOperationException();
    }
  }

  private class TestConnectionFactoryLocator implements ConnectionFactoryLocator {

    @Override
    public ConnectionFactory<?> getConnectionFactory(String factoryName) {
      return connectionFactories.get(factoryName);
    }

    @Override
    public <A> ConnectionFactory<A> getConnectionFactory(Class<A> factoryClass) {
      return connectionFactoriesByClass.get(factoryClass);
    }

    @Override
    public Set<String> registeredProviderIds() {
      throw new UnsupportedOperationException();
    }
  }

  private void assertConnectionData(User user, ConnectionData connectionData) {
    assertEquals(user.getDisplayName(), connectionData.getDisplayName());
    assertEquals(user.getAvatar(), connectionData.getImageUrl());
    assertEquals(user.getAuthProviderId(), connectionData.getProviderId());
    assertEquals(user.getAuthProviderUserId(), connectionData.getProviderUserId());
  }
}