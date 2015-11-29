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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import org.awesomeagile.AwesomeAgileApplication;
import org.awesomeagile.dao.testing.TestDatabase;
import org.awesomeagile.webapp.security.SignupFunctionalTest.EnvInitializer;
import org.awesomeagile.webapp.security.testing.LandingPage;
import org.awesomeagile.webapp.security.testing.google.FakeGoogleServer;
import org.awesomeagile.webapp.security.testing.google.Person;
import org.awesomeagile.webapp.security.testing.google.Person.Email;
import org.awesomeagile.webapp.security.testing.google.Person.Image;
import org.awesomeagile.webapp.security.testing.google.Person.Name;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An end-to-end test for the sign up flows.
 *
 * @author sbelov@google.com (Stan Belov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = {AwesomeAgileApplication.class},
    initializers = {EnvInitializer.class})
public class SignupFunctionalTest {

  private static final String DATABASE_NAME = "awesomeagile";
  private static final String CLIENT_ID = "testclient-1.google.com";
  private static final String CLIENT_SECRET = "verystrongsecret";
  private static final String DISPLAY_NAME = "sbelov";
  private final AtomicLong idProvider = new AtomicLong(1);

  public static final class EnvInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      applicationContext.getEnvironment().getPropertySources().addFirst(
          new MapPropertySource("overrides",
              new ImmutableMap.Builder<String, Object>()
                  .put("spring.datasource.url", testDatabase.getUrl(DATABASE_NAME))
                  .put("spring.datasource.username", testDatabase.getUserName())
                  .put("spring.datasource.password", testDatabase.getPassword())
                  .put("spring.social.google.api.url", fakeGoogleServer.getEndpoint())
                  .put("spring.social.google.oauth.url", fakeGoogleServer.getEndpoint())
                  .put("spring.social.google.clientId", CLIENT_ID)
                  .put("spring.social.google.secret", CLIENT_SECRET)
                  .put("spring.social.google.scope", "profile email")
                  .build()));
    }
  }

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME
  );

  @ClassRule
  public static FakeGoogleServer fakeGoogleServer = new FakeGoogleServer();

  @Value("${local.server.port}")
  private int port;

  private HtmlUnitDriver driver;

  @Before
  public void setUp() throws Exception {
    System.out.println("Fake Google OAuth2 server up at: " + fakeGoogleServer.getEndpoint());
    System.out.println("AwesomeAgile web application up at: " + getEndpoint());
    fakeGoogleServer.setClientId(CLIENT_ID);
    fakeGoogleServer.setClientSecret(CLIENT_SECRET);
    fakeGoogleServer.setRedirectUriPrefixes(
        ImmutableList.of("http://localhost:" + port + "/"));
    fakeGoogleServer.setPerson(
        new Person()
            .setId(String.valueOf(idProvider.getAndIncrement()))
            .setDisplayName(DISPLAY_NAME)
            .setName(new Name("Stan", "Belov"))
            .setEmails(ImmutableList.of(new Email("belov.stan@gmail.com", "account")))
            .setImage(new Image("http://static.google.com/avatar.jpg"))
    );
    driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    driver.setJavascriptEnabled(true);
  }

  /**
   * Verifies the normal sign up flow:
   * 1. User clicks on the login button.
   * 2. User selects Google as an OAuth2 provider.
   * 3. Google (fake Google OAuth server in this case) authenticates the user
   * and redirects back to the web application with the code request parameter set.
   * 4. Web application requests an OAuth2 token based on the code it received.
   * 5. User is logged in, and his display name is visible on the landing page.
   *
   * @throws Exception
   */
  @Test
  public void testSignupFlow() throws Exception {
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    assertEquals(DISPLAY_NAME, landingPage.getUserName());
    assertFalse(landingPage.isLoginButtonVisible());
  }

  private String getEndpoint() {
    return "http://localhost:" + port + "/";
  }
}
