package org.awesomeagile.webapp.security.google;

import org.awesomeagile.webapp.security.google.social.ConfigurableGoogleTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class GoogleConfigurableServiceProvider extends AbstractOAuth2ServiceProvider<Google> {

  private final String baseApiUrl;

  public GoogleConfigurableServiceProvider(String clientId, String clientSecret,
      String baseOAuthUrl, String baseApiUrl) {
    super(new GoogleConfigurableOAuth2Template(clientId, clientSecret, baseOAuthUrl));
    this.baseApiUrl = baseApiUrl;
  }

  @Override
  public Google getApi(String accessToken) {
    return new ConfigurableGoogleTemplate(accessToken, baseApiUrl);
  }
}
