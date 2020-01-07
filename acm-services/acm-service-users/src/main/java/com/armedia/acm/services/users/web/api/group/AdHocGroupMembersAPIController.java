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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
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
import java.util.List;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class AdHocGroupMembersAPIController
{
    private Logger LOG = LogManager.getLogger(getClass());
    private GroupService groupService;

    @RequestMapping(value = "/group/{groupId}/members/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveMembersToAdHocGroup(@RequestBody List<String> members,
            @PathVariable("groupId") String groupId) throws AcmAppErrorJsonMsg
    {
        LOG.info("Add user members group: [{}]", groupId);
        try
        {
            groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
            return groupService.addUserMembersToGroup(members, groupId);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Failed to add user members to Ad Hoc Group", "ADHOC_GROUP", e);
        }
    }

    @RequestMapping(value = "/group/{groupId}/members/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeMembersFromAdHocGroup(@RequestBody List<String> members,
            @PathVariable("groupId") String groupId) throws AcmAppErrorJsonMsg
    {
        LOG.info("Remove user members from group: [{}]", groupId);
        try
        {
            groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
            return groupService.removeUserMembersFromGroup(members, groupId);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Failed to remove user members to Ad Hoc Group", "ADHOC_GROUP", e);
        }
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

}
