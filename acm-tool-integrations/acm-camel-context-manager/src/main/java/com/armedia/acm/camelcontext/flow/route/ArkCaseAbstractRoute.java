package com.armedia.acm.camelcontext.flow.route;

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
