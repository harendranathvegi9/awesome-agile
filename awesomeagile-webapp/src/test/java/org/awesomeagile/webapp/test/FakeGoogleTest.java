package org.awesomeagile.webapp.test;

import static org.apache.commons.lang.math.RandomUtils.nextLong;

import com.google.common.collect.ImmutableList;

import org.awesomeagile.webapp.test.google.FakeGoogleServer;
import org.awesomeagile.webapp.test.google.Person;
import org.awesomeagile.webapp.test.google.Person.Email;
import org.awesomeagile.webapp.test.google.Person.Image;
import org.awesomeagile.webapp.test.google.Person.Name;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class FakeGoogleTest {

  @Rule
  public FakeGoogleServer fakeGoogleServer = new FakeGoogleServer();

  @Test
  public void testHello() throws Exception {
    System.out.println("Server up at: " + fakeGoogleServer.getEndpoint());

    fakeGoogleServer.setClientId("testclient-1.google.com");
    fakeGoogleServer.setClientSecret("verystrongsecret");
    fakeGoogleServer.setRedirectUriPrefixes(ImmutableList.of("http://www.awesomeagile.org/",
        "http://localhost:8888"));
    String id = String.valueOf(nextLong());
    fakeGoogleServer.setPerson(
        new Person()
            .setId(id)
            .setDisplayName("DCP")
            .setName(new Name("David", "Peterson"))
            .setEmails(ImmutableList.of(new Email("dpeterson123@gmail.com", "account")))
            .setImage(new Image("http://static.google.com/avatar.jpg"))
    );
    Thread.sleep(Long.MAX_VALUE);
  }
}
