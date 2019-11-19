package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" })
public class GetUsersByGroupAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private GroupService groupService;

    @RequestMapping(method = RequestMethod.GET, value = "/by-group/{group}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String info(Authentication auth, @PathVariable String group,
            @RequestParam(value = "status", required = false) String userStatus)
            throws SolrException
    {

        group = new String(Base64.getUrlDecoder().decode(group.getBytes()));
        log.debug("Getting users for group [{}]", group);
        return groupService.getUserMembersForGroup(group, Optional.ofNullable(userStatus), auth);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
