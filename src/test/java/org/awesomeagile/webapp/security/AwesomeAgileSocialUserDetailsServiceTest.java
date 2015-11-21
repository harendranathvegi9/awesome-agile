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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.awesomeagile.dao.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileSocialUserDetailsServiceTest {

  private UserRepository userRepository;
  private SocialUserDetailsService service;

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    service = new AwesomeAgileSocialUserDetailsService(userRepository);
  }

  @Test
  public void testLoadUserByUserIdFound() throws Exception {
    when(userRepository.findOneByPrimaryEmail(SocialTestUtils.USER_EMAIL_TWO)).
        thenReturn(SocialTestUtils.USER_TWO);
    SocialUserDetails socialUserDetails =
        service.loadUserByUserId(SocialTestUtils.USER_EMAIL_TWO);
    assertNotNull(socialUserDetails);
    assertTrue(socialUserDetails instanceof AwesomeAgileSocialUser);
    assertEquals(SocialTestUtils.USER_TWO,
        ((AwesomeAgileSocialUser)socialUserDetails).getUser());
    assertEquals(SocialTestUtils.USER_EMAIL_TWO, socialUserDetails.getUserId());
    assertEquals("", socialUserDetails.getPassword());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testLoadUserByUserNotFound() throws Exception {
    when(userRepository.findOneByPrimaryEmail(SocialTestUtils.USER_EMAIL_TWO)).
        thenReturn(SocialTestUtils.USER_TWO);
    service.loadUserByUserId(SocialTestUtils.USER_EMAIL_ONE);
  }
}
