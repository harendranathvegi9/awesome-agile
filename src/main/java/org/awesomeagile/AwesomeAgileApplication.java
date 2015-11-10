package org.awesomeagile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AwesomeAgileApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwesomeAgileApplication.class, args);
    }

}
