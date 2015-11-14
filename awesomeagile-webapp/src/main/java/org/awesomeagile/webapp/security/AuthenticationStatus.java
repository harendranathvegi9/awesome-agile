package org.awesomeagile.webapp.security;

import com.google.common.base.MoreObjects;

import org.awesomeagile.model.team.User;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AuthenticationStatus {

  private final boolean authenticated;
  private final User user;

  public AuthenticationStatus(boolean authenticated, User user) {
    this.authenticated = authenticated;
    this.user = user;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  public User getUser() {
    return user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationStatus that = (AuthenticationStatus) o;
    return Objects.equals(authenticated, that.authenticated)
        && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authenticated, user);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("authenticated", authenticated)
        .add("user", user)
        .toString();
  }
}
