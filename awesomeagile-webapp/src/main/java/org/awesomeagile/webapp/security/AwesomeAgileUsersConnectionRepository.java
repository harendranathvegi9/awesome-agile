package org.awesomeagile.webapp.security;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;

public class AwesomeAgileUsersConnectionRepository implements UsersConnectionRepository {

  private static final Function<User, String> USER_EMAIL = new Function<User, String>() {
    @Override
    public String apply(User user) {
      return user.getPrimaryEmail();
    }
  };
  private final UserRepository userRepository;
  private final ConnectionFactoryLocator connectionFactoryLocator;

  @Autowired
  public AwesomeAgileUsersConnectionRepository(UserRepository userRepository,
      ConnectionFactoryLocator connectionFactoryLocator) {
    this.userRepository = userRepository;
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  @Override
  public List<String> findUserIdsWithConnection(Connection<?> connection) {
    ConnectionKey key = connection.getKey();
    User user = userRepository.findOneByAuthProviderUserId(
        key.getProviderId(),
        key.getProviderUserId());
    if (user == null) {
      // create a new one
      user = createUserFromProfile(connection);
      userRepository.save(user);
    }
    return ImmutableList.of(user.getPrimaryEmail());
  }

  @Override
  public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
    return FluentIterable
        .from(userRepository.findOneByAuthProviderUserIds(providerId, providerUserIds))
        .transform(USER_EMAIL).toSet();
  }

  @Override
  public ConnectionRepository createConnectionRepository(String userId) {
    User user = userRepository.findOneByPrimaryEmail(userId);
    return new AwesomeAgileConnectionRepository(
        userRepository,
        user.getId(),
        connectionFactoryLocator);
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
