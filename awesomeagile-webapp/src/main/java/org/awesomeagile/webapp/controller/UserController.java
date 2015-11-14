package org.awesomeagile.webapp.controller;

import org.awesomeagile.model.team.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Controller
public class UserController {

  @RequestMapping(method = RequestMethod.GET, path = "/user")
  @ResponseBody
  public SocialUser getUserEmail(@AuthenticationPrincipal SocialUser principal) {
    return principal;
  }
}
