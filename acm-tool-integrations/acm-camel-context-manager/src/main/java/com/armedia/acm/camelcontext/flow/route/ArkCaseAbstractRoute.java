package com.armedia.acm.camelcontext.flow.route;

/*-
 * #%L
 * acm-camel-context-manager
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;

import org.apache.camel.builder.RouteBuilder;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Nov, 2019
 * 
 * Currently not in use.
 * This class will be parent class on all routes. All mutual methods will be placed here.
 * All classes that are instance of ArkCaseAbstract.class will be added as routes on camelContext.
 */
public abstract class ArkCaseAbstractRoute extends RouteBuilder
{
    /*
     * All properties from ArkCase services, are sent trough camel exchange object to the route.
     * First step in every route is to populate the map with those properties.
     */
    public Map<String, Object> routeProperties = new HashMap<>();
    private String repositoryId;
    private Long timeout;

    public String createUrl()
    {
        String api = ArkCaseCMISConstants.ARKCASE_CMIS_COMPONENT + routeProperties.get(ArkCaseCMISConstants.CMIS_API_URL).toString();
        UrlBuilder urlBuilder = new UrlBuilder(api);
        urlBuilder.addParameter("username", routeProperties.get(SessionParameter.USER).toString());
        urlBuilder.addParameter("password", routeProperties.get(SessionParameter.PASSWORD).toString());

        return urlBuilder.toString();
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public void setTimeout(String timeout)
    {
        this.timeout = Long.valueOf(timeout);
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public Long getTimeout()
    {
        return timeout;
    }

    public Map<String, Object> getRouteProperties()
    {
        return routeProperties;
    }

    public void setRouteProperties(Map<String, Object> routeProperties)
    {
        this.routeProperties = routeProperties;
    }
}
