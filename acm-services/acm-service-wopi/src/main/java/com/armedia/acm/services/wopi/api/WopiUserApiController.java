package com.armedia.acm.services.wopi.api;

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
import com.armedia.acm.services.wopi.model.WopiUserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.time.Period;
import java.util.List;

@Controller
@RequestMapping(value = "/api/latest/wopi/users")
public class WopiUserApiController
{
    private static final Logger log = LoggerFactory.getLogger(WopiUserApiController.class);
    private AuthenticationTokenService tokenService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WopiUserInfo getUserInfo(@RequestParam("acm_email_ticket") String token, HttpSession session)
    {
        log.info("Getting user info per email ticket [{}]", token);
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        List<AuthenticationToken> tokens = tokenService.findByKey(token);
        Long tokenTtl = tokens.stream()
                .filter(it -> it.getStatus().equals("ACTIVE"))
                .findFirst().map(it -> it.getCreated().toInstant()
                        .plus(Period.ofDays(AuthenticationTokenService.EMAIL_TICKET_EXPIRATION_DAYS)).toEpochMilli())
                .orElse(0L);
        return new WopiUserInfo(user.getFullName(), user.getUserId(), user.getLang(), tokenTtl);
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }
}
