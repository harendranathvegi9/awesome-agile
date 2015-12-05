package org.awesomeagile.webapp.controller;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.awesomeagile.annotations.Hackpad;
import org.awesomeagile.error.ResourceNotFoundException;
import org.awesomeagile.model.document.HackpadDocumentTemplate;
import org.awesomeagile.model.document.PadIdentity;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

@Controller
public class HackpadController {
    private static final String CREATE_URL = "/api/1.0/pad/create";
    private static final String UPDATE_URL = "/api/1.0/pad/{padId}/content";
    private static final String GET_URL = "/api/1.0/pad/{padId}/content/latest.html";

    private final OAuthRestTemplate restTemplate;
    private final String baseUrl;
    private final Map<String, HackpadDocumentTemplate> templates;

    @Autowired
    public HackpadController(
        @Hackpad OAuthRestTemplate restTemplate,
        @Hackpad String baseUrl,
        Map<String, HackpadDocumentTemplate> templates) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.templates = templates;
    }

    private String getHackpad(PadIdentity padIdentity) {
        return restTemplate.getForObject(
            GET_URL,
            String.class,
            ImmutableMap.of("padId", padIdentity.getPadId()));
    }

    private PadIdentity createHackpad(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>("Hackpad Title", headers);
        return restTemplate.postForObject(fullUrl(CREATE_URL), entity, PadIdentity.class);
    }
    
    private HackpadStatus updateHackpadContent(PadIdentity padIdentity, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> updateEntity = new HttpEntity<>(
                "<html><body><p><b>Subheader</b></p><p>Regular text</p></body></html>", headers);
        return restTemplate.postForObject(fullUrl(UPDATE_URL), updateEntity, HackpadStatus.class,
                ImmutableMap.of("padId", padIdentity.getPadId()));

    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/hackpad/{doctype}")
    @ResponseBody
    public String createNewHackpad(
        @AuthenticationPrincipal AwesomeAgileSocialUser principal,
        @PathVariable("docType") String documentType)
            throws MalformedURLException {
        HackpadDocumentTemplate template = templates.get(documentType);
        if (template == null) {
            throw new ResourceNotFoundException("Bad document type");
        }
        PadIdentity identity = createHackpad(template.getTitle());
        updateHackpadContent(identity, getHackpad(template.getPadIdentity()));
        return fullUrl(identity.getPadId());
    }

    private String fullUrl(String apiUrl) {
        try {
            return new URI(baseUrl).resolve(apiUrl).toString();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class HackpadStatus {
        @JsonProperty
        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            HackpadStatus that = (HackpadStatus) o;
            return Objects.equals(success, that.success);
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("success", success).toString();
        }
    }

}
