package org.awesomeagile.webapp.test.google;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@EnableWebMvc
@Configuration
public class FakeGoogleOAuthServerConfiguration extends WebMvcConfigurerAdapter {

}
