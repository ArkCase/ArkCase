package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class AcmGroupAPIController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private GroupService groupService;
    private MuleContextManager muleContextManager;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/groups/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws MuleException
    {
        LOG.info("Taking all groups and subgroups from Solr.");

        String query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), query);

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type is [{}]", response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    @RequestMapping(value = "/{userid:.+}/groups", params = {
            "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForUser(@PathVariable("userid") String userId, @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws MuleException
    {
        LOG.info("Taking groups and subgroups from Solr for specific user.");

        String solrQuery = groupService.buildGroupsForUserSolrQuery(authorized, userId);

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @RequestMapping(value = "/{userId:.+}/groups", params = {
            "fq", "authorized" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForUserByName(@RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", defaultValue = "name_lcs") String sortBy,
            @PathVariable(value = "userId") String userId,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "fq") String searchFilter,
            Authentication auth) throws MuleException
    {
        LOG.info("Taking groups and subgroups from Solr for specific user by name.");

        String solrQuery = getGroupService().buildGroupsForUserByNameSolrQuery(authorized, userId, searchFilter);

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @RequestMapping(value = "/groups", params = { "nameFq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsByName(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "nameFq") String nameFilter,
            Authentication auth) throws MuleException
    {
        return groupService.getGroupsByNameFilter(auth, nameFilter, startRow, maxRows, sortBy, sortDirection);
    }

    @RequestMapping(value = "/groups/adhoc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAdhocGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws MuleException
    {
        LOG.info("Taking ad-hoc groups from Solr.");

        String solrQuery = "object_type_s:GROUP AND object_sub_type_s:ADHOC_GROUP AND status_lcs:ACTIVE";

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows, sort);

    }

    @RequestMapping(value = "/{directory:.+}/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroupsByDirectory(@PathVariable String directory,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws MuleException
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

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: [{}]", response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {

            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    @RequestMapping(value = "/group/{groupId:.+}/get/subgroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSubGroups(@PathVariable("groupId") String groupId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws MuleException
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

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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
