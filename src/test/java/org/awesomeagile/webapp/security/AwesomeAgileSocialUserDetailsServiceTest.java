package org.awesomeagile.webapp.security;

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