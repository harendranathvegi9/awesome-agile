package org.awesomeagile.webapp.controller;

import static org.apache.commons.lang.math.RandomUtils.nextLong;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.awesomeagile.dao.DocumentRepository;
import org.awesomeagile.model.Dashboard;
import org.awesomeagile.model.document.Document;
import org.awesomeagile.model.document.DocumentType;
import org.awesomeagile.model.team.User;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class DashboardControllerTest {

  private DocumentRepository documentRepository;
  private long userId;

  /**
   * Object under test
   */
  private DashboardController controller;

  @Before
  public void setUp() throws Exception {
    userId = nextLong();
    documentRepository = mock(DocumentRepository.class);
    controller = new DashboardController(documentRepository);
  }

  @Test
  public void testGetDashboard() {
    Document one = document(DocumentType.DEFINITION_OF_READY);
    Document two = document(DocumentType.DEFINITION_OF_DONE);
    when(documentRepository.findAllByUserId(userId))
        .thenReturn(ImmutableList.of(one, two));
    Dashboard dashboard = controller.getDashboard(
        new AwesomeAgileSocialUser(user(), ImmutableSet.of()));
    assertNotNull(dashboard);
    assertEquals(user(), dashboard.getUser());
    assertEquals(
        ImmutableMap.of(
            DocumentType.DEFINITION_OF_READY, one.getUrl(),
            DocumentType.DEFINITION_OF_DONE, two.getUrl()),
        ImmutableMap.copyOf(dashboard.getDocuments()));
  }

  @Test
  public void testGetDashboardDuplicateDocuments() throws Exception {
    Document one = document(DocumentType.DEFINITION_OF_READY);
    Document two = document(DocumentType.DEFINITION_OF_READY);
    when(documentRepository.findAllByUserId(userId))
        .thenReturn(ImmutableList.of(one, two));
    Dashboard dashboard = controller.getDashboard(
        new AwesomeAgileSocialUser(user(), ImmutableSet.of()));
    assertNotNull(dashboard);
    assertEquals(user(), dashboard.getUser());
    assertEquals(
        ImmutableMap.of(
            DocumentType.DEFINITION_OF_READY, one.getUrl()),
        ImmutableMap.copyOf(dashboard.getDocuments()));
  }

  @Test
  public void testGetDashboardNoDocuments() throws Exception {
    when(documentRepository.findAllByUserId(userId)).thenReturn(ImmutableList.of());
    Dashboard dashboard = controller.getDashboard(
        new AwesomeAgileSocialUser(user(), ImmutableSet.of()));
    assertNotNull(dashboard);
    assertEquals(user(), dashboard.getUser());
    assertTrue(dashboard.getDocuments().isEmpty());
  }

  private User user() {
    User user = new User()
        .setPrimaryEmail("user@domain.com");
    user.setId(userId);
    return user;
  }

  private static Document document(DocumentType type) {
    return new Document(type, randomAlphanumeric(32));
  }
}