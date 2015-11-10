package org.awesomeagile.dao;

import org.awesomeagile.model.team.User;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class UserDaoJdbcTemplate implements UserDao {

  private static final String SELECT_USERS = "select * from teams.user where user_id = ?";
  private static final String INSERT_USER =
      "insert into teams.user (primary_email, status, display_name, avatar, is_visible)"
          + " values (?, ?, ?, ?, ?)";
  private static final String UPDATE_USER =
      "update teams.user set (primary_email, status, display_name, avatar, is_visible) = "
          + " (?, ?, ?, ?, ?) where user_id = ?";
  private static final String USER_ID_COLUMN = "user_id";
  private final JdbcTemplate jdbcTemplate;

  public UserDaoJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public User getUserById(int id) {
    User user = jdbcTemplate.queryForObject(SELECT_USERS,
        new BeanPropertyRowMapper<>(User.class), id);
    return user;
  }

  @Override
  public User createUser(final User newUser) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement statement = con.prepareStatement(INSERT_USER,
            new String[]{USER_ID_COLUMN});
        statement.setString(1, newUser.getPrimaryEmail());
        statement.setString(2, newUser.getStatus().name());
        statement.setString(3, newUser.getDisplayName());
        statement.setString(4, newUser.getAvatar());
        statement.setBoolean(5, newUser.isVisible());
        return statement;
      }
    }, keyHolder);
    return getUserById(keyHolder.getKey().intValue());
  }

  @Override
  public User updateUser(final User updatedUser) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int updated = jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement statement = con.prepareStatement(UPDATE_USER);
        statement.setString(1, updatedUser.getPrimaryEmail());
        statement.setString(2, updatedUser.getStatus().name());
        statement.setString(3, updatedUser.getDisplayName());
        statement.setString(4, updatedUser.getAvatar());
        statement.setBoolean(5, updatedUser.isVisible());
        statement.setInt(6, updatedUser.getUserId());
        return statement;
      }
    }, keyHolder);
    if (updated == 0) {
      throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
          UPDATE_USER,
          /* expected */ 1,
          /* actual */ 0);
    }
    return getUserById(updatedUser.getUserId());
  }
}
