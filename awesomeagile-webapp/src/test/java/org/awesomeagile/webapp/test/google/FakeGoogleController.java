package org.awesomeagile.webapp.test.google;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Controller
public class FakeGoogleController {

  private static final String CODE = "code";
  private static final String AUTHORIZATION_CODE = "authorization_code";
  private static final int EXPIRATION_MILLIS = 3600000;
  public static final String BEARER = "Bearer";
  private String clientId;
  private String clientSecret;
  private List<String> redirectUriPrefixes = ImmutableList.of();
  private Map<String, AccessToken> codeToToken = new ConcurrentHashMap<>();
  private Set<String> knownTokens =
      Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  private Person person;

  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, path = "/oauth2/auth")
  public ResponseEntity<?> authenticate(
      @RequestParam("client_id")
      String clientId,
      @RequestParam(value = "client_secret", required = false)
      String clientSecret,
      @RequestParam("response_type")
      String responseType,
      @RequestParam("redirect_uri")
      String redirectUri,
      @RequestParam("scope")
      String scope
      ) {
    // Validate client_id and client_secret
    if (!this.clientId.equals(clientId)
        || (clientSecret != null && !this.clientSecret.equals(clientSecret))) {
      return ResponseEntity.<String>badRequest()
          .body("Wrong client_id or client_secret!");
    }
    if (!prefixMatches(redirectUri)) {
      return wrongRedirectUriResponse();
    }
    String code = RandomStringUtils.randomAlphanumeric(64);
    String token = RandomStringUtils.randomAlphanumeric(64);
    codeToToken.put(code,
        new AccessToken(token, scope, "", System.currentTimeMillis() + EXPIRATION_MILLIS));
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam(CODE, code);
    return ResponseEntity.status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION, builder.build().toUriString()).build();
  }

  @RequestMapping(value = "/oauth2/token", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getToken(
      @RequestParam("code") String code,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("grant_type") String grantType
  ) {
    if (AUTHORIZATION_CODE.equals(grantType)) {
      if (!prefixMatches(redirectUri)) {
        return wrongRedirectUriResponse();
      }
      if (!codeToToken.containsKey(code)) {
        return ResponseEntity.<String>badRequest().body("Wrong code!");
      }
      AccessToken accessToken = codeToToken.get(code);
      knownTokens.add(accessToken.getAccessToken());
      return ResponseEntity.ok(accessToken);
    }
    return ResponseEntity.<String>badRequest().body("Wrong grant_type!");
  }

  @RequestMapping(value = "/plus/v1/people/me", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Person getMe(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
    validateAuthorization(authorizationHeader);
    return person;
  }

  @ResponseBody
  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  private void validateAuthorization(String authorizationHeader) {
    if (StringUtils.startsWithIgnoreCase(authorizationHeader, BEARER)) {
      if (knownTokens.contains(
          StringUtils.removeStartIgnoreCase(authorizationHeader, BEARER).trim())) {
        return;
      }
    }
    throw new BadCredentialsException("Invalid authorization header: " + authorizationHeader);
  }

  private ResponseEntity<String> wrongRedirectUriResponse() {
    return ResponseEntity.<String>badRequest()
        .body("Wrong redirect_uri!");
  }

  private boolean prefixMatches(String redirectUri) {
    for (String prefix : redirectUriPrefixes) {
      if (redirectUri.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  public FakeGoogleController setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public FakeGoogleController setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

  public FakeGoogleController setRedirectUriPrefixes(
      List<String> redirectUriPrefixes) {
    this.redirectUriPrefixes = ImmutableList.copyOf(redirectUriPrefixes);
    return this;
  }

  public FakeGoogleController setPerson(Person person) {
    this.person = person;
    return this;
  }
}
