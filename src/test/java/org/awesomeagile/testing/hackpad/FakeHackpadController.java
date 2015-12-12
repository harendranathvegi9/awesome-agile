package org.awesomeagile.testing.hackpad;

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

import org.apache.commons.lang3.RandomStringUtils;
import org.awesomeagile.integrations.hackpad.HackpadStatus;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Controller
class FakeHackpadController {

  private String clientId;
  private String clientSecret;
  private Map<PadIdentity, String> hackpads = new ConcurrentHashMap<>();

  @RequestMapping(
      value = {"/{padId:.*}"},
      method = RequestMethod.GET,
      produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String renderHackpad(@PathVariable("padId") String padId) {
    return hackpads.get(new PadIdentity(padId));
  }

  @RequestMapping(
      value = {"/api/1.0/pad/{padId}/content/latest.html"},
      method = RequestMethod.GET,
      produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String getHackpad(
      @PathVariable("padId") String padId,
      @RequestParam("oauth_consumer_key") String key) {
    if (!clientId.equals(key)) {
      throw new BadCredentialsException("Invalid client ID: " + key);
    }
    return hackpads.get(new PadIdentity(padId));
  }

  @RequestMapping(value = "/api/1.0/pad/create", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PadIdentity createHackpad(
      @RequestBody String content,
      @RequestParam("oauth_consumer_key") String key) {
    if (!clientId.equals(key)) {
      throw new BadCredentialsException("Invalid client ID: " + key);
    }
    PadIdentity padIdentity = new PadIdentity(RandomStringUtils.randomAlphanumeric(8));
    hackpads.put(padIdentity, content);
    return padIdentity;
  }

  @RequestMapping(
      value = "/api/1.0/pad/{padId}/content",
      method = RequestMethod.POST,
      consumes = MediaType.TEXT_HTML_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public HackpadStatus updateHackpad(
      @PathVariable("padId") String padId,
      @RequestBody String content,
      @RequestParam("oauth_consumer_key") String key) {
    if (!clientId.equals(key)) {
      throw new BadCredentialsException("Invalid client ID: " + key);
    }
    PadIdentity padIdentity = new PadIdentity(padId);
    if (!hackpads.containsKey(padIdentity)) {
      return new HackpadStatus(false);
    }
    hackpads.put(padIdentity, content);
    return new HackpadStatus(true);
  }

  @ResponseBody
  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  public FakeHackpadController setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public FakeHackpadController setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

  public Map<PadIdentity, String> getHackpads() {
    return hackpads;
  }
}
