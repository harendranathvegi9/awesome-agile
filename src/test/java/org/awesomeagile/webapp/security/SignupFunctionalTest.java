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

import static org.apache.commons.lang.math.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import org.awesomeagile.AwesomeAgileApplication;
import org.awesomeagile.dao.testing.TestDatabase;
import org.awesomeagile.testutils.NetworkUtils;
import org.awesomeagile.webapp.security.testing.LandingPage;
import org.awesomeagile.webapp.security.testing.google.FakeGoogleServer;
import org.awesomeagile.webapp.security.testing.google.Person;
import org.awesomeagile.webapp.security.testing.google.Person.Email;
import org.awesomeagile.webapp.security.testing.google.Person.Image;
import org.awesomeagile.webapp.security.testing.google.Person.Name;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class SignupFunctionalTest {

  private static final String DATABASE_NAME = "awesomeagile";
  public static final String CLIENT_ID = "testclient-1.google.com";
  public static final String CLIENT_SECRET = "verystrongsecret";

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME
  );

  @ClassRule
  public static FakeGoogleServer fakeGoogleServer = new FakeGoogleServer();

  private static int port;
  private HtmlUnitDriver driver;

  @BeforeClass
  public static void beforeClass() throws IOException {
    port = NetworkUtils.findAvailablePort();
    AwesomeAgileApplication.main(getArguments().toArray(new String[0]));
  }

  private static List<String> getArguments() {
    return ImmutableList.of(
        argument("server.port", port),
        argument("spring.datasource.url", testDatabase.getUrl(DATABASE_NAME)),
        argument("spring.datasource.username", testDatabase.getUserName()),
        argument("spring.datasource.password", testDatabase.getPassword()),

        argument("spring.social.google.api.url", fakeGoogleServer.getEndpoint()),
        argument("spring.social.google.oauth.url", fakeGoogleServer.getEndpoint()),
        argument("spring.social.google.clientId", CLIENT_ID),
        argument("spring.social.google.secret", CLIENT_SECRET),
        argument("spring.social.google.scope", "profile email")
    );
  }

  private static String argument(String name, int value) {
    return argument(name, String.valueOf(value));
  }

  private static String argument(String name, String value) {
    if (!Strings.isNullOrEmpty(value)) {
      return "--" + name + "=" + value + "";
    } else {
      return "--" + name;
    }
  }

  @Before
  public void setUp() throws Exception {
    fakeGoogleServer.setClientId(CLIENT_ID);
    fakeGoogleServer.setClientSecret(CLIENT_SECRET);
    fakeGoogleServer.setRedirectUriPrefixes(
        ImmutableList.of("http://localhost:" + port + "/"));
    String id = String.valueOf(nextLong());
    fakeGoogleServer.setPerson(
        new Person()
            .setId(id)
            .setDisplayName("sbelov")
            .setName(new Name("Stan", "Belov"))
            .setEmails(ImmutableList.of(new Email("belov.stan@gmail.com", "account")))
            .setImage(new Image("http://static.google.com/avatar.jpg"))
    );
    driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    driver.setJavascriptEnabled(true);
  }

  @Test
  public void testHello() throws Exception {
    System.out.println("Fake Google OAuth2 server up at: " + fakeGoogleServer.getEndpoint());
    String endPoint = "http://localhost:" + port + "/";
    System.out.println("AwesomeAgile web application up at: " + endPoint);
    LandingPage landingPage = PageFactory.initElements(driver, LandingPage.class);
    landingPage.loginWithGoogle(endPoint);
    assertEquals("sbelov", landingPage.getUserName());
  }
}
