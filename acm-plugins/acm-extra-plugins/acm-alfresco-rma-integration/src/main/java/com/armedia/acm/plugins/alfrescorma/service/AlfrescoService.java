package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.camelcontext.basic.auth.HttpInvokerUtil;
import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public abstract class AlfrescoService<T>
{
    private static final String KERBEROS_USERNAME_PREFIX = "KERBEROS/";
    private static final String APP_CONFIGURATION_ENTRY_NAME = "AlfrescoLogin";
    private final Logger LOG = LogManager.getLogger(getClass());
    private RestTemplate restTemplate;
    private String basicAuthenticationHeaderValue;
    private short maxAttempts = 10;
    private short backoffMillis = 500;
    private AlfrescoConfig alfrescoConfig;

    public String baseUrl()
    {
        return String.format("%s://%s:%d/%s", alfrescoConfig.getProtocol(), alfrescoConfig.getHost(),
                alfrescoConfig.getPort(), alfrescoConfig.getContextRoot());
    }

    public T service(Map<String, Object> context) throws AlfrescoServiceException
    {
        short attempt = 0;
        AlfrescoServiceException lastException = null;
        boolean throwedException = false;
        while (attempt < maxAttempts)
        {
            try
            {
                T retval = doService(context);
                return retval;
            }
            catch (AlfrescoServiceException e)
            {
                LOG.warn("Exception in service attempt # {}: {} {}", attempt, e.getMessage(), e);
                attempt++;
                lastException = e;
                throwedException = true;
                try
                {
                    Thread.sleep(backoffMillis);
                }
                catch (InterruptedException e1)
                {
                    LOG.warn("Could not wait for the backoff period: {} {}", e1.getMessage(), e1);
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (throwedException)
        {
            LOG.error("Max attempts of {} have failed, throwing exception {} {}", maxAttempts, lastException.getMessage(), lastException);
            throw lastException;
        }
        else
        {
            throw new AlfrescoServiceException();
        }

    }

    protected HttpEntity<String> buildRestEntity(JSONObject payload)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // external authentication header is added always, even if Alfresco is not setup to use it
        headers.set(HttpInvokerUtil.EXTERNAL_AUTH_KEY, HttpInvokerUtil.getExternalUserIdValue());

        // add basic authentication header for the call
        if (findAlfrescoAuthenticationType().equals(AlfrescoAuthenticationType.BASIC))
        {
            headers.set(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeaderValue());
        }

        return new HttpEntity<>(payload.toString(), headers);
    }

    protected HttpEntity<MultiValueMap<String, Object>> buildRestEntity(MultiValueMap<String, Object> multipartData)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // external authentication header is added always, even if Alfresco is not setup to use it
        headers.set(HttpInvokerUtil.EXTERNAL_AUTH_KEY, HttpInvokerUtil.getExternalUserIdValue());

        // add basic authentication header for the call
        if (findAlfrescoAuthenticationType().equals(AlfrescoAuthenticationType.BASIC))
        {
            headers.set(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeaderValue());
        }

        return new HttpEntity<>(multipartData, headers);
    }

    private AlfrescoAuthenticationType findAlfrescoAuthenticationType()
    {
        if (alfrescoConfig.getUser().startsWith(KERBEROS_USERNAME_PREFIX))
        {
            return AlfrescoAuthenticationType.KERBEROS;
        }

        return AlfrescoAuthenticationType.BASIC;
    }

    // not synchronizing this method because the value would always be set the same
    private String getBasicAuthenticationHeaderValue()
    {
        if (basicAuthenticationHeaderValue == null)
        {
            String auth = alfrescoConfig.getUser() + ":" + alfrescoConfig.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
            basicAuthenticationHeaderValue = "Basic " + new String(encodedAuth);
        }

        return basicAuthenticationHeaderValue;
    }

    public abstract T doService(Map<String, Object> context) throws AlfrescoServiceException;

    synchronized protected RestTemplate getRestTemplate()
    {
        if (restTemplate == null)
        {
            createAndSetRestTemplate();
        }

        return restTemplate;
    }

    private void createAndSetRestTemplate()
    {

        switch (findAlfrescoAuthenticationType())
        {
        case KERBEROS:
            AppConfigurationEntry appConfigurationEntry = Configuration.getConfiguration()
                    .getAppConfigurationEntry(APP_CONFIGURATION_ENTRY_NAME)[0];
            restTemplate = new KerberosRestTemplate((String) appConfigurationEntry.getOptions().get("keytab"),
                    (String) appConfigurationEntry.getOptions().get("principal"));
            break;
        case BASIC:
            // basic authentication header will be added in the headers before each call
            restTemplate = new RestTemplate();
            break;
        default:
            // should not happen
            throw new RuntimeException("Alfresco authentication type unknown!");
        }
    }

    public AlfrescoConfig getAlfrescoConfig()
    {
        return alfrescoConfig;
    }

    public void setAlfrescoConfig(AlfrescoConfig alfrescoConfig)
    {
        this.alfrescoConfig = alfrescoConfig;
    }

}
