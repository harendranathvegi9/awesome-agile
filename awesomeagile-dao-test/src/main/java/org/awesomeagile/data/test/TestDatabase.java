package org.awesomeagile.data.test;

import static java.util.Arrays.asList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

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

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class TestDatabase extends ExternalResource {

  public static final int TIMEOUT = 5;
  public static final int RETRIES = 30;
  public static final int POSTGRESQL_PORT = 5432;
  public static final String POSTGRES_9_2_IMAGE = "postgres:9.4";
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
    // Bind container ports to host ports
    localPort = findAvailablePort();
    final HostConfig hostConfig = HostConfig.builder()
        .portBindings(createPortBinding(POSTGRESQL_PORT, localPort))
        .build();
    docker.pull(POSTGRES_9_2_IMAGE);
    this.container = docker.createContainer(ContainerConfig.builder()
        .image(POSTGRES_9_2_IMAGE)
        .hostConfig(hostConfig)
        .exposedPorts(String.valueOf(POSTGRESQL_PORT))
        .build());
    docker.startContainer(container.id());
    waitForDatabase(RETRIES);
    createDatabase();
    for (String script : scripts) {
      executeScript(script);
    }
  }

  public void executeScript(final String script) {
    executeScript(Optional.of(databaseName), script);
  }

  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(
        new DriverManagerDataSource(
            /* url */ getUrl(),
            /* username */ "postgres",
            /* password */ ""));
  }

  public JdbcTemplate jdbcTemplate(String database) {
    return new JdbcTemplate(
        new DriverManagerDataSource(
            /* url */ getUrl(database),
            /* username */ "postgres",
            /* password */ ""));
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
}
