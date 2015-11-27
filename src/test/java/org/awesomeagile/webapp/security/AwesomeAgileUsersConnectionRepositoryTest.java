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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileUsersConnectionRepositoryTest {

  private UsersConnectionRepository usersConnectionRepository;
  private UserRepository userRepository;
  private ConnectionSignUp connectionSignUp;
  private ConnectionFactoryLocator connectionFactoryLocator;
  private AtomicLong idProvider = new AtomicLong(1);

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    connectionFactoryLocator = mock(ConnectionFactoryLocator.class);
    connectionSignUp = mock(ConnectionSignUp.class);
    usersConnectionRepository = new AwesomeAgileUsersConnectionRepository(
        userRepository, connectionFactoryLocator, connectionSignUp);
  }

  @Test
  public void testFindUserIdsWithConnectionNewUser() throws Exception {
    TestConnection connection = new TestConnection(
        SocialTestUtils.key(SocialTestUtils.FACEBOOK, SocialTestUtils.PROVIDER_USER_ID_ONE));
    connection
        .setDisplayName(SocialTestUtils.DISPLAY_NAME)
        .setImageUrl(SocialTestUtils.IMAGE_URL)
        .setProfileUrl("http://www.facebook.com/sbelov2015")
        .setUserProfile(new UserProfileBuilder()
            .setEmail(SocialTestUtils.USER_EMAIL_ONE)
            .setFirstName("Stan")
            .setLastName("Belov")
            .setName("Stan Belov")
            .setUsername(SocialTestUtils.PROVIDER_USER_ID_ONE)
            .build());
    when(connectionSignUp.execute(connection)).thenReturn(SocialTestUtils.USER_EMAIL_ONE);
    List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
    assertEquals(ImmutableList.of(SocialTestUtils.USER_EMAIL_ONE), ImmutableList.copyOf(userIds));
    verify(connectionSignUp).execute(connection);
  }

  @Test
  public void testFindUserIdsWithConnectionExistingUser() throws Exception {
    TestConnection connection = new TestConnection(
        SocialTestUtils.key(SocialTestUtils.FACEBOOK, SocialTestUtils.PROVIDER_USER_ID_ONE));
    connection
        .setDisplayName(SocialTestUtils.DISPLAY_NAME)
        .setImageUrl(SocialTestUtils.IMAGE_URL)
        .setProfileUrl("http://www.facebook.com/sbelov2015")
        .setUserProfile(new UserProfileBuilder()
            .setEmail("otheremail@gmail.com")
            .setFirstName("Stan")
            .setLastName("Belov")
            .setName("Stan Belov")
            .setUsername(SocialTestUtils.PROVIDER_USER_ID_ONE)
            .build());
    when(userRepository.findOneByAuthProviderUserId(
        SocialTestUtils.FACEBOOK, SocialTestUtils.PROVIDER_USER_ID_ONE))
        .thenReturn(SocialTestUtils.USER_ONE);
    List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
    verify(userRepository, never()).save(any(User.class));
    assertEquals(ImmutableList.of(SocialTestUtils.USER_EMAIL_ONE), ImmutableList.copyOf(userIds));
  }

  @Test
  public void testFindUserIdsConnectedToMultipleUsers() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_ONE, SocialTestUtils.PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.of(SocialTestUtils.USER_ONE, SocialTestUtils.USER_TWO));
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_ONE, SocialTestUtils.PROVIDER_USER_ID_TWO));
    assertEquals(ImmutableSet.of(SocialTestUtils.USER_EMAIL_ONE, SocialTestUtils.USER_EMAIL_TWO),
        userIds);
  }

  @Test
  public void testFindUserIdsConnectedToSingleUser() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.of(SocialTestUtils.USER_TWO));
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_TWO));
    assertEquals(ImmutableSet.of(SocialTestUtils.USER_EMAIL_TWO),
        userIds);
  }

  @Test
  public void testFindUserIdsConnectedToSingleUserNotFound() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.<User>of());
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        SocialTestUtils.FACEBOOK,
        ImmutableSet.of(SocialTestUtils.PROVIDER_USER_ID_TWO));
    assertTrue(userIds.isEmpty());
  }

  @Test
  public void testFindUserIdsConnectedToSingleUserEmptyRequest() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(SocialTestUtils.FACEBOOK, ImmutableSet.<String>of()))
        .thenReturn(ImmutableSet.<User>of());
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        SocialTestUtils.FACEBOOK, ImmutableSet.<String>of());
    assertTrue(userIds.isEmpty());
  }

  @Test
  public void testCreateConnectionRepository() throws Exception {
    User user = new User(SocialTestUtils.USER_ONE);
    user.setId(idProvider.getAndIncrement());
    when(userRepository.findOneByPrimaryEmail(SocialTestUtils.USER_EMAIL_ONE))
        .thenReturn(user);

    Connection connection = mock(Connection.class);
    ArgumentCaptor<ConnectionData> connectionDataCaptor =
        ArgumentCaptor.forClass(ConnectionData.class);
    ConnectionFactory connectionFactoryOne = mock(ConnectionFactory.class);
    when(connectionFactoryLocator.getConnectionFactory(user.getAuthProviderId()))
        .thenReturn(connectionFactoryOne);
    when(connectionFactoryOne.createConnection(connectionDataCaptor.capture()))
        .thenReturn(connection);

    ConnectionRepository connectionRepository = usersConnectionRepository
        .createConnectionRepository(SocialTestUtils.USER_EMAIL_ONE);
    assertTrue(connectionRepository instanceof AwesomeAgileConnectionRepository);
  }
}
