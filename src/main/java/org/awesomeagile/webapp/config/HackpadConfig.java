package org.awesomeagile.webapp.config;

import com.google.common.collect.ImmutableMap;

import org.awesomeagile.annotations.Hackpad;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.model.document.PadIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;

import java.util.Map;

import javax.inject.Named;

/**
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

  @Bean
  public OAuthRestTemplate getOtherRestTemplate() {
    BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
    resource.setConsumerKey("123");
    resource.setSharedSecret(new SharedConsumerSecretImpl("234"));
    resource.setAcceptsAuthorizationHeader(false);
    return new OAuthRestTemplate(resource);
  }

  @Bean(name = "defnready")
  public HackpadDocumentTemplate getDefinitionOfReadyTemplate() {
    return new HackpadDocumentTemplate(
        "Definition of ready",
        new PadIdentity(hackpadDefinitonOfReady));
  }
}
