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

  private final UserRepository userRepository;

  @Autowired
  public AwesomeAgileSocialUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public SocialUserDetails loadUserByUserId(String userId)
      throws UsernameNotFoundException, DataAccessException {
    User user = userRepository.findOneByPrimaryEmail(userId);
    if (user == null) {
      throw new UsernameNotFoundException(String.format("User with ID %s was not found", userId));
    }
    return new AwesomeAgileSocialUser(user, ImmutableSet.<GrantedAuthority>of());
  }

}
