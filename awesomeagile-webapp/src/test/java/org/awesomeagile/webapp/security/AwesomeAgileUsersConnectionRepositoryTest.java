package org.awesomeagile.webapp.security;

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
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileUsersConnectionRepositoryTest {

  private static final String FACEBOOK = "facebook";
  private static final String PROVIDER_USER_ID_ONE = "sbelov2015";
  private static final String PROVIDER_USER_ID_TWO = "peterson2013";
  private static final String USER_EMAIL_ONE = "belov.stan@gmail.com";
  private static final String DISPLAY_NAME = "stan nyc";
  private static final String IMAGE_URL = "http://static.facebook.com/image.jpg";
  private static final User USER_ONE = new User()
      .setPrimaryEmail(USER_EMAIL_ONE)
      .setAuthProviderId(FACEBOOK)
      .setAuthProviderUserId(PROVIDER_USER_ID_ONE)
      .setAvatar(IMAGE_URL)
      .setDisplayName(DISPLAY_NAME)
      .setIsVisible(true)
      .setStatus(UserStatus.ACTIVE);
  private static final String USER_EMAIL_TWO = "workinghard@gmail.com";
  private static final User USER_TWO = new User()
      .setPrimaryEmail(USER_EMAIL_TWO)
      .setAuthProviderId(FACEBOOK)
      .setAuthProviderUserId(PROVIDER_USER_ID_TWO)
      .setAvatar("http://static.facebook.com/photo.png")
      .setDisplayName("dcp")
      .setIsVisible(true)
      .setStatus(UserStatus.ACTIVE);
  private UsersConnectionRepository usersConnectionRepository;
  private UserRepository userRepository;
  private ConnectionFactoryLocator connectionFactoryLocator;

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    connectionFactoryLocator = mock(ConnectionFactoryLocator.class);
    usersConnectionRepository = new AwesomeAgileUsersConnectionRepository(
        userRepository, connectionFactoryLocator);
  }

  @Test
  public void testFindUserIdsWithConnectionNewUser() throws Exception {
    TestConnection connection = new TestConnection(key(FACEBOOK, PROVIDER_USER_ID_ONE));
    connection
        .setDisplayName(DISPLAY_NAME)
        .setImageUrl(IMAGE_URL)
        .setProfileUrl("http://www.facebook.com/sbelov2015")
        .setUserProfile(new UserProfileBuilder()
            .setEmail(USER_EMAIL_ONE)
            .setFirstName("Stan")
            .setLastName("Belov")
            .setName("Stan Belov")
            .setUsername(PROVIDER_USER_ID_ONE)
            .build());
    List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
    assertEquals(ImmutableList.of("belov.stan@gmail.com"), ImmutableList.copyOf(userIds));
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User captured = userCaptor.getValue();
    assertEquals(FACEBOOK, captured.getAuthProviderId());
    assertEquals(PROVIDER_USER_ID_ONE, captured.getAuthProviderUserId());
    assertEquals(USER_EMAIL_ONE, captured.getPrimaryEmail());
    assertEquals(DISPLAY_NAME, captured.getDisplayName());
    assertEquals(UserStatus.ACTIVE, captured.getStatus());
    assertEquals(IMAGE_URL, captured.getAvatar());
    assertTrue(captured.isVisible());
  }

  @Test
  public void testFindUserIdsWithConnectionExistingUser() throws Exception {
    TestConnection connection = new TestConnection(key(FACEBOOK, PROVIDER_USER_ID_ONE));
    connection
        .setDisplayName(DISPLAY_NAME)
        .setImageUrl(IMAGE_URL)
        .setProfileUrl("http://www.facebook.com/sbelov2015")
        .setUserProfile(new UserProfileBuilder()
            .setEmail("otheremail@gmail.com")
            .setFirstName("Stan")
            .setLastName("Belov")
            .setName("Stan Belov")
            .setUsername(PROVIDER_USER_ID_ONE)
            .build());
    when(userRepository.findOneByAuthProviderUserId(FACEBOOK, PROVIDER_USER_ID_ONE))
        .thenReturn(USER_ONE);
    List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
    verify(userRepository, never()).save(any(User.class));
    assertEquals(ImmutableList.of(USER_EMAIL_ONE), ImmutableList.copyOf(userIds));
  }

  @Test
  public void testFindUserIdsConnectedToMultipleUsers() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_ONE, PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.of(USER_ONE, USER_TWO));
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_ONE, PROVIDER_USER_ID_TWO));
    assertEquals(ImmutableSet.of(USER_EMAIL_ONE, USER_EMAIL_TWO),
        userIds);
  }

  @Test
  public void testFindUserIdsConnectedToSingleUser() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.of(USER_TWO));
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_TWO));
    assertEquals(ImmutableSet.of(USER_EMAIL_TWO),
        userIds);
  }

  @Test
  public void testFindUserIdsConnectedToSingleUserNotFound() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_TWO)))
        .thenReturn(ImmutableSet.<User>of());
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        FACEBOOK,
        ImmutableSet.of(PROVIDER_USER_ID_TWO));
    assertTrue(userIds.isEmpty());
  }

  @Test
  public void testFindUserIdsConnectedToSingleUserEmptyRequest() throws Exception {
    when(userRepository.findOneByAuthProviderUserIds(FACEBOOK, ImmutableSet.<String>of()))
        .thenReturn(ImmutableSet.<User>of());
    Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
        FACEBOOK, ImmutableSet.<String>of());
    assertTrue(userIds.isEmpty());
  }

  private static ConnectionKey key(String providerId, String providerUserId) {
    return new ConnectionKey(providerId, providerUserId);
  }
}