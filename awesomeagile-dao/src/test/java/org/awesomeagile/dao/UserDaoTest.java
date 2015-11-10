package org.awesomeagile.dao;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.awesomeagile.data.test.TestDatabase;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class UserDaoTest {
  private static final String DATABASE_NAME = "awesomeagile";

  @ClassRule
  public static TestDatabase testDatabase = new TestDatabase(
      DATABASE_NAME,
      "/create-schema.sql",
      "/create-sample-data.sql"
  );
  // Object under test
  private UserDao userDao;

  @Before
  public void setUp() throws Exception {
    clearTable();
    userDao = new UserDaoJdbcTemplate(jdbcTemplate());
  }

  @Test
  public void testGetUserById() throws Exception {
    String email = "mwarren@gmail.com";
    int userId = 1234;
    String displayName = "Mark";
    String avatar = "ava";
    jdbcTemplate().update("insert into teams.user(user_id, primary_email,display_name,avatar,is_visible,status)"
        + " values(?, ?, ?, ?, ?, ?)", userId, email, displayName, avatar, false, "INACTIVE");
    User user = userDao.getUserById(userId);
    assertNotNull(user);
    assertEquals(email, user.getPrimaryEmail());
    assertEquals(displayName, user.getDisplayName());
    assertEquals(UserStatus.INACTIVE, user.getStatus());
    assertEquals(avatar, user.getAvatar());
    assertFalse(user.isVisible());
  }

  @Test(expected = DataAccessException.class)
  public void testGetNonExistingUser() throws Exception {
    userDao.getUserById(1234);
  }

  @Test
  public void testCreateUser() throws Exception {
    User newUser = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User created = userDao.createUser(newUser);
    assertTrue(created.getUserId() > 0);
    assertEquals(newUser.getDisplayName(), created.getDisplayName());
    assertEquals(newUser.getPrimaryEmail(), created.getPrimaryEmail());
    assertEquals(newUser.getAvatar(), created.getAvatar());
    assertEquals(newUser.getStatus(), created.getStatus());
    User databaseUser = userDao.getUserById(created.getUserId());
    assertEquals(created, databaseUser);
  }

  @Test
  public void testUpdateUser() throws Exception {
    User newUser = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User created = userDao.createUser(newUser);
    int createdId = created.getUserId();
    User toUpdate = new User(created);

    // update user status
    toUpdate.setStatus(UserStatus.INACTIVE);
    assertUserUpdated(createdId, toUpdate);

    // now, update the isVisible flag
    toUpdate.setIsVisible(false);
    assertUserUpdated(createdId, toUpdate);

    // update the display name
    toUpdate.setDisplayName("stan");
    assertUserUpdated(createdId, toUpdate);

    // update the avatar
    toUpdate.setAvatar("http://cdn.com/ava.jpg");
    assertUserUpdated(createdId, toUpdate);

    // update the primary e-mail
    toUpdate.setPrimaryEmail("sbelov@google.com");
    assertUserUpdated(createdId, toUpdate);

    // try to update the signup date - shouldn't work
    User withDifferentSignupDate = new User(toUpdate);
    User updated = userDao.updateUser(withDifferentSignupDate);
    assertEquals(toUpdate, updated);
    assertEquals(toUpdate, userDao.getUserById(createdId));
  }

  @Test
  public void testListUsers() throws Exception {
    Collection<User> users = userDao.listUsers();
    assertNotNull(users);
    assertTrue(users.isEmpty());
    User newUserOne = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User one = userDao.createUser(newUserOne);
    users = userDao.listUsers();
    assertNotNull(users);
    assertEquals(1, users.size());
    assertThat(users, hasItem(one));
    User newUserTwo = userWithNameAndEmail("anand", "anand@hotmail.com");
    User two = userDao.createUser(newUserTwo);
    users = userDao.listUsers();
    assertNotNull(users);
    assertEquals(2, users.size());
    assertThat(users, hasItems(one, two));
  }

  private void assertUserUpdated(int createdId, User toUpdate) {
    User updated;
    User databaseUser;
    updated = userDao.updateUser(toUpdate);
    assertEquals(toUpdate, updated);
    databaseUser = userDao.getUserById(createdId);
    assertEquals(toUpdate, databaseUser);
  }

  private static User userWithNameAndEmail(String displayName, String email) {
    return new User()
        .setStatus(UserStatus.ACTIVE)
        .setIsVisible(true)
        .setDisplayName(displayName)
        .setPrimaryEmail(email);
  }

  private JdbcTemplate jdbcTemplate() {
    return testDatabase.jdbcTemplate(DATABASE_NAME);
  }

  private void clearTable() {
    jdbcTemplate().update("delete from teams.user");
  }
}