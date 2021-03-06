package org.awesomeagile.webapp;

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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import org.apache.commons.lang3.StringUtils;
import org.awesomeagile.AwesomeAgileApplication;
import org.awesomeagile.dao.testing.TestDatabase;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.awesomeagile.testing.google.FakeGoogleServer;
import org.awesomeagile.testing.google.Person;
import org.awesomeagile.testing.google.Person.Email;
import org.awesomeagile.testing.google.Person.Image;
import org.awesomeagile.testing.google.Person.Name;
import org.awesomeagile.testing.hackpad.FakeHackpadServer;
import org.awesomeagile.webapp.AwesomeAgileFunctionalTest.EnvInitializer;
import org.awesomeagile.webapp.page.HackpadPage;
import org.awesomeagile.webapp.page.LandingPage;
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

import java.util.Set;
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
public class AwesomeAgileFunctionalTest {

  private static final String DATABASE_NAME = "awesomeagile";
  private static final String CLIENT_ID = "testclient-1.google.com";
  private static final String CLIENT_SECRET = "verystrongsecret";
  private static final String HACKPAD_CLIENT_ID = "hackpadclient";
  private static final String HACKPAD_CLIENT_SECRET = "hackpadsecret";
  private static final String DISPLAY_NAME = "sbelov";
  private static final String DEFINITION_OF_READY_TEMPLATE_ID = "Defnready-xyz";
  private static final String DEFINITION_OF_READY_CONTENTS = "This is the definition of ready";
  private static final String DEFINITION_OF_DONE_TEMPLATE_ID = "Defndone-xyz";
  private static final String DEFINITION_OF_DONE_CONTENTS = "This is the definition of done";
  private static final AtomicLong idProvider = new AtomicLong(1);

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
                  .put("hackpad.client.id", HACKPAD_CLIENT_ID)
                  .put("hackpad.client.secret", HACKPAD_CLIENT_SECRET)
                  .put("hackpad.url", fakeHackpadServer.getEndpoint())
                  .put("hackpad.templates.defnready", DEFINITION_OF_READY_TEMPLATE_ID)
                  .put("hackpad.templates.defndone", DEFINITION_OF_DONE_TEMPLATE_ID)
                  .build()));
    }
  }

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME
  );

  @ClassRule
  public static FakeGoogleServer fakeGoogleServer = new FakeGoogleServer();

  @ClassRule
  public static FakeHackpadServer fakeHackpadServer = new FakeHackpadServer();

  @Value("${local.server.port}")
  private int port;

  private HtmlUnitDriver driver;

  @Before
  public void setUp() throws Exception {
    System.out.println("Fake Google OAuth2 server up at: " + fakeGoogleServer.getEndpoint());
    System.out.println("Fake Hackpad server up at: " + fakeHackpadServer.getEndpoint());
    System.out.println("AwesomeAgile web application up at: " + getEndpoint());
    fakeGoogleServer.setClientId(CLIENT_ID);
    fakeGoogleServer.setClientSecret(CLIENT_SECRET);
    fakeGoogleServer.setRedirectUriPrefixes(
        ImmutableList.of("http://localhost:" + port + "/"));
    fakeGoogleServer.setPerson(createUser());
    fakeHackpadServer.setClientId(HACKPAD_CLIENT_ID);
    fakeHackpadServer.setClientSecret(HACKPAD_CLIENT_SECRET);
    fakeHackpadServer.getHackpads().clear();
    fakeHackpadServer.addHackpad(
        new PadIdentity(DEFINITION_OF_READY_TEMPLATE_ID),
        DEFINITION_OF_READY_CONTENTS);
    fakeHackpadServer.addHackpad(
        new PadIdentity(DEFINITION_OF_DONE_TEMPLATE_ID),
        DEFINITION_OF_DONE_CONTENTS);

    driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    driver.setJavascriptEnabled(true);
  }

  private Person createUser() {
    long userId = idProvider.getAndIncrement();
    return new Person()
        .setId(String.valueOf(userId))
        .setDisplayName(DISPLAY_NAME)
        .setName(new Name("Stan", "Belov"))
        .setEmails(ImmutableList.of(new Email(emailForUser(userId), "account")))
        .setImage(new Image("http://static.google.com/avatar.jpg"));
  }

  private static String emailForUser(long id) {
    return String.format("user%03d@gmail.com", id);
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

  @Test
  public void testCreateDefinitionOfReady() throws Exception {
    assertEquals(2, fakeHackpadServer.getHackpads().size());
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    assertThat(driver.getWindowHandles(), hasSize(1));
    String firstWindow = driver.getWindowHandle();
    landingPage.createDefinitionOfReady();
    assertThat(driver.getWindowHandles(), hasSize(2));
    assertEquals(3, fakeHackpadServer.getHackpads().size());

    String newWindow = Iterables.getFirst(Sets.difference(
        driver.getWindowHandles(),
        ImmutableSet.of(firstWindow)), null);
    driver.switchTo().window(newWindow);
    HackpadPage hackpadPage = PageFactory.initElements(driver, HackpadPage.class);
    assertEquals(DEFINITION_OF_READY_CONTENTS, hackpadPage.getContent());
    String newHackpadUrl = driver.getCurrentUrl();
    String newHackpadId = StringUtils.substringAfterLast(newHackpadUrl, "/");
    assertNotEquals(DEFINITION_OF_READY_TEMPLATE_ID, newHackpadId);
    String newHackpad = fakeHackpadServer.getHackpad(new PadIdentity(newHackpadId));
    assertEquals(DEFINITION_OF_READY_CONTENTS, newHackpad);
  }

  @Test
  public void testCreateDefinitionOfDone() throws Exception {
    assertEquals(2, fakeHackpadServer.getHackpads().size());
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    assertThat(driver.getWindowHandles(), hasSize(1));
    String firstWindow = driver.getWindowHandle();
    landingPage.createDefinitionOfDone();
    assertThat(driver.getWindowHandles(), hasSize(2));
    assertEquals(3, fakeHackpadServer.getHackpads().size());

    String newWindow = Iterables.getFirst(Sets.difference(
        driver.getWindowHandles(),
        ImmutableSet.of(firstWindow)), null);
    driver.switchTo().window(newWindow);
    HackpadPage hackpadPage = PageFactory.initElements(driver, HackpadPage.class);
    assertEquals(DEFINITION_OF_DONE_CONTENTS, hackpadPage.getContent());
    String newHackpadUrl = driver.getCurrentUrl();
    String newHackpadId = StringUtils.substringAfterLast(newHackpadUrl, "/");
    assertNotEquals(DEFINITION_OF_DONE_TEMPLATE_ID, newHackpadId);
    String newHackpad = fakeHackpadServer.getHackpad(new PadIdentity(newHackpadId));
    assertEquals(DEFINITION_OF_DONE_CONTENTS, newHackpad);
  }

  /**
   * Tests the fact that dashboard view can retrieve links
   * to the previously created Hackpad documents with the following steps:
   * 1. Log in.
   * 2. Click on create definition of ready button and wait until the view button is visible.
   * 3. Refresh Awesome Agile window.
   * 4. Verify that the view button is visible, and create button is invisible.
   * 5. Click on the view button and verify the contents.
   * @throws Exception
   */
  @Test
  public void testDashboard() throws Exception {
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    assertThat(driver.getWindowHandles(), hasSize(1));
    String firstWindow = driver.getWindowHandle();
    landingPage.createDefinitionOfReady();
    closeWindowsExceptFor(driver, firstWindow);
    assertEquals(1, driver.getWindowHandles().size());
    driver.switchTo().window(firstWindow);
    driver.navigate().refresh();
    landingPage.waitForDefinitionOfReady();
    assertTrue(landingPage.isDefinitionOfReadyViewable());
    landingPage.viewDefinitionOfReady();
    String newWindow = Iterables.getFirst(Sets.difference(
        driver.getWindowHandles(),
        ImmutableSet.of(firstWindow)), null);
    driver.switchTo().window(newWindow);
    HackpadPage hackpadPage = PageFactory.initElements(driver, HackpadPage.class);
    assertEquals(DEFINITION_OF_READY_CONTENTS, hackpadPage.getContent());
    String newHackpadUrl = driver.getCurrentUrl();
    String newHackpadId = StringUtils.substringAfterLast(newHackpadUrl, "/");
    assertNotEquals(DEFINITION_OF_READY_TEMPLATE_ID, newHackpadId);
  }

  @Test
  public void testDashboardReopenBrowser() throws Exception {
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    assertThat(driver.getWindowHandles(), hasSize(1));
    landingPage.createDefinitionOfReady();

    HtmlUnitDriver driverTwo = new HtmlUnitDriver(BrowserVersion.CHROME);
    driverTwo.setJavascriptEnabled(true);

    // Open a completely new browser with no cookies
    // Verify that view button is visible for this same user,
    // and we're able to open his Definition of ready hackpad
    LandingPage landingPageTwo = PageFactory.initElements(driverTwo, LandingPage.class);
    landingPageTwo.loginWithGoogle(getEndpoint());
    landingPageTwo.waitForDefinitionOfReady();
    assertTrue(landingPageTwo.isDefinitionOfReadyViewable());
    landingPageTwo.viewDefinitionOfReady();
    String newWindow = Iterables.getFirst(Sets.difference(
        driverTwo.getWindowHandles(),
        ImmutableSet.of(driverTwo.getWindowHandle())), null);
    driverTwo.switchTo().window(newWindow);
    HackpadPage hackpadPage = PageFactory.initElements(driverTwo, HackpadPage.class);
    assertEquals(DEFINITION_OF_READY_CONTENTS, hackpadPage.getContent());
  }

  /**
   * Verifies that different users get different dashboards with links to different documents
   * @throws Exception
   */
  @Test
  public void testSeparateDashboards() throws Exception {
    String templateOne = DEFINITION_OF_READY_CONTENTS + "1";
    String templateTwo = DEFINITION_OF_READY_CONTENTS + "2";

    Person userOne = createUser();
    Person userTwo = createUser();

    // Create hackpad for user #1
    fakeGoogleServer.setPerson(userOne);

    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(getEndpoint());
    fakeHackpadServer.addHackpad(
        new PadIdentity(DEFINITION_OF_READY_TEMPLATE_ID),
        templateOne);
    String currentWindow = driver.getWindowHandle();
    landingPage.createDefinitionOfReady();
    closeWindowsExceptFor(driver, currentWindow);


    // Log in with user #2, there should be no definition of ready on the dashboard
    fakeGoogleServer.setPerson(userTwo);

    HtmlUnitDriver driverTwo = new HtmlUnitDriver(BrowserVersion.CHROME);
    driverTwo.setJavascriptEnabled(true);
    LandingPage landingPageTwo = PageFactory.initElements(driverTwo, LandingPage.class);
    landingPageTwo.loginWithGoogle(getEndpoint());
    assertFalse(landingPageTwo.isDefinitionOfReadyViewable());

    // Create another definition of ready
    fakeHackpadServer.addHackpad(
        new PadIdentity(DEFINITION_OF_READY_TEMPLATE_ID),
        templateTwo);
    String currentWindowTwo = driverTwo.getWindowHandle();
    landingPageTwo.createDefinitionOfReady();
    closeWindowsExceptFor(driverTwo, currentWindowTwo);

    assertDefinitionOfReadyContents(driver, templateOne);

    assertDefinitionOfReadyContents(driverTwo, templateTwo);
  }

  private static void closeWindowsExceptFor(HtmlUnitDriver driver, String currentWindow) {
    Set<String> difference = ImmutableSet.copyOf(Sets.difference(
        driver.getWindowHandles(),
        ImmutableSet.of(currentWindow)));
    for (String window : difference) {
      driver.switchTo().window(window).close();
    }
    driver.switchTo().window(currentWindow);
  }

  private static void assertDefinitionOfReadyContents(HtmlUnitDriver driver, String contents) {
    // Refresh the dashboard for user #1; he should have definition of ready viewable
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    driver.navigate().refresh();
    String currentWindow = driver.getWindowHandle();
    landingPage.waitForDefinitionOfReady();
    assertTrue(landingPage.isDefinitionOfReadyViewable());
    landingPage.viewDefinitionOfReady();

    // Verify definition of ready contents for user #1
    String newWindow = Iterables.getFirst(Sets.difference(
        driver.getWindowHandles(),
        ImmutableSet.of(currentWindow)), null);
    driver.switchTo().window(newWindow);
    HackpadPage hackpadPage = PageFactory.initElements(driver, HackpadPage.class);
    assertEquals(contents, hackpadPage.getContent());
  }

  private String getEndpoint() {
    return "http://localhost:" + port + "/";
  }
}
