package com.armedia.acm.plugins.dashboard.site.web.api;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.dashboard.site.dao.SiteDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.armedia.acm.plugins.dashboard.site.model.SiteConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private static final Logger LOGGER = LogManager.getLogger(ListSiteAPIController.class);
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
