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
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class AcmGroupAPIController
{
    private Logger LOG = LogManager.getLogger(getClass());

    private GroupService groupService;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/groups/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking groups.");

        return groupService.buildGroupsSolrQuery(auth, startRow, maxRows, sortBy, sortDirection);
    }

    @RequestMapping(value = "/groups/adhoc", params = { "fq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroups(@RequestParam(value = "fq") String fq,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking groups.");

        String solrQuery = groupService.buildGroupsAdHocByNameSolrQuery(fq);

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortDirection);
    }

    @RequestMapping(value = "/{userId:.+}/groups", params = {
            "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForUser(@PathVariable("userId") String userId, @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking groups and subgroups from Solr for specific user.");

        String solrQuery = groupService.buildGroupsForUserSolrQuery(authorized, userId);

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        String rowQueryParameters = "fq=hidden_b:false";

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection, rowQueryParameters);
    }

    @RequestMapping(value = "/{groupName:.+}/groups/adhoc", params = {
            "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getMemberGroups(@PathVariable("groupName") String groupName, @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "groupType") String groupType,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws SolrException
    {
        groupName = new String(Base64.getUrlDecoder().decode(groupName.getBytes()));
        LOG.info("Taking groups from Solr for specific group.");
        return groupService.getAdHocMemberGroups(auth, startRow, maxRows, sortBy, sortDirection, authorized, groupName,
                groupType);
    }

    @RequestMapping(value = "/{userId:.+}/groups", params = {
            "fq", "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForUserByName(@PathVariable(value = "userId") String userId,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "fq") String searchFilter,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking groups and subgroups from Solr for specific user by name.");

        String solrQuery = getGroupService().buildGroupsForUserByNameSolrQuery(authorized, userId, searchFilter);

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @RequestMapping(value = "/{groupId:.+}/groups/adhoc", params = {
            "fq", "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getMemberGroupsByName(@PathVariable(value = "groupId") String groupId,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "groupType") String groupType,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "fq") String searchFilter,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking groups and subgroups from Solr for specific group by name.");

        return groupService.getAdHocMemberGroupsByMatchingName(auth, startRow, maxRows, sortBy, sortDirection,
                authorized, groupId, searchFilter, groupType);
    }

    @RequestMapping(value = "/groups/adhoc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAdhocGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws SolrException
    {
        LOG.info("Taking ad-hoc groups from Solr.");

        String solrQuery = groupService.buildGroupsAdHocSolrQuery();

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @RequestMapping(value = "/{directory:.+}/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroupsByDirectory(@PathVariable String directory,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws SolrException
    {

        LOG.info("Taking groups by directory from Solr.");

        StringBuilder solrQuery = new StringBuilder();
        solrQuery.append("object_type_s:GROUP");

        if (directory.length() > 0)
        {
            solrQuery.append(" AND directory_name_s:").append(directory).append(" AND status_lcs:ACTIVE");
        }

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery.toString());

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery.toString(), startRow, maxRows,
                sort);

    }

    @RequestMapping(value = "/group/{groupId:.+}/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroup(@PathVariable("groupId") String groupId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws Exception
    {

        // we need to decode base64 encoded group id because can contain characters which can interfere with url
        groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
        LOG.info("Taking group from Solr with ID = [{}]", groupId);

        String query = "object_id_s:" + groupId
                + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), query);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);
    }

    @RequestMapping(value = "/group/{groupId:.+}/get/subgroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSubGroups(@PathVariable("groupId") String groupId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws SolrException
    {

        // we need to decode base64 encoded group id because can contain characters which can interfere with url
        groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));

        LOG.info("Taking subgroups from Solr with ID = [{}] " + groupId);

        return groupService.getGroupsByParent(groupId, startRow, maxRows, sort, auth);
    }

    @RequestMapping(value = "/group/get/toplevel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTopLevelGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "groupSubtype", required = false) List<String> groupSubtype,
            Authentication auth) throws Exception
    {
        LOG.info("Taking all top level groups from Solr.");

        return groupService.getTopLevelGroups(groupSubtype, startRow, maxRows, sort, auth);
    }

    @RequestMapping(value = "/group/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveGroup(@RequestBody AcmGroup group) throws AcmAppErrorJsonMsg
    {
        LOG.info("Saving ad-hoc group [{}]", group.getName());
        try
        {
            return groupService.createGroup(group);
        }
        catch (AcmObjectAlreadyExistsException e)
        {
            AcmAppErrorJsonMsg errorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!", "GROUP", "name", e);
            errorJsonMsg.putExtra("group", group);
            throw errorJsonMsg;
        }
    }

    @RequestMapping(value = "/group/save/{subGroupId:.+}/{parentId:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup addGroupMember(@PathVariable("subGroupId") String subGroupId, @PathVariable("parentId") String parentId)
            throws AcmCreateObjectFailedException
    {
        // we need to decode base64 encoded group id because can contain characters which can interfere with url
        subGroupId = new String(Base64.getUrlDecoder().decode(subGroupId.getBytes()));
        parentId = new String(Base64.getUrlDecoder().decode(parentId.getBytes()));
        LOG.info("Saving ad-hoc subgroup with id [{}]", subGroupId);
        return groupService.addGroupMember(subGroupId, parentId);
    }

    @RequestMapping(value = "/group/{parentId:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmGroup> addGroupMembers(@PathVariable("parentId") String parentId, @RequestBody List<String> members)
            throws AcmCreateObjectFailedException
    {
        // we need to decode base64 encoded group id because can contain characters which can interfere with url
        parentId = new String(Base64.getUrlDecoder().decode(parentId.getBytes()));
        LOG.info("Saving ad-hoc subgroups in parent [{}]", parentId);
        return groupService.addGroupMembers(parentId, members);
    }

    @RequestMapping(value = "/group/save/{parentId:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveSubGroup(@RequestBody AcmGroup subGroup,
            @PathVariable("parentId") String parentId) throws AcmAppErrorJsonMsg, AcmCreateObjectFailedException
    {

        // we need to decode base64 encoded group id because can contain characters which can interfere with url
        parentId = new String(Base64.getUrlDecoder().decode(parentId.getBytes()));
        LOG.info("Saving ad-hoc subgroup [{}]", subGroup.getName());
        try
        {
            return groupService.saveAdHocSubGroup(subGroup, parentId);
        }
        catch (AcmObjectAlreadyExistsException e)
        {
            AcmAppErrorJsonMsg errorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!", "GROUP", "name", e);
            errorJsonMsg.putExtra("subGroup", subGroup);
            throw errorJsonMsg;
        }
    }

    @RequestMapping(value = "/group/{groupId:.+}/remove", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup deleteGroup(@PathVariable String groupId) throws AcmAppErrorJsonMsg
    {
        LOG.info("Mark group [{}] as deleted", groupId);
        try
        {
            // we need to decode base64 encoded group id because can contain characters which can interfere with url
            groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));

            return getGroupService().markGroupDeleted(groupId);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Failed to delete group. Cause: " + e.getCauseMessage(), "GROUP", null, e);
        }
    }

    @RequestMapping(value = "/group/{groupId:.+}/parent/{parentId:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeGroupMembership(@PathVariable String groupId, @PathVariable String parentId) throws AcmAppErrorJsonMsg
    {
        try
        {
            // we need to decode base64 encoded group id because can contain characters which can interfere with url
            groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
            parentId = new String(Base64.getUrlDecoder().decode(parentId.getBytes()));

            return getGroupService().removeGroupMembership(groupId, parentId);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Failed to remove group. Cause: " + e.getCauseMessage(), "GROUP", null, e);
        }
    }

    @RequestMapping(value = "/groups/{parentId:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmGroup> removeGroupsMembership(@PathVariable String parentId, @RequestBody List<String> subGroups)
            throws AcmAppErrorJsonMsg
    {
        try
        {
            // we need to decode base64 encoded group id because can contain characters which can interfere with url
            parentId = new String(Base64.getUrlDecoder().decode(parentId.getBytes()));

            return getGroupService().removeGroupsMembership(parentId, subGroups);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Failed to remove group. Cause: " + e.getCauseMessage(), "GROUP", null, e);
        }
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
