package org.awesomeagile.webapp.controller;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadPostTest {
  public static final String CLIENT_ID = "FiE4R5tmkef";
  public static final String SECRET = "eAvjusoCwiM4jG2yL3lUWZ8C7n3IOWT8";
  public static final String CREATE_URL = "https://hackpad.com/api/1.0/pad/create";
  public static final String UPDATE_URL = "https://hackpad.com/api/1.0/pad/{padId}/content";
  public static final String GET_URL = "https://hackpad.com/api/1.0/pad/{padId}/content/latest.html";

  @Test
  public void testCreatePage() throws Exception {
    BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
    resource.setConsumerKey(CLIENT_ID);
    resource.setSharedSecret(new SharedConsumerSecretImpl(SECRET));
//    resource.setAdditionalRequestHeaders(ImmutableMap.of(
//        "email", "belov.stan@gmail.com"
//    ));
    resource.setAcceptsAuthorizationHeader(false);
    OAuthRestTemplate restTemplate = new OAuthRestTemplate(resource);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);

    HttpEntity<String> entity = new HttpEntity<>("Hackpad Title", headers);
    PadIdentity padIdentity = restTemplate.postForObject(CREATE_URL, entity, PadIdentity.class);
    assertNotNull(padIdentity);
    assertFalse(isNullOrEmpty(padIdentity.getPadId()));
    System.out.println(padIdentity);

    headers.setContentType(MediaType.TEXT_HTML);
    HttpEntity<String> updateEntity =
        new HttpEntity<>("<html><body><p><b>Subheader</b></p><p>Regular text</p></body></html>",
            headers);
    HackpadStatus status = restTemplate.postForObject(UPDATE_URL, updateEntity, HackpadStatus.class,
        ImmutableMap.of("padId", padIdentity.getPadId()));
    assertNotNull(status);
    assertTrue(status.isSuccess());
    System.out.println(status);
    String padContent = restTemplate
        .getForObject(GET_URL, String.class, ImmutableMap.of("padId", padIdentity.getPadId()));
    System.out.println(padContent);

    updateEntity =
        new HttpEntity<>(padContent,
            headers);
    status = restTemplate.postForObject(UPDATE_URL, updateEntity, HackpadStatus.class,
        ImmutableMap.of("padId", padIdentity.getPadId()));
    assertNotNull(status);
    assertTrue(status.isSuccess());
//    String response = restTemplate.getForObject(OPTIONS_URL, String.class,
//        ImmutableMap.of(
//            "padId", padIdentity.getPadId()));
//    System.out.println(response);
  }

  private static class HackpadStatus {
    @JsonProperty
    private boolean success;

    public boolean isSuccess() {
      return success;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      HackpadStatus that = (HackpadStatus) o;
      return Objects.equals(success, that.success);
    }

    @Override
    public int hashCode() {
      return Objects.hash(success);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("success", success)
          .toString();
    }
  }

  private static class PadIdentity {

    @JsonProperty
    private String padId;

    public String getPadId() {
      return padId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PadIdentity that = (PadIdentity) o;
      return Objects.equals(padId, that.padId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(padId);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("padId", padId)
          .toString();
    }
  }

}