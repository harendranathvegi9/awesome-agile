package org.awesomeagile.webapp.controller;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import org.awesomeagile.model.team.User;
import org.awesomeagile.webapp.controller.HackpadPostTest.HackpadStatus;
import org.awesomeagile.webapp.controller.HackpadPostTest.PadIdentity;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

@Controller
public class HackpadController {

    public static final String CLIENT_ID = "FiE4R5tmkef";
    public static final String SECRET = "eAvjusoCwiM4jG2yL3lUWZ8C7n3IOWT8";
    public static final String CREATE_URL = "https://hackpad.com/api/1.0/pad/create";
    public static final String UPDATE_URL = "https://hackpad.com/api/1.0/pad/{padId}/content";
    public static final String OPTIONS_URL = "https://hackpad.com/api/1.0/pad/{padId}/options";

    private final OAuthRestTemplate restTemplate;

    @Autowired
    public HackpadController(OAuthRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    private PadIdentity createHackpad(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>("Hackpad Title", headers);
        PadIdentity padIdentity = restTemplate.postForObject(CREATE_URL, entity, PadIdentity.class);
    }
    
    private HackpadStatus updateHackpadContent(PadIdentity padIdentity, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> updateEntity = new HttpEntity<>(
                "<html><body><p><b>Subheader</b></p><p>Regular text</p></body></html>", headers);
        HackpadStatus status = restTemplate.postForObject(UPDATE_URL, updateEntity, HackpadStatus.class,
                ImmutableMap.of("padId", padIdentity.getPadId()));
    }
    
    
    @RequestMapping(method = RequestMethod.POST, path = "/api/hackpad")
    @ResponseBody
    public URL createNewHackpad(@AuthenticationPrincipal AwesomeAgileSocialUser principal)
            throws MalformedURLException {

        User user = principal.getUser();

        BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
        resource.setConsumerKey(CLIENT_ID);
        resource.setSharedSecret(new SharedConsumerSecretImpl(SECRET));
        resource.setAdditionalRequestHeaders(ImmutableMap.of("email", "belov.stan@gmail.com"));
        resource.setAcceptsAuthorizationHeader(false);
        OAuthRestTemplate restTemplate = new OAuthRestTemplate(resource);


        return new URL("");
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

    private static class PadIdentity {

        @JsonProperty
        private String padId;

        public String getPadId() {
            return padId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PadIdentity that = (PadIdentity) o;
            return Objects.equals(padId, that.padId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(padId);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("padId", padId).toString();
        }
    }

}
