package com.armedia.acm.services.wopi.web;

/*-
 * #%L
 * ACM Service: Wopi service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
