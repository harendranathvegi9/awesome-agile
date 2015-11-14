package org.awesomeagile.webapp.config;

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
          .antMatchers("/index.html", "/", "/auth/**", "/images/**", "/css/**", "/js/**", "/node_modules/**")
            .permitAll()
          .anyRequest()
            .authenticated()
          .and()
        .rememberMe()
          .and()
        .apply(new SpringSocialConfigurer());
  }

}
