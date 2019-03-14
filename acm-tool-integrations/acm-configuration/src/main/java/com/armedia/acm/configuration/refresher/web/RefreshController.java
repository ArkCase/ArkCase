package com.armedia.acm.configuration.refresher.web;
/*-
 * #%L
 * configuration-refresher
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

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.api.RefreshScopeFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Named;

/**
 * API endpoints to refresh configuration values externally for the application.
 */
@RestController
public class RefreshController
{
    @Autowired(required = false)
    @Named("proxyRefreshScopeFacade")
    private RefreshScopeFacade refreshScopeFacade;

    @Autowired(required = false)
    @Named("proxyConfigurationFacade")
    private ConfigurationFacade configurationFacade;

    @RequestMapping("/refresh")
    public void refresh()
    {
        if (refreshScopeFacade != null)
        {
            refreshScopeFacade.refresh();
        }
        if (configurationFacade != null)
        {
            configurationFacade.refresh();
        }
    }
}
