package org.awesomeagile.webapp.security;

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
