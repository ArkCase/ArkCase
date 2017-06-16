/**
 *
 */
package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
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

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({"/api/v1/users", "/api/latest/users"})
public class GetGroupAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private AcmGroupDao groupDao;
    private MuleContextManager muleContextManager;

    @RequestMapping(value = "/groups/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAllGroupsAndSubgroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                                           @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
                                           @RequestParam(value = "s", required = false, defaultValue = "") String sort,
                                           Authentication auth,
                                           HttpSession httpSession) throws MuleException, Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking all groups and subgroups from Solr.");
        }

        String query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (LOG.isDebugEnabled())
        {
            LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    @RequestMapping(value = "/directory/groups/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroupsByDirectory(@RequestParam(value = "directory") String directory,@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                                           @RequestParam(value = "n", required = false, defaultValue = "200") int maxRows,
                                           @RequestParam(value = "s", required = false, defaultValue = "") String sort,
                                           Authentication auth,
                                           HttpSession httpSession) throws MuleException, Exception
    {

        LOG.info("Taking groups by directory from Solr.");

        StringBuilder solrQuery = new StringBuilder();
        solrQuery.append("object_type_s:GROUP");

        if(directory.length() > 0){
            solrQuery.append(" AND directory_name_s:").append(directory).append(" AND status_lcs:ACTIVE");
        }


        LOG.debug("User [{}] is searching for {}", auth.getName(), solrQuery.toString());


        Map<String, Object> headers = new HashMap<>();
        headers.put("query", solrQuery.toString());
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: {}", response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    @RequestMapping(value = "/group/{groupId}/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroup(@PathVariable("groupId") String groupId,
                           @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                           @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
                           @RequestParam(value = "s", required = false, defaultValue = "") String sort,
                           Authentication auth,
                           HttpSession httpSession) throws MuleException, Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking group from Solr with ID = " + groupId);
        }

        String query = "object_id_s:" + groupId + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (LOG.isDebugEnabled())
        {
            LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    @RequestMapping(value = "/group/{groupId}/get/subgroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSubGroups(@PathVariable("groupId") String groupId,
                               @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                               @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
                               @RequestParam(value = "s", required = false, defaultValue = "") String sort,
                               Authentication auth,
                               HttpSession httpSession) throws MuleException, Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking subgroups from Solr with ID = " + groupId);
        }

        String groupString = getGroupsByParent(groupId, startRow, maxRows, sort, auth);

        if (groupString != null)
        {
            return groupString;
        }

        throw new IllegalStateException("Cannot retrieve subgroups for group with ID = " + groupId);

    }

    @RequestMapping(value = "/group/get/toplevel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTopLevelGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                                    @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
                                    @RequestParam(value = "s", required = false, defaultValue = "") String sort,
                                    @RequestParam(value = "groupSubtype", required = false, defaultValue = "") List<String> groupSubtype,
                                    Authentication auth,
                                    HttpSession httpSession) throws MuleException, Exception
    {
        LOG.info("Taking all top level groups from Solr.");

        String query = "object_type_s:GROUP AND -parent_id_s:* AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (groupSubtype != null && !groupSubtype.isEmpty())
        {
            query += " AND object_sub_type_s:(" + String.join(" OR ", groupSubtype) + ")";
        }
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
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    private String getGroupsByParent(String groupId, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
    {
        String query = "parent_id_s:\"" + groupId + "\" AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (LOG.isDebugEnabled())
        {
            LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
