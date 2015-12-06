package org.awesomeagile.testing.hackpad;

import org.apache.commons.lang3.RandomStringUtils;
import org.awesomeagile.integrations.hackpad.HackpadStatus;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  public String getHackpad(@PathVariable("padId") String padId) {
    return hackpads.get(new PadIdentity(padId));
  }

  @RequestMapping(value = "/api/1.0/pad/create", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PadIdentity createHackpad(@RequestBody String content) {
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
      @RequestBody String content) {
    PadIdentity padIdentity = new PadIdentity(padId);
    if (!hackpads.containsKey(padIdentity)) {
      return new HackpadStatus(false);
    }
    hackpads.put(padIdentity, content);
    return new HackpadStatus(true);
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
