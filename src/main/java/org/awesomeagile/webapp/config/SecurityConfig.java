package org.awesomeagile.webapp.config;

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

import org.awesomeagile.webapp.security.AwesomeAgileAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UsersConnectionRepository usersConnectionRepository;

  @Autowired
  private SocialUserDetailsService socialUserDetailsService;

  @Bean
  public AuthenticationProvider getAuthenticationProvider() {
    return new SocialAuthenticationProvider(usersConnectionRepository, socialUserDetailsService);
  }

  public void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic()
          .authenticationEntryPoint(new AwesomeAgileAuthenticationEntryPoint())
          .and()
        .logout()
          // TODO signout isn't currently handled
          .logoutUrl("/signout")
          .deleteCookies("JSESSIONID")
          .and()
        .authorizeRequests()
          .antMatchers("/index.html", "/", "/auth/**", "/info", "/health",
              "/images/**", "/css/**", "/js/**", "/node_modules/**")
            .permitAll()
          .anyRequest()
            .authenticated()
          .and()
        .rememberMe()
          .and()
        .apply(new SpringSocialConfigurer());
  }

}
