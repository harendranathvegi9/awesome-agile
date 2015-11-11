package org.awesomeagile;

import org.awesomeagile.dao.UserRepository;
import org.awesomeagile.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AwesomeAgileApplication.class})
@ActiveProfiles("test")
public class AwesomeAgileIntegrationTests {

    // the below to be discovered dynamically at test time

    private static String url = null;

    private static String username = null;

    private static String password = null;

    @Configuration
    protected static class TestConfgiuration {
        @Bean
        public DataSource getDataSource() {
            return new SingleConnectionDataSource(url, username, password, true);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserSave() {
        User user = new User();
        user.setUsername("jdoe@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());
    }

}
