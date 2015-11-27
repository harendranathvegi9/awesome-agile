package org.awesomeagile.webapp.controller;

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

  /**
   * Returns the currently authenticated user
   * @param principal authenticated user as a {@link AwesomeAgileSocialUser} object
   * @return user currently logged in
   */
  @RequestMapping(method = RequestMethod.GET, path = "/api/user")
  @ResponseBody
  public User getCurrentUser(
      @AuthenticationPrincipal AwesomeAgileSocialUser principal) {
    return principal.getUser();
  }
}
