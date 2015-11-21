package org.awesomeagile;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@SpringBootApplication(scanBasePackages = {"org.awesomeagile.dao", "org.awesomeagile.model"})
@EnableConfigurationProperties
@ActiveProfiles("test")
public class TestApplication {
}
