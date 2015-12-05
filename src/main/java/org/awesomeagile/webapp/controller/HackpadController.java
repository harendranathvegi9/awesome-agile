package org.awesomeagile.webapp.controller;

import java.net.MalformedURLException;
import java.net.URL;

import org.awesomeagile.model.team.User;
import org.awesomeagile.webapp.security.AwesomeAgileSocialUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HackpadController {
    
    @RequestMapping(method = RequestMethod.POST, path = "/api/hackpad")
    @ResponseBody
    public URL createNewHackpad(
            @AuthenticationPrincipal AwesomeAgileSocialUser principal) throws MalformedURLException {
        
        User user = principal.getUser();

        BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
        OAuthRestTemplate restTemplate = new OAuthRestTemplate(resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        
        HttpEntity<String> entity = new HttpEntity<String>("Hackpad Title", headers);
        String urlString = "https://hackpad.com/api/1.0/pad/create";
        restTemplate.exchange(urlString, HttpMethod.POST, entity, String.class);

        return new URL("");
    }

}
