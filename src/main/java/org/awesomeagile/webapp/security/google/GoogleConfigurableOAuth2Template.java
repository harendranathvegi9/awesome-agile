package org.awesomeagile.webapp.security.google;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * Copyright (C) 2011 Gabriel Axel
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
    setUseParametersForClientAuthentication(true);
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
