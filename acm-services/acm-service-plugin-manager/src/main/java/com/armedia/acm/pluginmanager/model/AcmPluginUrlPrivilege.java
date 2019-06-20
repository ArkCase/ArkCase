package com.armedia.acm.pluginmanager.model;

/*-
 * #%L
 * ACM Service: Plugin Manager
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpMethod;

/**
 * Defines the privilege required to invoke a plugin URL. URLs in the /plugin URL namespace are protected by a Spring
 * MVC interceptor. The interceptor checks AcmPluginUrlPrivilege instances to verify the user has a privilege that
 * allows them to execute the URL.
 */
public class AcmPluginUrlPrivilege
{
    private static final String HTML_EXTENSION = ".html";
    private static final String XML_EXTENSION = ".xml";
    private static final String JSON_EXTENSION = ".json";
    private static final int XML_EXTENSION_LENGTH = XML_EXTENSION.length();
    private static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();
    private final Logger log = LogManager.getLogger(getClass());
    private String url;
    private HttpMethod httpMethod;
    private AcmPluginPrivilege requiredPrivilege;
    private String[] urlPathVariables;

    public boolean matches(String incomingUrl, String method)
    {
        if (!getHttpMethod().name().equals(method))
        {
            return false;
        }

        if (getUrl().equalsIgnoreCase(incomingUrl))
        {
            return true;
        }

        if (incomingUrl.endsWith(HTML_EXTENSION) && incomingUrl.startsWith(getUrl()))
        {
            return true;
        }

        String[] incomingUrlPathVariables = incomingUrl.split("/");

        if (incomingUrlPathVariables.length != urlPathVariables.length)
        {
            return false;
        }

        for (int a = 0; a < incomingUrlPathVariables.length; a++)
        {
            String pathPart = incomingUrlPathVariables[a];
            if (pathPart.endsWith(XML_EXTENSION))
            {
                pathPart = trimPathPart(pathPart, XML_EXTENSION_LENGTH);
            }
            if (pathPart.endsWith(JSON_EXTENSION))
            {
                pathPart = trimPathPart(pathPart, JSON_EXTENSION_LENGTH);
            }
            boolean exactMatch = pathPart.equalsIgnoreCase(urlPathVariables[a]);
            boolean placeholder = urlPathVariables[a].startsWith("{") && urlPathVariables[a].endsWith("}");

            if (!exactMatch && !placeholder)
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
