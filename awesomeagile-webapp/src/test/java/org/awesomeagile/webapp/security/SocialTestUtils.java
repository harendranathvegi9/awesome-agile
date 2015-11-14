package org.awesomeagile.webapp.security;

import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.social.connect.ConnectionKey;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class SocialTestUtils {

  static final String FACEBOOK = "facebook";
  static final String PROVIDER_USER_ID_ONE = "sbelov2015";
  static final String PROVIDER_USER_ID_TWO = "peterson2013";
  static final String USER_EMAIL_ONE = "belov.stan@gmail.com";
  static final String DISPLAY_NAME = "stan nyc";
  static final String IMAGE_URL = "http://static.facebook.com/image.jpg";
  static final User USER_ONE = new User()
      .setPrimaryEmail(USER_EMAIL_ONE)
      .setAuthProviderId(FACEBOOK)
      .setAuthProviderUserId(PROVIDER_USER_ID_ONE)
      .setAvatar(IMAGE_URL)
      .setDisplayName(DISPLAY_NAME)
      .setIsVisible(true)
      .setStatus(UserStatus.ACTIVE);
  static final String USER_EMAIL_TWO = "workinghard@gmail.com";
  static final User USER_TWO = new User()
      .setPrimaryEmail(USER_EMAIL_TWO)
      .setAuthProviderId(FACEBOOK)
      .setAuthProviderUserId(PROVIDER_USER_ID_TWO)
      .setAvatar("http://static.facebook.com/photo.png")
      .setDisplayName("dcp")
      .setIsVisible(true)
      .setStatus(UserStatus.ACTIVE);

  static ConnectionKey key(String providerId, String providerUserId) {
    return new ConnectionKey(providerId, providerUserId);
  }
}
