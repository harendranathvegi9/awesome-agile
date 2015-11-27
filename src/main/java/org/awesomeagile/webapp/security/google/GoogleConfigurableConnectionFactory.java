package org.awesomeagile.webapp.security.google;

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
