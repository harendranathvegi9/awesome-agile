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

import com.google.common.collect.ImmutableMap;

import org.awesomeagile.annotations.Hackpad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public String fullUrl(String apiUrl) {
        return baseUrl + apiUrl;
    }

}
