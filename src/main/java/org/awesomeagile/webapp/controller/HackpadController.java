package org.awesomeagile.webapp.controller;

import org.awesomeagile.error.ResourceNotFoundException;
import org.awesomeagile.integrations.hackpad.HackpadClient;
import org.awesomeagile.model.document.CreatedDocument;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.model.document.PadIdentity;
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

@Controller
public class HackpadController {
    private final Map<String, HackpadDocumentTemplate> templates;
    private final HackpadClient client;

    @Autowired
    public HackpadController(HackpadClient client,
            Map<String, HackpadDocumentTemplate> templates) {
        this.client = client;
        this.templates = templates;
    }

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
