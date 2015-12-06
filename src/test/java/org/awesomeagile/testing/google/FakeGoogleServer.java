package org.awesomeagile.testing.google;

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

import org.awesomeagile.testutils.NetworkUtils;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.List;

/**
 * A fake server that emulates Google OAuth2 authentication and a limited subset of Google Plus API
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class FakeGoogleServer extends ExternalResource {

  private static final String CONTEXT_PATH = "/g";

  private ConfigurableApplicationContext webApplicationContext;
  @Autowired
  private FakeGoogleController controller;
  private final int port;

  public FakeGoogleServer() {
    try {
      this.port = NetworkUtils.findAvailablePort();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to find a free port", ex);
    }
  }

  public String getEndpoint() {
    return "http://localhost:" + port + CONTEXT_PATH + "/";
  }

  public FakeGoogleServer setClientId(String clientId) {
    controller.setClientId(clientId);
    return this;
  }

  public FakeGoogleServer setClientSecret(String clientSecret) {
    controller.setClientSecret(clientSecret);
    return this;
  }

  public FakeGoogleServer setRedirectUriPrefixes(List<String> redirectUriPrefixes) {
    controller.setRedirectUriPrefixes(redirectUriPrefixes);
    return this;
  }

  public FakeGoogleServer setPerson(Person person) {
    controller.setPerson(person);
    return this;
  }

  @Override
  protected void before() throws Throwable {
    start();
  }

  @Override
  protected void after() {
    stop();
  }

  private void start() {
    try {
      webApplicationContext = SpringApplication.run(
          new Object[] {
              FakeGoogleOAuthServerConfiguration.class,
              PropertyPlaceholderAutoConfiguration.class,
              DispatcherServletAutoConfiguration.class,
              ServerPropertiesAutoConfiguration.class},
          new String[] { "--server.port=" + port });
      webApplicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    } catch (Exception ex) {
      throw new RuntimeException("Unable to start fake Google OAuth server", ex);
    }
  }

  private void stop() {
    try {
      webApplicationContext.close();
    } catch (Exception ex) {
      throw new RuntimeException("Unable to stop fake Google OAuth server", ex);
    }
  }

  @EnableWebMvc
  @ComponentScan(basePackageClasses = FakeGoogleController.class)
  public static class FakeGoogleOAuthServerConfiguration {

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory(
        @Value("${server.port}") int port) {
      return new TomcatEmbeddedServletContainerFactory(CONTEXT_PATH, port);
    }
  }
}
