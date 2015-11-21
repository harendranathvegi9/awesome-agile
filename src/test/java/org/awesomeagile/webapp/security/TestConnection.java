package org.awesomeagile.webapp.security;

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

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;

/**
 * A test version of {@link org.springframework.social.connect.Connection}
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class TestConnection<A> implements Connection<A> {

  private final ConnectionKey key;
  private String displayName;
  private String profileUrl;
  private String imageUrl;
  private UserProfile userProfile;

  public TestConnection(ConnectionKey key) {
    this.key = key;
  }

  @Override
  public ConnectionKey getKey() {
    return key;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getProfileUrl() {
    return profileUrl;
  }

  @Override
  public String getImageUrl() {
    return imageUrl;
  }

  public TestConnection setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public TestConnection setProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
    return this;
  }

  public TestConnection setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  public TestConnection setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
    return this;
  }

  @Override
  public void sync() {

  }

  @Override
  public boolean test() {
    return true;
  }

  @Override
  public boolean hasExpired() {
    return false;
  }

  @Override
  public void refresh() {

  }

  @Override
  public UserProfile fetchUserProfile() {
    return userProfile;
  }

  @Override
  public void updateStatus(String message) {

  }

  @Override
  public A getApi() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ConnectionData createData() {
    return null;
  }
}
