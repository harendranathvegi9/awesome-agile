package org.awesomeagile.webapp.security.google.social;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.PlusOperations;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class ConfigurableGoogleTemplate extends GoogleTemplate {

  private final String baseApiUrl;
  private PlusOperations plusOperations;


  public ConfigurableGoogleTemplate(String baseApiUrl) {
    this.baseApiUrl = baseApiUrl;
    initializeOperations();
  }

  public ConfigurableGoogleTemplate(String accessToken, String baseApiUrl) {
    super(accessToken);
    this.baseApiUrl = baseApiUrl;
    initializeOperations();
  }

  private void initializeOperations() {
    this.plusOperations = new ConfigurablePlusTemplate(
        getRestTemplate(), isAuthorized(), baseApiUrl);
  }

  @Override
  public PlusOperations plusOperations() {
    return plusOperations;
  }
}
