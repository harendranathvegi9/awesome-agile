package org.awesomeagile.webapp.controller;

import org.awesomeagile.model.team.User;
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

  @RequestMapping(method = RequestMethod.GET, path = "/api/user")
  @ResponseBody
  public User getAuthenticationStatus(
      @AuthenticationPrincipal AwesomeAgileSocialUser principal) {
    return principal.getUser();
  }
}
