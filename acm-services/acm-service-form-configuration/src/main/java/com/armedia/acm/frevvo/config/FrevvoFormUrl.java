/**
 * 
 */
package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormUrl implements FormUrl
{

    private Logger LOG = LogManager.getLogger(FrevvoFormUrl.class);

    private Map<String, Object> properties;
    private Properties plainFormProperties;
    private AuthenticationTokenService authenticationTokenService;

    public String getBaseUrl()
    {
        String url = "";

        String protocol = getProtocol();
        String host = getHost();
        String port = getPort();

        if (protocol != null && !protocol.isEmpty())
        {
            url = url + protocol + "://";
        }
        else
        {
            LOG.error("Frevvo Protocol is not defined.");
        }

        if (host != null && !host.isEmpty())
        {
            url = url + host;
        }
        else
        {
            LOG.error("Frevvo Host is not defined.");
        }

        if (port != null && !port.isEmpty())
        {
            url = url + ":" + port;
        }
        else
        {
            LOG.warn("Frevvo port number is not defined. Maybe is not needed.");
        }

        LOG.info("Form Base Url: " + url);

        return url;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    /**
     * @return the authenticationTokenService
     */
    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    /**
     * @param authenticationTokenService
     *            the authenticationTokenService to set
     */
    public void setAuthenticationTokenService(
            AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    public String getNewFormUrl(String formName, boolean plain)
    {
        String uri = (String) properties.get(FrevvoFormConstants.URI);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (uri == null)
        {
            uri = "";
            LOG.error("Frevvo URI is not defined.");
        }

        String tenant = getTenant();
        String user = getDesignerUser();
        String applicationId = plain == false ? (String) properties.get(formName + ".application.id")
                : (String) getPlainFormProperties().get(formName + ".application.id");
        String type = plain == false ? (String) properties.get(formName + ".type")
                : (String) getPlainFormProperties().get(formName + ".type");
        String mode = plain == false ? (String) properties.get(formName + ".mode")
                : (String) getPlainFormProperties().get(formName + ".mode");
        String token = this.authenticationTokenService.getTokenForAuthentication(authentication);
        String service = (String) properties.get(FrevvoFormConstants.SERVICE);
        String serviceExternal = (String) properties.get(FrevvoFormConstants.SERVICE_EXTERNAL);
        String redirect = (String) properties.get(FrevvoFormConstants.REDIRECT);
        String timezone = (String) properties.get(FrevvoFormConstants.TIMEZONE);
        String locale = getLocale();

        if (tenant != null)
        {
            uri = uri.replace("{tenant}", tenant);
        }

        if (user != null)
        {
            uri = uri.replace("{user}", user);
        }

        if (applicationId != null)
        {
            uri = uri.replace("{application}", applicationId);
        }

        if (type != null)
        {
            uri = uri.replace("{type}", type);
        }

        if (mode != null)
        {
            uri = uri.replace("{mode}", mode);
        }

        if (token != null)
        {
            uri = uri.replace("{acm_ticket}", token);
        }

        if (service != null)
        {
            uri = uri.replace("{frevvo_service_baseUrl}", service);
        }

        if (serviceExternal != null)
        {
            uri = uri.replace("{frevvo_service_external_baseUrl}", serviceExternal);
        }

        if (redirect != null)
        {
            uri = uri.replace("{frevvo_browser_redirect_baseUrl}", redirect);
        }

        if (timezone != null)
        {
            uri = uri.replace("{frevvo_timezone}", timezone);
        }

        if (locale != null)
        {
            uri = uri.replace("{frevvo_locale}", locale);
        }

        String url = getBaseUrl() + uri;

        LOG.info("Form Url: " + url);

        return url;
    }

    @Override
    public String getPdfRenditionUrl(String formName, String docId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String enableFrevvoFormEngine(String formName)
    {
        String enableFrevvoFormEngine = (String) properties.get(formName + ".enable.frevvo.form.engine");
        return enableFrevvoFormEngine;
    }

    private String getLocale()
    {
        String retval = "";

        String defaultLocale = (String) properties.get(FrevvoFormConstants.LOCALE);
        String overwriteLocale = (String) properties.get(FrevvoFormConstants.LOCALE + ".overwrite");

        if (overwriteLocale != null && "true".equalsIgnoreCase(overwriteLocale))
        {
            HttpServletRequest request = getCurrentRequest();

            if (request != null)
            {
                Locale locale = request.getLocale();
                if (locale != null && !"".equals((locale.toString())))
                {
                    String language = locale.getLanguage() == null ? "" : locale.getLanguage();
                    String country = locale.getCountry() == null ? "" : locale.getCountry();

                    retval = language + "_" + country;
                }
            }
        }

        if ("".equals(retval) && defaultLocale != null)
        {
            retval = defaultLocale;
        }

        return retval;
    }

    public HttpServletRequest getCurrentRequest()
    {
        HttpServletRequest request = null;

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

        if (attrs instanceof ServletRequestAttributes)
        {
            request = ((ServletRequestAttributes) attrs).getRequest();
        }

        return request;
    }

    public String getTenant()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.TENANT))
        {
            return (String) getProperties().get(FrevvoFormConstants.TENANT);
        }

        return null;
    }

    public String getAdminUser()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.ADMIN_USER))
        {
            return (String) getProperties().get(FrevvoFormConstants.ADMIN_USER);
        }

        return null;
    }

    public String getAdminPassword()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.ADMIN_PASSWORD))
        {
            return (String) getProperties().get(FrevvoFormConstants.ADMIN_PASSWORD);
        }

        return null;
    }

    public String getDesignerUser()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.DESIGNER_USER))
        {
            return (String) getProperties().get(FrevvoFormConstants.DESIGNER_USER);
        }

        return null;
    }

    public List<String> getPlainFormApplicationIds()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.PLAIN_FORM_APPLICATION_IDS))
        {
            String applicationIdsAsString = (String) getProperties().get(FrevvoFormConstants.PLAIN_FORM_APPLICATION_IDS);
            if (applicationIdsAsString != null && !applicationIdsAsString.isEmpty())
            {
                String[] applicationIdsAsArray = applicationIdsAsString.split(",", -1);
                List<String> applicationIds = Arrays.asList(applicationIdsAsArray);

                return applicationIds.stream().map(element -> element.trim()).collect(Collectors.toList());
            }
        }

        return null;
    }

    @Override
    public String getProtocol()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.PROTOCOL))
        {
            return (String) getProperties().get(FrevvoFormConstants.PROTOCOL);
        }

        return null;
    }

    @Override
    public String getHost()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.HOST))
        {
            return (String) getProperties().get(FrevvoFormConstants.HOST);
        }

        return null;
    }

    @Override
    public String getPort()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.PORT))
        {
            return (String) getProperties().get(FrevvoFormConstants.PORT);
        }

        return null;
    }

    @Override
    public Integer getPortAsInteger()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.PORT))
        {
            String port = (String) getProperties().get(FrevvoFormConstants.PORT);

            try
            {
                return Integer.parseInt(port);
            }
            catch (Exception e)
            {
                LOG.warn("Cannot parse port=" + port + ". If empty, normal behaviour.");
            }
        }

        return null;
    }

    @Override
    public String getInternalProtocol()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.INTERNAL_PROTOCOL))
        {
            return (String) getProperties().get(FrevvoFormConstants.INTERNAL_PROTOCOL);
        }

        return null;
    }

    @Override
    public String getInternalHost()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.INTERNAL_HOST))
        {
            return (String) getProperties().get(FrevvoFormConstants.INTERNAL_HOST);
        }

        return null;
    }

    @Override
    public String getInternalPort()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.INTERNAL_PORT))
        {
            return (String) getProperties().get(FrevvoFormConstants.INTERNAL_PORT);
        }

        return null;
    }

    @Override
    public Integer getInternalPortAsInteger()
    {
        if (getProperties() != null && getProperties().containsKey(FrevvoFormConstants.INTERNAL_PORT))
        {
            String port = (String) getProperties().get(FrevvoFormConstants.INTERNAL_PORT);

            try
            {
                return Integer.parseInt(port);
            }
            catch (Exception e)
            {
                LOG.warn("Cannot parse port=" + port + ". If empty, normal behaviour.");
            }
        }

        return null;
    }

    public Properties getPlainFormProperties()
    {
        return plainFormProperties;
    }

    public void setPlainFormProperties(Properties plainFormProperties)
    {
        this.plainFormProperties = plainFormProperties;
    }

}
