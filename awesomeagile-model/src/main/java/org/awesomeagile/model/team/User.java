package org.awesomeagile.model.team;

import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class User {

  private int userId;
  private String primaryEmail;
  private String displayName;
  private String avatar;
  private boolean isVisible;
  private UserStatus status;
  private Date signupDate;

  public User() {
  }

  /**
   * Copy constructor
   *
   * @param other user object to copy fields from
   */
  public User(User other) {
    this.userId = other.userId;
    this.primaryEmail = other.primaryEmail;
    this.displayName = other.displayName;
    this.avatar = other.avatar;
    this.isVisible = other.isVisible;
    this.status = other.status;
    this.signupDate = other.signupDate;
  }

  public Date getSignupDate() {
    return signupDate;
  }

  public User setSignupDate(Date signupDate) {
    this.signupDate = signupDate;
    return this;
  }

  public UserStatus getStatus() {
    return status;
  }

  public User setStatus(UserStatus status) {
    this.status = status;
    return this;
  }

  public String getAvatar() {
    return avatar;
  }

  public User setAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public User setIsVisible(boolean isVisible) {
    this.isVisible = isVisible;
    return this;
  }

  public int getUserId() {
    return userId;
  }

  public User setUserId(int userId) {
    this.userId = userId;
    return this;
  }

  public String getPrimaryEmail() {
    return primaryEmail;
  }

  public User setPrimaryEmail(String primaryEmail) {
    this.primaryEmail = primaryEmail;
    return this;
  }

  public String getDisplayName() {
    return displayName;
  }

  public User setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(userId, user.userId)
        && Objects.equals(isVisible, user.isVisible)
        && Objects.equals(primaryEmail, user.primaryEmail)
        && Objects.equals(displayName, user.displayName)
        && Objects.equals(avatar, user.avatar)
        && Objects.equals(status, user.status)
        && Objects.equals(signupDate, user.signupDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, primaryEmail, displayName, avatar, isVisible, status, signupDate);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("userId", userId)
        .add("primaryEmail", primaryEmail)
        .add("displayName", displayName)
        .add("avatar", avatar)
        .add("isVisible", isVisible)
        .add("status", status)
        .add("signupDate", signupDate)
        .toString();
  }
}
