package org.awesomeagile.webapp.config;

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
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/authenticate")
                .failureUrl("/login?param.error=bad_credentials")
                .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
            .authorizeRequests()
                .antMatchers("/api/**")
                    .authenticated()
                .anyRequest()
                    .permitAll()
                .and()
            .rememberMe()
                .and()
            .apply(new SpringSocialConfigurer());
    }

}
