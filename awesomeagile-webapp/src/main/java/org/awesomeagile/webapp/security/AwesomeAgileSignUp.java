package org.awesomeagile.webapp.security;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.team.User;
import org.awesomeagile.model.team.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

public class AwesomeAgileSignUp implements ConnectionSignUp {

    // TODO finish implementing this class

    @Autowired
    private UserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        User user = new User();
        user.setPrimaryEmail(profile.getEmail());
        user.setDisplayName(profile.getFirstName() + profile.getLastName());
        user.setIsVisible(true);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return user.getPrimaryEmail();
    }

}
