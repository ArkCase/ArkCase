package com.armedia.acm.pluginmanager.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * Defines the privilege required to invoke a plugin URL.  URLs in the /plugin URL namespace are protected by a
 * Spring MVC interceptor.  The interceptor checks AcmPluginUrlPrivilege instances to verify the user has a privilege
 * that allows them to execute the URL.
 */
public class AcmPluginUrlPrivilege
{
    private static final String XML_EXTENSION = ".xml";
    private static final String JSON_EXTENSION = ".json";
    private static final int XML_EXTENSION_LENGTH = XML_EXTENSION.length();
    private static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();
    private String url;
    private HttpMethod httpMethod;
    private AcmPluginPrivilege requiredPrivilege;
    private String[] urlPathVariables;

    private final Logger log = LoggerFactory.getLogger(getClass());

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
            String pathPart = incomingUrlPathVariables[a];
            if ( pathPart.endsWith(XML_EXTENSION) )
            {
                pathPart = trimPathPart(pathPart, XML_EXTENSION_LENGTH);
            }
            if ( pathPart.endsWith(JSON_EXTENSION) )
            {
                pathPart = trimPathPart(pathPart, JSON_EXTENSION_LENGTH);
            }
            boolean exactMatch = pathPart.equalsIgnoreCase(urlPathVariables[a]);
            boolean placeholder = urlPathVariables[a].startsWith("{") && urlPathVariables[a].endsWith("}");

            if ( !exactMatch && !placeholder )
            {
                return false;
            }
        }

        return true;
    }

    private String trimPathPart(String pathPart, int extensionLength)
    {
        pathPart = pathPart.substring(0, pathPart.length() - extensionLength);
        return pathPart;
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
