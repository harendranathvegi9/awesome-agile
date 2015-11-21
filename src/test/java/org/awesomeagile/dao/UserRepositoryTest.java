package org.awesomeagile.dao;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.awesomeagile.TestApplication;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class})
@ActiveProfiles("test")
public class UserRepositoryTest {

  private final AtomicInteger providerKey = new AtomicInteger();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  // Object under test
  @Autowired
  private UserRepository userRepository;

  @Before
  public void setUp() throws Exception {
    clearTable();
  }

  @Test
  public void testFindOne() throws Exception {
    String email = "mwarren@gmail.com";
    long userId = 1234;
    String displayName = "Mark";
    String avatar = "ava";
    jdbcTemplate.update("insert into teams.user(id, primary_email,display_name,avatar,is_visible,status)"
        + " values(?, ?, ?, ?, ?, ?)", userId, email, displayName, avatar, false, "INACTIVE");
    User user = userRepository.findOne(userId);
    assertNotNull(user);
    assertEquals(email, user.getPrimaryEmail());
    assertEquals(displayName, user.getDisplayName());
    assertEquals(UserStatus.INACTIVE, user.getStatus());
    assertEquals(avatar, user.getAvatar());
    assertFalse(user.isVisible());
  }

  @Test
  public void testGetNonExistingUser() throws Exception {
    assertNull(userRepository.findOne(1234L));
  }

  @Test
  public void testCreateUser() throws Exception {
    User newUser = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User created = userRepository.save(newUser);
    assertTrue(created.getId() > 0);
    assertEquals(newUser.getDisplayName(), created.getDisplayName());
    assertEquals(newUser.getPrimaryEmail(), created.getPrimaryEmail());
    assertEquals(newUser.getAvatar(), created.getAvatar());
    assertEquals(newUser.getStatus(), created.getStatus());
    assertEquals(newUser.getAuthProviderId(), created.getAuthProviderId());
    assertEquals(newUser.getAuthProviderUserId(), created.getAuthProviderUserId());
    User databaseUser = userRepository.findOne(created.getId());
    assertEquals(created, databaseUser);
  }

  @Test
  public void testFindOneByAuthProviderId() throws Exception {
    User one = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User createdOne = userRepository.save(one);
    User two = userWithNameAndEmail("sbelov", "sbelov@google.com");
    User createdTwo = userRepository.save(two);
    assertEquals(createdOne, userRepository.findOneByAuthProviderUserId(
        one.getAuthProviderId(),
        one.getAuthProviderUserId()));
    assertEquals(createdTwo, userRepository.findOneByAuthProviderUserId(
        two.getAuthProviderId(),
        two.getAuthProviderUserId()));
  }

  @Test
  public void testFindOneByAuthProviderIds() throws Exception {
    User one = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User createdOne = userRepository.save(one);
    User two = userWithNameAndEmail("sbelov", "sbelov@google.com");
    User createdTwo = userRepository.save(two);
    assertEquals(ImmutableList.of(createdOne, createdTwo),
        ImmutableList.copyOf(userRepository.findOneByAuthProviderUserIds(
            one.getAuthProviderId(),
            ImmutableSet.of(one.getAuthProviderUserId(), two.getAuthProviderUserId()))));
    assertEquals(ImmutableList.of(createdTwo),
        ImmutableList.copyOf(userRepository.findOneByAuthProviderUserIds(
            one.getAuthProviderId(),
            ImmutableSet.of(two.getAuthProviderUserId()))));
  }

  @Test
  public void testDuplicateProviderUserId() throws Exception {
    User one = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    userRepository.save(one);
    User two = new User(one);
    // force creation of a new user
    two.setId(null);
    try {
      userRepository.save(two);
      fail("DataIntegrityViolationException expected.");
    } catch (DataIntegrityViolationException ignored) {
      // no-op
    }
  }

  @Test
  public void testDuplicateEmail() throws Exception {
    User one = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    userRepository.save(one);
    User two = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    // force creation of a new user
    two.setId(null);
    try {
      userRepository.save(two);
      fail("DataIntegrityViolationException expected.");
    } catch (DataIntegrityViolationException ignored) {
      // no-op
    }
  }

  @Test
  public void testUpdateUser() throws Exception {
    User newUser = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User created = userRepository.save(newUser);
    long createdId = created.getId();
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

    // try to update the primary e-mail - shouldn't work
    User withDifferentEmail = new User(toUpdate);
    withDifferentEmail.setPrimaryEmail("sbelov@google.com");
    userRepository.save(withDifferentEmail);
    assertEquals(toUpdate, userRepository.findOne(createdId));

    // try to update the signup date - shouldn't work
    User withDifferentSignupDate = new User(toUpdate);
    withDifferentSignupDate.setCreatedDate(new Date(1999, 1, 1));
    userRepository.save(withDifferentSignupDate);
    assertEquals(toUpdate, userRepository.findOne(createdId));
  }

  @Test
  public void testFindAll() throws Exception {
    Iterable<User> users = userRepository.findAll();
    assertNotNull(users);
    assertTrue(Iterables.isEmpty(users));
    User newUserOne = userWithNameAndEmail("sbelov", "belov.stan@gmail.com");
    User one = userRepository.save(newUserOne);
    users = userRepository.findAll();
    assertNotNull(users);
    assertEquals(1, Iterables.size(users));
    assertThat(users, hasItem(one));
    User newUserTwo = userWithNameAndEmail("anand", "anand@hotmail.com");
    User two = userRepository.save(newUserTwo);
    users = userRepository.findAll();
    assertNotNull(users);
    assertEquals(2, Iterables.size(users));
    assertThat(users, hasItems(one, two));
  }

  private void assertUserUpdated(long createdId, User toUpdate) {
    User updated;
    User databaseUser;
    updated = userRepository.save(toUpdate);
    assertEquals(toUpdate, updated);
    databaseUser = userRepository.findOne(createdId);
    assertEquals(toUpdate, databaseUser);
  }

  private User userWithNameAndEmail(String displayName, String email) {
    return new User()
        .setStatus(UserStatus.ACTIVE)
        .setIsVisible(true)
        .setDisplayName(displayName)
        .setAuthProviderId("google")
        .setAuthProviderUserId(String.valueOf(providerKey.incrementAndGet()))
        .setPrimaryEmail(email);
  }

  private void clearTable() {
    jdbcTemplate.update("delete from teams.user");
  }
}