package org.awesomeagile.webapp.test.google;

import org.awesomeagile.testutils.NetworkUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.util.List;

/**
 * A fake server that emulates Google OAuth2 authentication and a limited subset of Google Plus API
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class FakeGoogleServer extends ExternalResource {

  private static final String CONTEXT_PATH = "/g";
  private static final String CONFIG_LOCATION = "org.awesomeagile.webapp.test.google";
  private static final String MAPPING_URL = "/*";

  private final int port;
  private Server server;
  private WebApplicationContext webApplicationContext;
  @Autowired
  private FakeGoogleController controller;

  public FakeGoogleServer() {
    try {
      this.port = NetworkUtils.findAvailablePort();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to find a free port", ex);
    }
  }

  public int getPort() {
    return port;
  }

  public String getEndpoint() {
    return "http://localhost:" + port + CONTEXT_PATH + "/";
  }

  public FakeGoogleServer setClientId(String clientId) {
    controller.setClientId(clientId);
    return this;
  }

  public FakeGoogleServer setClientSecret(String clientSecret) {
    controller.setClientSecret(clientSecret);
    return this;
  }

  public FakeGoogleServer setRedirectUriPrefixes(List<String> redirectUriPrefixes) {
    controller.setRedirectUriPrefixes(redirectUriPrefixes);
    return this;
  }

  public FakeGoogleServer setPerson(Person person) {
    controller.setPerson(person);
    return this;
  }

  @Override
  protected void before() throws Throwable {
    start();
  }

  @Override
  protected void after() {
    stop();
  }

  private void start() {
    try {
      startJetty(port);
    } catch (Exception ex) {
      throw new RuntimeException("Unable to start fake Google OAuth server", ex);
    }
  }

  private void stop() {
    try {
      server.stop();
    } catch (Exception ex) {
      throw new RuntimeException("Unable to stop fake Google OAuth server", ex);
    }
  }

  private void startJetty(int port) throws Exception {
    this.server = new Server(port);
    webApplicationContext = getContext();
    server.setHandler(getServletContextHandler(webApplicationContext));
    server.start();
    webApplicationContext.getAutowireCapableBeanFactory().autowireBean(this);
  }

  private static ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
    ServletContextHandler contextHandler = new ServletContextHandler();
    contextHandler.setErrorHandler(null);
    contextHandler.setContextPath(CONTEXT_PATH);
    contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
    contextHandler.addEventListener(new ContextLoaderListener(context));
    return contextHandler;
  }

  private static WebApplicationContext getContext() {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.setConfigLocation(CONFIG_LOCATION);
    return context;
  }
}
