package com.armedia.acm.services.users.web.api.group;

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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class SupervisorGroupAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());
    private GroupService groupService;

    @RequestMapping(value = "/group/{groupId}/supervisor/save/{applyToAll}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup addSupervisorToGroup(@RequestBody AcmUser supervisor,
            @PathVariable("groupId") String groupId,
            @PathVariable("applyToAll") boolean applyToAll) throws AcmUserActionFailedException
    {
        groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
        LOG.info("Saving supervisor to the group with ID = [{}]", groupId);
        return groupService.setSupervisor(supervisor, groupId, applyToAll);
    }

    @RequestMapping(value = "/group/{groupId}/supervisor/remove/{applyToAll}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeSupervisorFromGroup(@PathVariable("groupId") String groupId,
            @PathVariable("applyToAll") boolean applyToAll) throws AcmUserActionFailedException
    {
        groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
        LOG.info("Removing supervisor from the group with ID = [{}]", groupId);
        return groupService.removeSupervisor(groupId, applyToAll);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
