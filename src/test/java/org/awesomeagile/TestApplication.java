package org.awesomeagile;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@SpringBootApplication(scanBasePackages = {"org.awesomeagile.dao", "org.awesomeagile.model"})
@EnableConfigurationProperties
public class TestApplication {
}
