package org.awesomeagile.webapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;

public class AwesomeAgileUsersConnectionRepository implements UsersConnectionRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        logger.info("HERE: findUserIdsWithConnection()");
        // TODO implement this method
        return null;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        logger.info("HERE: findUserIdsConnectedTo()");
        // TODO implement this method
        return null;
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        logger.info("HERE: createConnectionRepository()");
        // TODO implement this method
        return null;
    }

}
