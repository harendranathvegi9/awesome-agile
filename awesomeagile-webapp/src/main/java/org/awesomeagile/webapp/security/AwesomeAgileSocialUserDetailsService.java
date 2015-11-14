package org.awesomeagile.webapp.security;

import com.google.common.collect.ImmutableSet;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class AwesomeAgileSocialUserDetailsService implements SocialUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        User user = userRepository.findOneByPrimaryEmail(userId);
        return new AwesomeAgileSocialUserDetails(user, ImmutableSet.<GrantedAuthority>of());
    }

}
