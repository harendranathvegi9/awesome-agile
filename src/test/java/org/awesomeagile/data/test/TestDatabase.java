package org.awesomeagile.data.test;

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

import static java.util.Arrays.asList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class TestDatabase extends ExternalResource {

  public static final int TIMEOUT = 5;
  public static final int RETRIES = 30;
  public static final int POSTGRESQL_PORT = 5432;
  public static final String POSTGRES_9_2_IMAGE = "postgres:9.4";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "";
  private String hostName;
  private ContainerCreation container;
  private DefaultDockerClient docker;
  private int localPort;
  private final String databaseName;
  private final List<String> scripts;

  public TestDatabase(String databaseName, String... scripts) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(databaseName));
    this.databaseName = databaseName;
    this.scripts = asList(scripts);
  }

  @Override
  protected void before() throws Throwable {
    // Create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
    docker = DefaultDockerClient.fromEnv().build();
    hostName = docker.getHost();
    docker.pull(POSTGRES_9_2_IMAGE);
    // Bind container ports to host ports
    localPort = findAvailablePort();
    final HostConfig hostConfig = HostConfig.builder()
        .portBindings(createPortBinding(POSTGRESQL_PORT, localPort))
        .build();
    this.container = docker.createContainer(ContainerConfig.builder()
        .image(POSTGRES_9_2_IMAGE)
        .hostConfig(hostConfig)
        .exposedPorts(String.valueOf(POSTGRESQL_PORT))
        .build());
    docker.startContainer(container.id());
    waitForDatabase(RETRIES);
    createDatabase();
    Flyway flyway = new Flyway();
    flyway.setSqlMigrationPrefix("v");
    flyway.setSqlMigrationSeparator("-");
    flyway.setDataSource(getDataSource(databaseName));
    flyway.migrate();
    for (String script : scripts) {
      executeScript(script);
    }
  }

  public void executeScript(final String script) {
    executeScript(Optional.of(databaseName), script);
  }

  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(getDataSource());
  }

  public DriverManagerDataSource getDataSource() {
    return new DriverManagerDataSource(
        /* url */ getUrl(),
        /* username */ "postgres",
        /* password */ "");
  }

  public JdbcTemplate jdbcTemplate(String database) {
    return new JdbcTemplate(getDataSource(database));
  }

  public DataSource getDataSource(String database) {
    return new DriverManagerDataSource(
            /* url */ getUrl(database),
            /* username */ "postgres",
            /* password */ "");
  }

  public String getUrl() {
    return String.format("jdbc:postgresql://%s:%d/", hostName, localPort);
  }

  public String getUrl(String database) {
    return String.format("jdbc:postgresql://%s:%d/%s", hostName, localPort, database);
  }

  @Override
  protected void after() {
    try {
      docker.stopContainer(container.id(), TIMEOUT);
      docker.removeContainer(container.id());
    } catch (Exception ex) {
      throw new RuntimeException("Unable to stop the Docker container", ex);
    }
  }

  private static Map<String, List<PortBinding>> createPortBinding(int from, int to) {
    final Map<String, List<PortBinding>> portBindings = new HashMap<>();
    List<PortBinding> hostPorts = new ArrayList<>();
    hostPorts.add(PortBinding.of("0.0.0.0", to));
    portBindings.put(String.valueOf(from), hostPorts);
    return portBindings;
  }

  private int findAvailablePort() throws IOException {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(0);
      return serverSocket.getLocalPort();
    } finally {
      if (serverSocket != null) {
        serverSocket.close();
      }
    }
  }

  private void waitForDatabase(int retries) {
    JdbcTemplate jdbcTemplate = jdbcTemplate();
    int left = retries;
    while (left > 0) {
      left--;
      System.out.println(left + " attempts left");
      try {
        Thread.sleep(1000);
        jdbcTemplate.execute("select 1");
        return;
      } catch (Exception ex) {
        // ignore
      }
    }
    throw new RuntimeException("Database did not come up after " + retries + " attempts");
  }

  private void executeScript(Optional<String> databaseName, final String script) {
    JdbcTemplate jdbcTemplate =
        databaseName.isPresent() ? jdbcTemplate(databaseName.get()) : jdbcTemplate();
    jdbcTemplate.execute(new ConnectionCallback<Object>() {
      @Override
      public Object doInConnection(Connection con) throws SQLException, DataAccessException {
        ScriptUtils.executeSqlScript(con, new ClassPathResource(script));
        return null;
      }
    });
  }

  private void createDatabase() {
    JdbcTemplate jdbcTemplate = jdbcTemplate();
    jdbcTemplate.execute(String.format("DROP DATABASE IF EXISTS %s", databaseName));
    jdbcTemplate.execute(String.format("CREATE DATABASE %s", databaseName));
  }

  public String getUserName() {
    return USERNAME;
  }

  public String getPassword() {
    return PASSWORD;
  }
}
