package org.awesomeagile.webapp.controller;

import org.awesomeagile.webapp.security.AuthenticationStatus;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Controller
public class UserController {

  @RequestMapping(method = RequestMethod.GET, path = "/authstatus")
  @ResponseBody
  public AuthenticationStatus getAuthenticationStatus(
      @AuthenticationPrincipal AwesomeAgileSocialUser principal) {
    if (principal == null) {
      return new AuthenticationStatus(false, null);
    }
    return new AuthenticationStatus(true, principal.getUser());
  }
}
