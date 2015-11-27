package org.awesomeagile.webapp.test;

import static org.apache.commons.lang.math.RandomUtils.nextLong;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.awesomeagile.AwesomeAgileApplication;
import org.awesomeagile.data.test.TestDatabase;
import org.awesomeagile.webapp.test.SignupTest.AppConfig;
import org.awesomeagile.webapp.test.SignupTest.EnvInitializer;
import org.awesomeagile.webapp.test.google.FakeGoogleServer;
import org.awesomeagile.webapp.test.google.Person;
import org.awesomeagile.webapp.test.google.Person.Email;
import org.awesomeagile.webapp.test.google.Person.Image;
import org.awesomeagile.webapp.test.google.Person.Name;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AwesomeAgileApplication.class, AppConfig.class},
    initializers = EnvInitializer.class)
@WebIntegrationTest(randomPort = true)
public class SignupTest {

  private static final String DATABASE_NAME = "awesomeagile";
  public static final String CLIENT_ID = "testclient-1.google.com";
  public static final String CLIENT_SECRET = "verystrongsecret";

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME
  );

  @ClassRule
  public static FakeGoogleServer fakeGoogleServer = new FakeGoogleServer();

  @EnableWebMvc
  @Configuration
  public static class AppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/**").addResourceLocations("classpath:static/").setCachePeriod(31556926);
    }
  }

  @Value("${local.server.port}")
  private int localPort;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  public static final class EnvInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      applicationContext.getEnvironment().getPropertySources().addFirst(
          new MapPropertySource("overrides",
              new ImmutableMap.Builder<String, Object>()
                  .put("spring.social.google.api.url", fakeGoogleServer.getEndpoint())
                  .put("spring.social.google.oauth.url", fakeGoogleServer.getEndpoint())
                  .put("spring.datasource.url", testDatabase.getUrl(DATABASE_NAME))
                  .put("spring.datasource.username", testDatabase.getUserName())
                  .put("spring.datasource.password", testDatabase.getPassword())
                  .put("spring.social.google.clientId", CLIENT_ID)
                  .put("spring.social.google.secret", CLIENT_SECRET)
                  .put("spring.social.google.scope", "profile email")
                  .build()));
    }
  }

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();
    fakeGoogleServer.setClientId(CLIENT_ID);
    fakeGoogleServer.setClientSecret(CLIENT_SECRET);
    fakeGoogleServer.setRedirectUriPrefixes(
        ImmutableList.of("http://localhost:" + localPort + "/"));
    String id = String.valueOf(nextLong());
    fakeGoogleServer.setPerson(
        new Person()
            .setId(id)
            .setDisplayName("DCP")
            .setName(new Name("David", "Peterson"))
            .setEmails(ImmutableList.of(new Email("dpeterson123@gmail.com", "account")))
            .setImage(new Image("http://static.google.com/avatar.jpg"))
    );
  }

  @Test
  public void testHello() throws Exception {
    System.out.println("Server up at: " + fakeGoogleServer.getEndpoint());
    System.out.println("AwesomeAgile up at: http://localhost:" + localPort);
    Thread.sleep(Long.MAX_VALUE);
  }
}
