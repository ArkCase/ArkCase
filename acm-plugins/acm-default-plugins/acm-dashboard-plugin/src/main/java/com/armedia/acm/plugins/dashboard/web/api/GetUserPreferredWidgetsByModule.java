package com.armedia.acm.plugins.dashboard.web.api;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.userPreference.PreferredWidgetsDto;
import com.armedia.acm.plugins.dashboard.service.UserPreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class GetUserPreferredWidgetsByModule
{
    private UserPreferenceService userPreferenceService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/preferred/{moduleName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public PreferredWidgetsDto getPreferredWidgets(@PathVariable("moduleName") String moduleName, Authentication authentication,
            HttpSession session) throws AcmWidgetException, AcmObjectNotFoundException
    {

        String userId = authentication.getName();
        log.info("Finding widgets for user  based on the user preference for user: [{}]", userId);

        PreferredWidgetsDto result;
        try
        {
            result = userPreferenceService.getPreferredWidgetsByUserAndModule(userId, moduleName);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw e;
        }

        return result;
    }

    public UserPreferenceService getUserPreferenceService()
    {
        return userPreferenceService;
    }

    public void setUserPreferenceService(UserPreferenceService userPreferenceService)
    {
        this.userPreferenceService = userPreferenceService;
    }
}
