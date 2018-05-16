package com.armedia.acm.services.wopi.web;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/office")
public class WopiHostUIController
{
    private static final Logger log = LoggerFactory.getLogger(WopiHostUIController.class);

    private AuthenticationTokenService tokenService;

    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}")
    public ModelAndView getWopiHostPage(Authentication authentication, @PathVariable Long fileId)
    {
        log.info("Opening file with id [{}] per user [{}]", fileId, authentication.getName());
        String token = tokenService.getTokenForAuthentication(authentication);
        ModelAndView model = new ModelAndView();
        model.addObject("access_token", token);
        model.setViewName("wopi-host");
        //TODO: Send all information about the host as part of the url which loads the embedded wopi host content
        //access_token_param_name
        //protocol
        //domain
        //port
        //context
        //resources_url
        return model;
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }
}
