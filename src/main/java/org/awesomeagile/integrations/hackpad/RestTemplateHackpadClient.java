package org.awesomeagile.integrations.hackpad;

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

    /**
     * Create an instance of the RestTemplate based Hackpad Client
     * @param restTemplate A RestTemplate instance
     * @param baseUrl The base URL the client should use for API calls
     */
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

    /**
     * An object to represent status responses for Hackpad API calls
     * @author phheller
     *
     */
    public static class HackpadStatus {
        @JsonProperty
        private boolean success;

        /**
         * Indicate call success
         * @return True on a successful call, false otherwise
         */
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
