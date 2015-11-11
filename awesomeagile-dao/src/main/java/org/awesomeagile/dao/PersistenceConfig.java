package org.awesomeagile.dao;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.awesomeagile")
@EnableJpaAuditing(modifyOnCreate = true)
@EnableTransactionManagement
public class PersistenceConfig {
}
