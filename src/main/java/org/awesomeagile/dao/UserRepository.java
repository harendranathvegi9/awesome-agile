package org.awesomeagile.dao;

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
