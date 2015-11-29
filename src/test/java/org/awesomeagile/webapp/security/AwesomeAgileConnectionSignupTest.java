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

import static org.awesomeagile.webapp.security.SocialTestUtils.DISPLAY_NAME;
import static org.awesomeagile.webapp.security.SocialTestUtils.FACEBOOK;
import static org.awesomeagile.webapp.security.SocialTestUtils.IMAGE_URL;
import static org.awesomeagile.webapp.security.SocialTestUtils.PROVIDER_USER_ID_ONE;
import static org.awesomeagile.webapp.security.SocialTestUtils.USER_EMAIL_ONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfileBuilder;

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
