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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.ImmutableSet;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.awesomeagile.error.ResourceNotFoundException;
import org.awesomeagile.integrations.hackpad.HackpadClient;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.awesomeagile.model.document.CreatedDocument;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.model.team.User;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadControllerTest {

  private static final String SHINY_TEMPLATE = "shinytemplate";
  private static final String BASE_URL = "http://hackpad.org/";
  private TestHackpadClient hackpadClient;
  private HackpadController controller;
  private Map<String, HackpadDocumentTemplate> templates;
  private String shinyTemplatePadId;

  @Before
  public void setUp() throws Exception {
    hackpadClient = new TestHackpadClient(BASE_URL);
    templates = new HashMap<>();
    controller = new HackpadController(hackpadClient, templates);
    shinyTemplatePadId = RandomStringUtils.randomAlphanumeric(8);
    PadIdentity padIdentity = new PadIdentity(shinyTemplatePadId);
    templates.put(
        SHINY_TEMPLATE,
        new HackpadDocumentTemplate("New shiny template", padIdentity));
    hackpadClient.getContentByPadId().put(
        padIdentity,
        "<html><body>New and shiny document</body></html>");
  }

  @Test
  public void testCreateNewHackpadSuccess() throws Exception {
    CreatedDocument document = controller.createNewHackpad(
        new AwesomeAgileSocialUser(new User().setPrimaryEmail("user@domain.com"), ImmutableSet.of()),
        SHINY_TEMPLATE);
    assertNotNull(document);
    String padId = StringUtils.removeStart(document.getUrl(), BASE_URL);
    String content = hackpadClient.getContentByPadId().get(new PadIdentity(padId));
    assertNotNull(content);
    assertEquals(content, "<html><body>New and shiny document</body></html>");
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testCreateNewHackpadBadDocumentType() throws Exception {
    controller.createNewHackpad(
        new AwesomeAgileSocialUser(new User().setPrimaryEmail("user@domain.com"), ImmutableSet.of()),
        "other_template");
  }

  private static class TestHackpadClient implements HackpadClient {

    private final String baseUrl;
    private final Map<PadIdentity, String> contentByPadId = new HashMap<>();

    private TestHackpadClient(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    @Override
    public String getHackpad(PadIdentity padIdentity) {
      if (!contentByPadId.containsKey(padIdentity)) {
        throw new RuntimeException("not found");
      }
      return contentByPadId.get(padIdentity);
    }

    @Override
    public PadIdentity createHackpad(String title) {
      PadIdentity newPadId = new PadIdentity(RandomStringUtils.randomAlphanumeric(8));
      contentByPadId.put(newPadId, title);
      return newPadId;
    }

    @Override
    public void updateHackpad(PadIdentity padIdentity, String content) {
      if (!contentByPadId.containsKey(padIdentity)) {
        throw new RuntimeException("not found");
      }
      contentByPadId.put(padIdentity, content);
    }

    @Override
    public String fullUrl(String apiUrl) {
      return baseUrl + apiUrl;
    }

    public Map<PadIdentity, String> getContentByPadId() {
      return contentByPadId;
    }

    @Override
    public void createUser(String email, String name) {
        // TODO Auto-generated method stub
        
    }
  }
}