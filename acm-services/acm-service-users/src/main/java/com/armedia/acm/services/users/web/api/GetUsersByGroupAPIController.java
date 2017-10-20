package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.service.group.GroupService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" })
public class GetUsersByGroupAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private GroupService groupService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by-group/{group}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String info(Authentication auth, @PathVariable String group,
                       @RequestParam(value = "status", required = false) String userStatus)
            throws MuleException
    {
        log.debug("Getting users for group [{}]", group);
        return groupService.getUserMembersForGroup(group, Optional.ofNullable(userStatus), auth);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
