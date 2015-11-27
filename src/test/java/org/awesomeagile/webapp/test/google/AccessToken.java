package org.awesomeagile.webapp.test.google;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AccessToken {

  @JsonProperty("access_token")
  private final String accessToken;

  @JsonProperty("scope")
  private final String scope;

  @JsonProperty("refresh_token")
  private final String refreshToken;

  @JsonProperty("expires_in")
  private final long expiresIn;

  public AccessToken(String accessToken, String scope, String refreshToken, long expiresIn) {
    this.accessToken = accessToken;
    this.scope = scope;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getScope() {
    return scope;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccessToken accessToken = (AccessToken) o;
    return Objects.equals(expiresIn, accessToken.expiresIn)
        && Objects.equals(this.accessToken, accessToken.accessToken)
        && Objects.equals(scope, accessToken.scope)
        && Objects.equals(refreshToken, accessToken.refreshToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, scope, refreshToken, expiresIn);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("accessToken", accessToken)
        .add("scope", scope)
        .add("refreshToken", refreshToken)
        .add("expiresIn", expiresIn)
        .toString();
  }
}
