package org.awesomeagile.dao;

import org.awesomeagile.model.team.User;
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
public class UserDao {

  private final JdbcTemplate jdbcTemplate;

  public UserDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public User getUserById(int id) {
    User user = jdbcTemplate.queryForObject("select * from user where user_id=?",
        new BeanPropertyRowMapper<User>(), id);
    return user;
  }

  public User createUser(final User newUser) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement statement = con.prepareStatement("insert into user (primary_email, signup_date, status, display_name, avatar, is_visible)"
            + " values(?, ?, ?, ?, ?, ?)");
        statement.setString(1, newUser.getPrimaryEmail());
        statement.setDate(2, new java.sql.Date(newUser.getSignupDate().getTime()));
        statement.setString(3, newUser.getStatus().name());
        statement.setString(4, newUser.getDisplayName());
        statement.setString(5, newUser.getAvatar());
        statement.setBoolean(6, newUser.isVisible());
        return statement;
      }
    }, keyHolder);
    return getUserById(keyHolder.getKey().intValue());
  }
}
