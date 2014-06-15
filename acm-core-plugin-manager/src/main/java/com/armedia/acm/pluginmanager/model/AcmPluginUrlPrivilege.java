package com.armedia.acm.pluginmanager.model;

import org.springframework.http.HttpMethod;

/**
 * Defines the privilege required to invoke a plugin URL.  URLs in the /plugin URL namespace are protected by a
 * Spring MVC interceptor.  The interceptor checks AcmPluginUrlPrivilege instances to verify the user has a privilege
 * that allows them to execute the URL.
 */
public class AcmPluginUrlPrivilege
{
    private String url;
    private HttpMethod httpMethod;
    private AcmPluginPrivilege requiredPrivilege;
    private String[] urlPathVariables;

    public boolean matches(String incomingUrl, String method)
    {
        if ( !getHttpMethod().name().equals(method))
        {
            return false;
        }

        if ( getUrl().equalsIgnoreCase(incomingUrl) )
        {
            return true;
        }

        String[] incomingUrlPathVariables = incomingUrl.split("/");

        if ( incomingUrlPathVariables.length != urlPathVariables.length )
        {
            return false;
        }

        for ( int a = 0; a < incomingUrlPathVariables.length; a++ )
        {
            boolean exactMatch = incomingUrlPathVariables[a].equalsIgnoreCase(urlPathVariables[a]);
            boolean placeholder = urlPathVariables[a].startsWith("{") && urlPathVariables[a].endsWith("}");

            if ( !exactMatch && !placeholder )
            {
                return false;
            }
        }

        return true;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
        this.urlPathVariables = url.split("/");
    }

    public HttpMethod getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public AcmPluginPrivilege getRequiredPrivilege()
    {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(AcmPluginPrivilege requiredPrivilege)
    {
        this.requiredPrivilege = requiredPrivilege;
    }
}
