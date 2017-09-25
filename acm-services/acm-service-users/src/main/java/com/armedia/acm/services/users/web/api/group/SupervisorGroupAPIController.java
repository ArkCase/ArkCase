package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class SupervisorGroupAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private GroupService groupService;

    @RequestMapping(value = "/group/{groupId}/supervisor/save/{applyToAll}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup addSupervisorToGroup(@RequestBody AcmUser supervisor,
                                         @PathVariable("groupId") String groupId,
                                         @PathVariable("applyToAll") boolean applyToAll)
    {
        LOG.info("Saving supervisor to the group with ID = [{}]", groupId);
        return groupService.setSupervisor(supervisor, groupId, applyToAll);
    }

    @RequestMapping(value = "/group/{groupId}/supervisor/remove/{applyToAll}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeSupervisorFromGroup(@PathVariable("groupId") String groupId,
                                              @PathVariable("applyToAll") boolean applyToAll) throws AcmUserActionFailedException
    {
        LOG.info("Removing supervisor from the group with ID = [{}]", groupId);
        return groupService.removeSupervisor(groupId, applyToAll);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
