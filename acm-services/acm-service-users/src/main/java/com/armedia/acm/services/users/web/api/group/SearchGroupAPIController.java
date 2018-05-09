package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.services.users.service.group.GroupService;

import org.mule.api.MuleException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/groups", "/api/latest/groups" })
public class SearchGroupAPIController
{
    private GroupService groupService;

    @RequestMapping(params = { "nameFq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsByName(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "nameFq") String nameFilter,
            Authentication auth) throws MuleException
    {
        return groupService.getGroupsByNameFilter(auth, nameFilter, startRow, maxRows, sortBy, sortDirection);
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
