package org.awesomeagile;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@SpringBootApplication(
    scanBasePackages = {
        "org.awesomeagile.webapp",
        "org.awesomeagile.dao",
        "org.awesomeagile.integrations",
        "org.awesomeagile.model"})
@EnableConfigurationProperties
public class AwesomeAgileApplication {

  public static void main(String[] args) {
    SpringApplication.run(AwesomeAgileApplication.class, args);
  }
}
