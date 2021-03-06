package org.awesomeagile.model.team;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.awesomeagile.model.AbstractAuditable;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

import com.google.common.base.MoreObjects;

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

  @NotNull
  @Column(nullable = false, updatable = false)
  private String authProviderId;

  @NotNull
  @Column(nullable = false, updatable = false)
  private String authProviderUserId;

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
    this.authProviderId = other.authProviderId;
    this.authProviderUserId = other.authProviderUserId;
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

  /**
   * Returns an identifier of an external authentication provider that can authenticate this user.
   *
   * @return identifier of an external authentication provider
   */
  public String getAuthProviderId() {
    return authProviderId;
  }

  public User setAuthProviderId(String authProviderId) {
    this.authProviderId = authProviderId;
    return this;
  }

  /**
   * Returns this user's unique identifier within the scope of the external authentication provider
   *
   * @return user's unique identifier within the scope of the external authentication provider.
   */
  public String getAuthProviderUserId() {
    return authProviderUserId;
  }

  public User setAuthProviderUserId(String authProviderUserId) {
    this.authProviderUserId = authProviderUserId;
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
        && Objects.equals(status, user.status)
        && Objects.equals(authProviderId, user.authProviderId)
        && Objects.equals(authProviderUserId, user.authProviderUserId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), primaryEmail, displayName, avatar, isVisible, status,
        authProviderId, authProviderUserId);
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
        .add("authProviderId", authProviderId)
        .add("authProviderUserId", authProviderUserId)
        .toString();
  }
}
