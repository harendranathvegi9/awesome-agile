package org.awesomeagile.webapp.security;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
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
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionRepositoryTest {

  private static final String PROVIDER_ONE = "one";
  private static final String PROVIDER_TWO = "two";
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
    connectionFactoryOne = mock(TestConnectionFactoryOne.class);
    when(connectionFactoryOne.getProviderId()).thenReturn(PROVIDER_ONE);
    connectionFactoryTwo = mock(TestConnectionFactoryTwo.class);
    when(connectionFactoryTwo.getProviderId()).thenReturn(PROVIDER_TWO);
    connectionFactories = ImmutableMap.of(
        PROVIDER_ONE, connectionFactoryOne,
        PROVIDER_TWO, connectionFactoryTwo);
    connectionFactoriesByClass = ImmutableMap.of(
        TestConnectionFactoryOne.class, connectionFactoryOne,
        TestConnectionFactoryTwo.class, connectionFactoryTwo);
    connectionRepository = new AwesomeAgileConnectionRepository(
        userRepository, userId, connectionFactoryLocator);
  }

  private User user() {
    User user = new User();
    user.setId(userId);
    user
        .setAuthProviderId(PROVIDER_ONE)
        .setAuthProviderUserId(providerUserId())
        .setPrimaryEmail("belov.stan@gmail.com")
        .setDisplayName("stan")
        .setIsVisible(true)
        .setStatus(UserStatus.ACTIVE)
        .setAvatar("http://static.akamai.com/image.png");
    return user;
  }

  private String providerUserId() {
    return PROVIDER_ONE + ":" + providerUserId;
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
    assertConnectionData(user(), connectionData);
  }

  @Test
  public void testFindConnectionsByClassNotFound() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    connectionFactoriesByClass = ImmutableMap.of(
        TestConnectionFactoryOne.class, connectionFactoryOne);
    List<Connection<TestConnectionFactoryTwo>> providerOneConnections =
        connectionRepository.findConnections(TestConnectionFactoryTwo.class);
    assertThat(providerOneConnections, empty());
  }

  @Test
  public void testFindConnectionsToUsers() throws Exception {
    MultiValueMap<String, Connection<?>> connectionsToUsers =
        connectionRepository.findConnectionsToUsers(
            CollectionUtils.toMultiValueMap(
                ImmutableMap.of(PROVIDER_ONE, ImmutableList.of(providerUserId()))));
    assertTrue(connectionsToUsers.isEmpty());
  }

  @Test
  public void testGetConnection() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    when(userRepository.findOneByAuthProviderUserId(PROVIDER_ONE, providerUserId()))
        .thenReturn(user());
    Connection<?> foundConnection = connectionRepository.getConnection(
        new ConnectionKey(PROVIDER_ONE, providerUserId()));
    assertNotNull(foundConnection);
    assertEquals(connection, foundConnection);
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test(expected = NoSuchConnectionException.class)
  public void testGetConnectionNonMatchingUser() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    User wrongUser = user();
    wrongUser.setId(idProvider.getAndIncrement());
    when(userRepository.findOneByAuthProviderUserId(PROVIDER_ONE, providerUserId()))
        .thenReturn(wrongUser);
    connectionRepository.getConnection(new ConnectionKey(PROVIDER_ONE, providerUserId()));
  }

  @Test
  public void testGetConnectionByProviderClass() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    Connection<?> foundConnection = connectionRepository.getConnection(
        TestConnectionFactoryOne.class, providerUserId());
    assertNotNull(foundConnection);
    assertEquals(connection, foundConnection);
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test(expected = NotConnectedException.class)
  public void testGetConnectionByProviderClassNotFound() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    connectionRepository.getConnection(
        TestConnectionFactoryOne.class, PROVIDER_ONE + ":" + idProvider.getAndIncrement());
  }

  @Test(expected = NotConnectedException.class)
  public void testGetConnectionByProviderClassWrongProvider() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    connectionRepository.getConnection(
        TestConnectionFactoryTwo.class, providerUserId());
  }

  @Test
  public void testGetPrimaryConnection() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    Connection<?> foundConnection = connectionRepository.getPrimaryConnection(
        TestConnectionFactoryOne.class);
    assertNotNull(foundConnection);
    assertEquals(connection, foundConnection);
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test(expected = NotConnectedException.class)
  public void testGetPrimaryConnectionNotFound() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    connectionRepository.getPrimaryConnection(TestConnectionFactoryTwo.class);
  }

  @Test
  public void testFindPrimaryConnection() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    Connection<?> foundConnection = connectionRepository.findPrimaryConnection(
        TestConnectionFactoryOne.class);
    assertNotNull(foundConnection);
    assertEquals(connection, foundConnection);
    ConnectionData connectionData = connectionDataCaptor.getValue();
    assertConnectionData(user(), connectionData);
  }

  @Test
  public void testFindPrimaryConnectionNotFound() throws Exception {
    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);
    Connection<?> foundConnection = connectionRepository.findPrimaryConnection(
        TestConnectionFactoryTwo.class);
    assertNull(foundConnection);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testAddConnection() throws Exception {
    connectionRepository.addConnection(mock(Connection.class));
  }

  @Test
  public void testUpdateConnection() throws Exception {
    connectionRepository.updateConnection(mock(Connection.class));
    verifyZeroInteractions(userRepository);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testRemoveConnections() throws Exception {
    connectionRepository.removeConnections(PROVIDER_ONE);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testRemoveConnection() throws Exception {
    connectionRepository.removeConnection(new ConnectionKey(PROVIDER_ONE,providerUserId()));
  }

  private static class TestConnectionFactoryOne extends ConnectionFactory {

    public TestConnectionFactoryOne() {
      super(PROVIDER_ONE, null, null);
      throw new RuntimeException("Shouldn't be instantiated");
    }

    @Override
    public Connection createConnection(ConnectionData data) {
      throw new UnsupportedOperationException();
    }
  }

  private static class TestConnectionFactoryTwo extends ConnectionFactory {

    public TestConnectionFactoryTwo() {
      super(PROVIDER_TWO, null, null);
      throw new RuntimeException("Shouldn't be instantiated");
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