package org.awesomeagile.webapp.test;

import static org.junit.Assert.assertEquals;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A test that brings up a local PostgreSQL instance within a Docker container.
 * It is assumed that Docker environment variables are already set up.
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class DatabaseIntegrationTest {

  public static final int TIMEOUT = 5;
  public static final int RETRIES = 30;
  public static final int PORT = 5432;
  private String hostName;
  private ContainerCreation container;
  private DefaultDockerClient docker;

  @Before
  public void setUp() throws Exception {
    // Create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
    docker = DefaultDockerClient.fromEnv().build();
    hostName = docker.getHost();
    // Bind container ports to host ports
    final HostConfig hostConfig = HostConfig.builder()
        .portBindings(createPortBindings(PORT))
        .build();
    this.container = docker.createContainer(ContainerConfig.builder()
        .image("postgres:9.2")
        .hostConfig(hostConfig)
        .exposedPorts(String.valueOf(PORT))
        .build());
    docker.startContainer(container.id());
    waitForDatabase(RETRIES);
    System.out.println("PostgreSQL server available at " + getUrl());
  }

  @After
  public void tearDown() throws Exception {
    docker.stopContainer(container.id(), TIMEOUT);
    docker.removeContainer(container.id());
  }

  private static Map<String, List<PortBinding>> createPortBindings(int... ports) {
    final Map<String, List<PortBinding>> portBindings = new HashMap<>();
    for (int port : ports) {
      List<PortBinding> hostPorts = new ArrayList<>();
      hostPorts.add(PortBinding.of("0.0.0.0", port));
      portBindings.put(String.valueOf(port), hostPorts);
    }
    return portBindings;
  }

  @Test
  public void testPostgres() throws Exception {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(
        new DriverManagerDataSource(
            /* url */ getUrl(),
            /* username */ "postgres",
            /* password */ ""));
    int random = RandomUtils.nextInt(1000);
    Integer integer = jdbcTemplate.queryForObject("select " + random, Integer.class);
    assertEquals(random, integer.intValue());
    System.out.printf("select %d returns: %d\n", random, integer);
  }

  private void waitForDatabase(int retries) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(
        new DriverManagerDataSource(
            /* url */ getUrl(),
            /* username */ "postgres",
            /* password */ ""));
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

  private String getUrl() {
    return String.format("jdbc:postgresql://%s:%d/", hostName, PORT);
  }
}
