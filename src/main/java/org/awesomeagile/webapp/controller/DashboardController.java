package org.awesomeagile.webapp.controller;

import com.google.common.collect.ImmutableMap;

import org.awesomeagile.dao.DocumentRepository;
import org.awesomeagile.model.Dashboard;
import org.awesomeagile.model.document.Document;
import org.awesomeagile.model.document.DocumentType;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Controller
public class DashboardController {

  private final DocumentRepository documentRepository;

  @Autowired
  public DashboardController(DocumentRepository documentRepository) {
    this.documentRepository = documentRepository;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/api/dashboard")
  @ResponseBody
  public Dashboard getDashboard(@AuthenticationPrincipal AwesomeAgileSocialUser principal) {
    Collection<Document> documents = documentRepository.findAllByUserId(
        principal.getUser().getId());
    Map<DocumentType, String> documentsByType = new HashMap<>();
    for (Document document : documents) {
      if (!documentsByType.containsKey(document.getDocumentType())) {
        documentsByType.put(document.getDocumentType(), document.getUrl());
      }
    }
    return new Dashboard()
        .setDocuments(ImmutableMap.copyOf(documentsByType))
        .setUser(principal.getUser());
  }
}
