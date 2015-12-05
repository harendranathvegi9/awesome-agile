package org.awesomeagile.integrations.hackpad;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.awesomeagile.annotations.Hackpad;
import org.awesomeagile.model.document.PadIdentity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

public class HackpadClient {
    private static final String CREATE_URL = "/api/1.0/pad/create";
    private static final String UPDATE_URL = "/api/1.0/pad/{padId}/content";
    private static final String GET_URL = "/api/1.0/pad/{padId}/content/latest.html";

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public HackpadClient(@Hackpad RestTemplate restTemplate,
            @Hackpad String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public String getHackpad(PadIdentity padIdentity) {
        return restTemplate.getForObject(fullUrl(GET_URL), String.class, ImmutableMap.of("padId", padIdentity.getPadId()));
    }

    public PadIdentity createHackpad(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(title, headers);
        return restTemplate.postForObject(fullUrl(CREATE_URL), entity, PadIdentity.class);
    }

    public HackpadStatus updateHackpad(PadIdentity padIdentity, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> updateEntity = new HttpEntity<>(
                "<html><body><p><b>Subheader</b></p><p>Regular text</p></body></html>", headers);
        return restTemplate.postForObject(fullUrl(UPDATE_URL), updateEntity, HackpadStatus.class,
                ImmutableMap.of("padId", padIdentity.getPadId()));

    }

    public static class HackpadStatus {
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

    public String fullUrl(String apiUrl) {
        return baseUrl + apiUrl;
    }

}
