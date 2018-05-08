package com.armedia.acm.plugins.dashboard.site.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.dashboard.site.dao.SiteDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.armedia.acm.plugins.dashboard.site.model.SiteConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets/site", "/api/latest/plugin/dashboard/widgets/site" })
public class ListSiteAPIController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ListSiteAPIController.class);
    private SiteDao siteDao;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Site> findAllSites(@RequestParam(value = "user", required = false) String user)
            throws AcmListObjectsFailedException
    {
        try
        {
            return getSiteDao().listAllSites(user);
        }
        catch (Exception e)
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