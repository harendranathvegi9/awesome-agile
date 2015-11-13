package org.awesomeagile.webapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AwesomeAgileUserDetails extends User {

    public AwesomeAgileUserDetails(String username, String password) {
        super(username, password, null);
    }

    public AwesomeAgileUserDetails(String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AwesomeAgileUserDetails(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
    }

}
