package org.awesomeagile.webapp.test;

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
