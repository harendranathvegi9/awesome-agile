package org.awesomeagile.model.team;

import com.google.common.base.MoreObjects;

import org.awesomeagile.model.AbstractAuditable;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Entity
@Table(name = "user", schema = "teams")
@SequenceGenerator(name = "idgen", sequenceName = "teams.user_id_seq")
@EntityListeners(AuditingEntityListener.class)
public class User extends AbstractAuditable<Long> {

  @NotEmpty
  @Column(unique = true, nullable = false, updatable = false)
  private String primaryEmail;

  @NotEmpty
  @Column(nullable = false)
  private String displayName;

  @Column(nullable = false)
  private String avatar;

  @NotNull
  @Column(nullable = false)
  private boolean isVisible;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  public User() {
  }

  /**
   * Copy constructor
   *
   * @param other user object to copy fields from
   */
  public User(User other) {
    this.setId(other.getId());
    this.setCreatedDate(other.getCreatedDate());
    this.setLastModifiedDate(other.getLastModifiedDate());
    this.primaryEmail = other.primaryEmail;
    this.displayName = other.displayName;
    this.avatar = other.avatar;
    this.isVisible = other.isVisible;
    this.status = other.status;
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
    return Objects.equals(getId(), user.getId())
        && Objects.equals(isVisible, user.isVisible)
        && Objects.equals(primaryEmail, user.primaryEmail)
        && Objects.equals(displayName, user.displayName)
        && Objects.equals(avatar, user.avatar)
        && Objects.equals(status, user.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), primaryEmail, displayName, avatar, isVisible, status);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("primaryEmail", primaryEmail)
        .add("displayName", displayName)
        .add("avatar", avatar)
        .add("isVisible", isVisible)
        .add("isVisible", isVisible)
        .add("status", status)
        .toString();
  }
}
