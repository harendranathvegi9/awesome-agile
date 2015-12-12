package org.awesomeagile.webapp.controller;

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
