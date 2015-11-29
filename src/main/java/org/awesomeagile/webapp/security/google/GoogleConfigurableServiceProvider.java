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
