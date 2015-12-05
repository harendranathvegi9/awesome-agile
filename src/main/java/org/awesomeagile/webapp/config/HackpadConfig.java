package org.awesomeagile.webapp.config;

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

import org.awesomeagile.annotations.Hackpad;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.model.document.PadIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;

/**
 * AwesomeAgile configuration for creating Hackpad documents
 * @author sbelov@google.com (Stan Belov)
 */
@Configuration
public class HackpadConfig {

  @Value("${hackpad.client.id}")
  private String hackpadClientId;

  @Value("${hackpad.client.secret}")
  private String hackpadClientSecret;

  @Value("${hackpad.url}")
  private String hackpadBaseUrl;

  @Value("${hackpad.templates.defnready}")
  private String hackpadDefinitonOfReady;

  @Hackpad
  @Bean
  public String getHackpadBaseUrl() {
    return hackpadBaseUrl;
  }

  @Hackpad
  @Bean
  public OAuthRestTemplate getHackpadRestTemplate() {
    BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
    resource.setConsumerKey(hackpadClientId);
    resource.setSharedSecret(new SharedConsumerSecretImpl(hackpadClientSecret));
    resource.setAcceptsAuthorizationHeader(false);
    return new OAuthRestTemplate(resource);
  }

  /**
   * A Document template for definition of ready, that refers to an existing
   * Hackpad document
   * @return An instance of HackpadDocumentTemplate that is available as a 
   *    document type for createHackpad API calls from clients
   */
  @Bean(name = "defnready")
  public HackpadDocumentTemplate getDefinitionOfReadyTemplate() {
    return new HackpadDocumentTemplate(
        "Definition of Ready",
        new PadIdentity(hackpadDefinitonOfReady));
  }
}
