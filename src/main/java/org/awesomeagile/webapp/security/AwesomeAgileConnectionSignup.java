package org.awesomeagile.webapp.security;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileConnectionSignup implements ConnectionSignUp {

  private final UserRepository userRepository;

  public AwesomeAgileConnectionSignup(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public String execute(Connection<?> connection) {
    User userFromProfile = createUserFromProfile(connection);
    userRepository.save(userFromProfile);
    return userFromProfile.getPrimaryEmail();
  }

  private User createUserFromProfile(Connection<?> connection) {
    UserProfile profile = connection.fetchUserProfile();
    ConnectionKey key = connection.getKey();
    return new User()
        .setPrimaryEmail(profile.getEmail())
        .setDisplayName(connection.getDisplayName())
        .setIsVisible(true)
        .setStatus(UserStatus.ACTIVE)
        .setAvatar(connection.getImageUrl())
        .setAuthProviderId(key.getProviderId())
        .setAuthProviderUserId(key.getProviderUserId());
  }
}
