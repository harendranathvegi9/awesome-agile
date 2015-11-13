package org.awesomeagile.dao;

import org.awesomeagile.model.team.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for {@link User} entities
 *
 * @author sbelov@google.com (Stan Belov)
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findOneByPrimaryEmail(String primaryEmail);

}
