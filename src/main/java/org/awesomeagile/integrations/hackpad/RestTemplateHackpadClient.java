package org.awesomeagile.integrations.hackpad;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.awesomeagile.annotations.Hackpad;
import org.awesomeagile.model.document.PadIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
public class RestTemplateHackpadClient implements HackpadClient {
    private static final String CREATE_URL = "/api/1.0/pad/create";
    private static final String UPDATE_URL = "/api/1.0/pad/{padId}/content";
    private static final String GET_URL = "/api/1.0/pad/{padId}/content/latest.html";

    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public RestTemplateHackpadClient(
        @Hackpad RestTemplate restTemplate,
        @Hackpad String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String getHackpad(PadIdentity padIdentity) {
        return restTemplate.getForObject(fullUrl(GET_URL), String.class, ImmutableMap.of("padId", padIdentity.getPadId()));
    }

    @Override
    public PadIdentity createHackpad(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(title, headers);
        return restTemplate.postForObject(fullUrl(CREATE_URL), entity, PadIdentity.class);
    }

    @Override
    public void updateHackpad(PadIdentity padIdentity, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> updateEntity = new HttpEntity<>(content, headers);
        HackpadStatus status = restTemplate.postForObject(
            fullUrl(UPDATE_URL),
            updateEntity,
            HackpadStatus.class,
            ImmutableMap.of("padId", padIdentity.getPadId()));
        if (!status.isSuccess()) {
            throw new RuntimeException("Failure to update a Hackpad.");
        }
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

    @Override
    public String fullUrl(String apiUrl) {
        return baseUrl + apiUrl;
    }

}
