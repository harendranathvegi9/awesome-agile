package org.awesomeagile.webapp.controller;

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