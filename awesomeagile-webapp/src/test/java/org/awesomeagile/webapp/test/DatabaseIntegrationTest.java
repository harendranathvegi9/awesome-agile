package org.awesomeagile.webapp.test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.math.RandomUtils;
import org.awesomeagile.data.test.TestDatabase;
import org.awesomeagile.model.team.User;
import org.hamcrest.CustomMatcher;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * A test that brings up a local PostgreSQL instance within a Docker container.
 * It is assumed that Docker environment variables are already set up.
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class DatabaseIntegrationTest {

  private static final String DATABASE_NAME = "awesomeagile";

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME,
      "/create-sample-data.sql"
  );

  @Test
  public void testPostgres() throws Exception {
    JdbcTemplate jdbcTemplate = testDatabase.jdbcTemplate();
    int random = RandomUtils.nextInt(1000);
    Integer integer = jdbcTemplate.queryForObject("select " + random, Integer.class);
    assertEquals(random, integer.intValue());
  }

  @Test
  public void testUsers() throws Exception {
    JdbcTemplate jdbcTemplate = testDatabase.jdbcTemplate(DATABASE_NAME);
    List<User> users = jdbcTemplate
        .query("select * from teams.user", new BeanPropertyRowMapper<>(User.class));
    assertEquals(2, users.size());
    assertThat(users, hasItem(new UserWithName("stan")));
  }

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
