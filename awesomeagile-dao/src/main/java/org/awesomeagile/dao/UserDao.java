package org.awesomeagile.dao;

import org.awesomeagile.model.team.User;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

/**
 * DAO for operating on user entities.
 *
 * @author sbelov@google.com (Stan Belov)
 */
public interface UserDao {

  /**
   * Retrieves a given user by ID
   * Throws a {@link org.springframework.dao.DataAccessException} if a user is not found.
   *
   * @param id user identifier
   * @return found user
   */
  User getUserById(int id);

  /**
   * Persists a new user to the database.
   *
   * @param newUser user to be created
   * @return created user with the userId property filled in.
   */
  User createUser(User newUser);

  /**
   * Updates a given user.
   * Throws a {@link JdbcUpdateAffectedIncorrectNumberOfRowsException} if a user to be updated
   * is not found.
   *
   * @param updatedUser user to be updated
   * @return updated user.
   */
  User updateUser(User updatedUser);
}
