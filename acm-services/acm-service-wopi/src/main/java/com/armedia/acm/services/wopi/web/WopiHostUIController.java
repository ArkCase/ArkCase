package com.armedia.acm.services.wopi.web;

import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.wopi.model.WopiConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/office")
public class WopiHostUIController
{
    private static final Logger log = LoggerFactory.getLogger(WopiHostUIController.class);

    private AuthenticationTokenService tokenService;
    private WopiConfig wopiConfig;

    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}")
    public ModelAndView getWopiHostPage(Authentication authentication, @PathVariable Long fileId, HttpSession session)
    {
        log.info("Opening file with id [{}] per user [{}]", fileId, authentication.getName());

        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        List<AuthenticationToken> tokens = tokenService.findByTokenEmailAndFileId(user.getMail(), fileId);
        List<AuthenticationToken> activeTokens = tokens.stream()
                .filter(token -> token.getStatus().equals("ACTIVE"))
                .collect(Collectors.toList());
        String authenticationToken;
        if (activeTokens.isEmpty())
        {
            authenticationToken = tokenService.generateAndSaveAuthenticationToken(fileId, user.getMail(), authentication);
        }
        else
        {
            authenticationToken = activeTokens.get(0).getKey();
        }

        ModelAndView model = new ModelAndView();
        model.setViewName("wopi-host");
        model.addObject("url", wopiConfig.getWopiHostUrl(fileId, authenticationToken));
        return model;
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }

    public void setWopiConfig(WopiConfig wopiConfig)
    {
        this.wopiConfig = wopiConfig;
    }
}