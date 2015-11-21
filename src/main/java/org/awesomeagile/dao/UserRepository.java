package org.awesomeagile.dao;

import org.awesomeagile.model.team.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * A repository for {@link User} entities
 *
 * @author sbelov@google.com (Stan Belov)
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findOneByPrimaryEmail(String primaryEmail);

    @Query("select u from User u where u.authProviderId = :authProviderId"
        + " and u.authProviderUserId = :authProviderUserId")
    User findOneByAuthProviderUserId(
        @Param("authProviderId") String authProviderId,
        @Param("authProviderUserId") String authProviderUserId);

    @Query("select u from User u where u.authProviderId = :authProviderId"
        + " and u.authProviderUserId in (:authProviderUserIds)")
    Collection<User> findOneByAuthProviderUserIds(
        @Param("authProviderId") String authProviderId,
        @Param("authProviderUserIds") Collection<String> authProviderUserIds);

}
