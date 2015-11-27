package org.awesomeagile.webapp.security.google;

import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleAdapter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class GoogleConfigurableConnectionFactory extends OAuth2ConnectionFactory<Google> {

  public GoogleConfigurableConnectionFactory(
      String clientId,
      String clientSecret,
      String baseOAuthUrl,
      String baseApiUrl) {
    super(
        "google",
        new GoogleConfigurableServiceProvider(clientId, clientSecret, baseOAuthUrl, baseApiUrl),
        new GoogleAdapter());
  }

  @Override
  protected String extractProviderUserId(AccessGrant accessGrant) {
    Google api = ((GoogleConfigurableServiceProvider)getServiceProvider())
        .getApi(accessGrant.getAccessToken());
    UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
    return userProfile.getUsername();
  }
}
