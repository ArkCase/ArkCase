package com.armedia.acm.plugins.wopi.api;

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

import com.armedia.acm.plugins.wopi.model.WopiSessionInfo;
import com.armedia.acm.plugins.wopi.model.WopiUserInfo;
import com.armedia.acm.plugins.wopi.service.WopiAcmService;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/latest/plugin/wopi/users")
public class WopiUserApiController
{
    private static final Logger log = LogManager.getLogger(WopiUserApiController.class);
    private WopiAcmService wopiService;
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WopiUserInfo getUserInfo(@RequestParam("acm_email_ticket") String token,
                                    Authentication authentication)
    {
        log.info("Getting user info per email ticket [{}]", token);
        AcmUser user = userDao.findByUserId(authentication.getName());
        return wopiService.getUserInfo(user, token);
    }

    @RequestMapping(value = "/resource/{file_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WopiSessionInfo getSessionInfo(@PathVariable("file_id") Long fileId, @RequestParam("acm_email_ticket") String token,
            Authentication authentication)
    {
        log.info("Getting wopi session info per email ticket [{}]", token);
        return wopiService.getSessionInfo(authentication, fileId, token);
    }

    public void setWopiService(WopiAcmService wopiService)
    {
        this.wopiService = wopiService;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
