package org.awesomeagile.webapp.controller;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableSet;

import org.awesomeagile.model.team.User;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.awesomeagile.webapp.security.SocialTestUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class UserControllerTest {

  private UserController userController;

  @Before
  public void setUp() throws Exception {
    userController = new UserController();
  }

  @Test
  public void testGetCurrentUser() throws Exception {
    AwesomeAgileSocialUser socialUser = new AwesomeAgileSocialUser(
        SocialTestUtils.USER_ONE, ImmutableSet.of());
    User currentUser = userController.getCurrentUser(socialUser);
    assertEquals(SocialTestUtils.USER_ONE, currentUser);
  }
}