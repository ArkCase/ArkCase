package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.service.group.GroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({ "/api/v1/groups", "/api/latest/groups" })
public class GroupAPIController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private GroupService groupService;
    private MuleContextManager muleContextManager;
    private ExecuteSolrQuery executeSolrQuery;

    /*
     * @RequestMapping(value = "/{userId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     * @ResponseBody
     * public String getGroupsByType(@RequestParam(value = "authorized") Boolean authorized,
     * @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
     * @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
     * @RequestParam(value = "s", defaultValue = "") String sort,
     * @PathVariable(value = "userId") String userId,
     * @RequestParam(value = "fq") String searchFilter,
     * Authentication auth) throws MuleException
     * {
     * LOG.info("Taking all groups and subgroups from Solr.");
     * String solrQuery = getGroupService().buildGroupsForUserSolrQuery(authorized, userId, searchFilter);
     * LOG.debug("User [{}] is searching for [{}]", auth.getName(), solrQuery);
     * return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow,
     * maxRows, sort);
     * }
     */

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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
