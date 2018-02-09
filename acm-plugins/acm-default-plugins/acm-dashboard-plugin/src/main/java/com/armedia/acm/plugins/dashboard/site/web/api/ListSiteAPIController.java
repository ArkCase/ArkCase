package com.armedia.acm.plugins.dashboard.site.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.dashboard.site.dao.SiteDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.armedia.acm.plugins.dashboard.site.model.SiteConstants;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets/site", "/api/latest/plugin/dashboard/widgets/site"})
public class ListSiteAPIController
{
    private SiteDao siteDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(ListSiteAPIController.class);

    @RequestMapping(value = "byUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Site findByUser(@RequestParam("user") String user) throws AcmListObjectsFailedException
    {
        Preconditions.checkNotNull(user, "User cannot be null");
        try
        {
            LOGGER.info("Searching for site owned by [{}]", user);
            return getSiteDao().findByUser(user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to find site for user [{}]", user, e);
            throw new AcmListObjectsFailedException(SiteConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "byId/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Site findById(@PathVariable("id") Long id) throws AcmUserActionFailedException
    {
        Preconditions.checkNotNull(id, "Id cannot be null");
        try
        {
            LOGGER.info("Searching for site [{}]", id);
            return getSiteDao().findById(id);
        } catch (Exception e)
        {
            LOGGER.error("Failed to find site [{}]", id, e);
            throw new AcmUserActionFailedException("Get Site", "Site", id, e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Site> findAllSites(@RequestParam(value = "user", required = false) String user)
            throws AcmListObjectsFailedException
    {
        try
        {
            return getSiteDao().listAllSites(user);
        } catch (Exception e)
        {
            throw new AcmListObjectsFailedException(SiteConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QueryResultPageWithTotalCount<Site> findPageSitesForUser(
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false) String s)
            throws AcmListObjectsFailedException
    {
        try
        {
            List<Site> siteList = getSiteDao().listSites(start, n, s);
            int numSites = getSiteDao().countAll();
            QueryResultPageWithTotalCount<Site> sitesPage = new QueryResultPageWithTotalCount<>();
            sitesPage.setResultPage(siteList);
            sitesPage.setTotalCount(numSites);
            sitesPage.setStartRow(start);
            sitesPage.setMaxRows(n);
            return sitesPage;
        } catch (Exception e)
        {
            throw new AcmListObjectsFailedException(SiteConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    public SiteDao getSiteDao()
    {
        return siteDao;
    }

    public void setSiteDao(SiteDao siteDao)
    {
        this.siteDao = siteDao;
    }
}