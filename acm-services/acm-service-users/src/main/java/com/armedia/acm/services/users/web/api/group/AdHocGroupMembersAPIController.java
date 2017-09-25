package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.services.users.model.AcmUser;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

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
    public AcmGroup saveMembersToAdHocGroup(@RequestBody Set<AcmUser> members,
                                            @PathVariable("groupId") String groupId,
                                            @RequestParam(value = "addToAllParentGroups", required = false,
                                                    defaultValue = "false") String addToAllParentGroups)
    {
        LOG.info("Saving members to the group with ID = [{}]", groupId);
        return groupService.addMembersToAdHocGroup(members, groupId);
    }

    @RequestMapping(value = "/group/{groupId}/members/remove", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeMembersFromAdHocGroup(@RequestBody Set<AcmUser> members,
                                                @PathVariable("groupId") String groupId)
    {
        LOG.info("Removing members from group with ID = [{}]", groupId);
        return groupService.removeMembersFromAdHocGroup(members, groupId);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

}
