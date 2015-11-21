package org.awesomeagile.webapp.test;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.math.RandomUtils;
import org.awesomeagile.TestApplication;
import org.awesomeagile.model.team.User;
import org.hamcrest.CustomMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A test that brings up a local PostgreSQL instance within a Docker container.
 * It is assumed that Docker environment variables are already set up.
 *
 * @author sbelov@google.com (Stan Belov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class})
@ActiveProfiles("test")
public class DatabaseIntegrationTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testPostgres() throws Exception {
    int random = RandomUtils.nextInt(1000);
    Integer integer = jdbcTemplate.queryForObject("select " + random, Integer.class);
    assertEquals(random, integer.intValue());
  }

  /*
  @Test
  public void testUsers() throws Exception {
    List<User> users = jdbcTemplate
        .query("select * from teams.user", new BeanPropertyRowMapper<>(User.class));
    assertEquals(2, users.size());
    assertThat(users, hasItem(new UserWithName("stan")));
  }
  */

  private static final class UserWithName extends CustomMatcher<User> {
    private final String name;

    public UserWithName(String name) {
      super("user with name " + name);
      this.name = name;
    }

    @Override
    public boolean matches(Object item) {
      return item instanceof User && name.equals(((User) item).getDisplayName());
    }
  }
}
