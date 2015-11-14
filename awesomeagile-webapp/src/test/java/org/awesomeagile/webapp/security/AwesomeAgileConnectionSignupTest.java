package org.awesomeagile.webapp.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfileBuilder;

import static org.awesomeagile.webapp.security.SocialTestUtils.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionSignupTest {

  private ConnectionSignUp connectionSignUp;
  private UserRepository userRepository;

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    connectionSignUp = new AwesomeAgileConnectionSignup(userRepository);
  }

  @Test
  public void testExecute() throws Exception {
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
    connectionSignUp.execute(connection);
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
}