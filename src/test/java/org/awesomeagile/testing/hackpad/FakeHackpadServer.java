package org.awesomeagile.testing.hackpad;

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

import org.apache.commons.lang3.RandomStringUtils;
import org.awesomeagile.integrations.hackpad.HackpadStatus;
import org.awesomeagile.integrations.hackpad.PadIdentity;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class FakeHackpadServer extends ExternalResource {

  private static final String CONTEXT_PATH = "";

  private ConfigurableApplicationContext webApplicationContext;
  @Autowired
  private FakeHackpadController controller;
  private final int port;

  public FakeHackpadServer() {
    try {
      this.port = NetworkUtils.findAvailablePort();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to find a free port", ex);
    }
  }

  public String getEndpoint() {
    return "http://localhost:" + port + CONTEXT_PATH + "/";
  }

  public FakeHackpadServer setClientId(String clientId) {
    controller.setClientId(clientId);
    return this;
  }

  public FakeHackpadServer setClientSecret(String clientSecret) {
    controller.setClientSecret(clientSecret);
    return this;
  }

  public FakeHackpadServer addHackpad(PadIdentity padIdentity, String content) {
    controller.getHackpads().put(padIdentity, content);
    return this;
  }

  public String getHackpad(PadIdentity padIdentity) {
    return controller.getHackpads().get(padIdentity);
  }

  public Map<PadIdentity, String> getHackpads() {
    return controller.getHackpads();
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
          new Object[]{
              FakeHackpadServerConfiguration.class,
              PropertyPlaceholderAutoConfiguration.class,
              DispatcherServletAutoConfiguration.class,
              ServerPropertiesAutoConfiguration.class},
          new String[]{"--server.port=" + port});
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
  @ComponentScan(basePackageClasses = FakeHackpadController.class)
  public static class FakeHackpadServerConfiguration {

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory(
        @Value("${server.port}") int port) {
      return new TomcatEmbeddedServletContainerFactory(CONTEXT_PATH, port);
    }
  }

  /**
   * @author sbelov@google.com (Stan Belov)
   */
  @Controller
  private static class FakeHackpadController {

    private String clientId;
    private String clientSecret;
    private Map<PadIdentity, String> hackpads = new ConcurrentHashMap<>();

    @RequestMapping(
        value = {"/{padId:.*}"},
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String renderHackpad(@PathVariable("padId") String padId) {
      return hackpads.get(new PadIdentity(padId));
    }

    @RequestMapping(
        value = {"/api/1.0/pad/{padId}/content/latest.html"},
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getHackpad(@PathVariable("padId") String padId) {
      return hackpads.get(new PadIdentity(padId));
    }

    @RequestMapping(value = "/api/1.0/pad/create", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PadIdentity createHackpad(@RequestBody String content) {
      PadIdentity padIdentity = new PadIdentity(RandomStringUtils.randomAlphanumeric(8));
      hackpads.put(padIdentity, content);
      return padIdentity;
    }

    @RequestMapping(
        value = "/api/1.0/pad/{padId}/content",
        method = RequestMethod.POST,
        consumes = MediaType.TEXT_HTML_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HackpadStatus updateHackpad(
        @PathVariable("padId") String padId,
        @RequestBody String content) {
      PadIdentity padIdentity = new PadIdentity(padId);
      if (!hackpads.containsKey(padIdentity)) {
        return new HackpadStatus(false);
      }
      hackpads.put(padIdentity, content);
      return new HackpadStatus(true);
    }

    public FakeHackpadController setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public FakeHackpadController setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public Map<PadIdentity, String> getHackpads() {
      return hackpads;
    }
  }
}
