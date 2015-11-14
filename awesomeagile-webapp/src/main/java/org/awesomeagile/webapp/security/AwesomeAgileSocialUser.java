package org.awesomeagile.webapp.security;

import com.google.common.base.MoreObjects;

import org.awesomeagile.model.team.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

import java.util.Collection;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class AwesomeAgileSocialUser extends SocialUser {

  private final User user;

  public AwesomeAgileSocialUser(User user, Collection<? extends GrantedAuthority> authorities) {
    super(user.getPrimaryEmail(), "", authorities);
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("user", user)
        .toString();
  }
}
