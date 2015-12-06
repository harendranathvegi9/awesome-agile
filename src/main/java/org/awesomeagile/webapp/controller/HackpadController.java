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

import org.awesomeagile.error.ResourceNotFoundException;
import org.awesomeagile.integrations.hackpad.HackpadClient;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.awesomeagile.model.document.CreatedDocument;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * REST controller to create Hackpad shared documents on behalf of a caller
 * @author phheller
 *
 */
@Controller
public class HackpadController {
    private final Map<String, HackpadDocumentTemplate> templates;
    private final HackpadClient client;

    /**
     * Create the Hackpad controller
     * @param client A Hackpad client instance, against which API calls are added.
     * @param templates A Map of Template instances for various document types
     */
    @Autowired
    public HackpadController(HackpadClient client,
            Map<String, HackpadDocumentTemplate> templates) {
        this.client = client;
        this.templates = templates;
    }

    /**
     * Create a Hackpad on behalf of an authenticated caller
     * @param principal The entity requesting the Hackpad creation
     * @param documentType The type of document the entity wishes to be created
     * @return A CreatedDocument instance, from which the document URL can be
     *  retrieved.
     * @throws MalformedURLException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/api/hackpad/{doctype}")
    @ResponseBody
    public CreatedDocument createNewHackpad(@AuthenticationPrincipal AwesomeAgileSocialUser principal,
            @PathVariable("doctype") String documentType) throws MalformedURLException {
        HackpadDocumentTemplate template = templates.get(documentType);
        if (template == null) {
            throw new ResourceNotFoundException("Bad document type");
        }
        PadIdentity identity = client.createHackpad(template.getTitle());
        client.updateHackpad(identity, client.getHackpad(template.getPadIdentity()));
        return new CreatedDocument(client.fullUrl(identity.getPadId()));
    }
}
