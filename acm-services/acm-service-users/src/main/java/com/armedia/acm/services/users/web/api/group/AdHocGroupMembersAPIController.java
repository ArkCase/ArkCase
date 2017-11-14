package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class AdHocGroupMembersAPIController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private GroupService groupService;

    @RequestMapping(value = "/group/{groupId}/members/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveMembersToAdHocGroup(@RequestBody List<String> members,
                                            @PathVariable("groupId") String groupId) throws AcmUserActionFailedException
    {
        LOG.info("Add user members group: [{}]", groupId);
        return groupService.addUserMembersToGroup(members, groupId);
    }

    @RequestMapping(value = "/group/{groupId}/members/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeMembersFromAdHocGroup(@RequestBody List<String> members,
                                                @PathVariable("groupId") String groupId) throws AcmUserActionFailedException
    {
        LOG.info("Remove user members from group: [{}]", groupId);
        return groupService.removeUserMembersFromGroup(members, groupId);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

}
