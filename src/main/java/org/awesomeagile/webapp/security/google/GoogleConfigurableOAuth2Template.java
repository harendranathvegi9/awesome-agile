package org.awesomeagile.webapp.security.google;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class GoogleConfigurableOAuth2Template extends OAuth2Template {

  private static final String AUTHORIZE_URL = "oauth2/auth";
  private static final String TOKEN_URL = "oauth2/token";
  private static final String SEPARATOR = "/";

  public GoogleConfigurableOAuth2Template(String clientId, String clientSecret, String baseUrl) {
    super(
        clientId,
        clientSecret,
        end(baseUrl, AUTHORIZE_URL),
        end(baseUrl, TOKEN_URL));
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected AccessGrant postForAccessGrant(
      String accessTokenUrl,
      MultiValueMap<String, String> parameters) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<MultiValueMap<String, String>> requestEntity =
        new HttpEntity<MultiValueMap<String, String>>(parameters, headers);
    ResponseEntity<Map> responseEntity = getRestTemplate().exchange(
        accessTokenUrl, HttpMethod.POST, requestEntity, Map.class);
    Map<String, Object> responseMap = responseEntity.getBody();
    return extractAccessGrant(responseMap);
  }

  private AccessGrant extractAccessGrant(Map<String, Object> result) {
    String accessToken = (String) result.get("access_token");
    String scope = (String) result.get("scope");
    String refreshToken = (String) result.get("refresh_token");

    // result.get("expires_in") may be an Integer, so cast it to Number first.
    Number expiresInNumber = (Number) result.get("expires_in");
    Long expiresIn = (expiresInNumber == null) ? null : expiresInNumber.longValue();
    return createAccessGrant(accessToken, scope, refreshToken, expiresIn, result);
  }

  private static String end(String base, String path) {
    if (!base.endsWith(SEPARATOR)) {
      base += SEPARATOR;
    }
    return base + path;
  }
}
