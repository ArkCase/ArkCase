package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.plugins.ecm.service.sync.AlfrescoSyncConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class AlfrescoAuditApplicationRestClient
{
    private static final String KERBEROS_USERNAME_PREFIX = "KERBEROS/";
    private static final String APP_CONFIGURATION_ENTRY_NAME = "AlfrescoLogin";
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private AlfrescoSyncConfig alfrescoSyncConfig;

    private RestTemplate restTemplate;
    private String basicAuthenticationHeaderValue;

    public String baseUrl()
    {
        return String.format("%s://%s:%d/%s", alfrescoSyncConfig.getProtocol(), alfrescoSyncConfig.getHost(),
                alfrescoSyncConfig.getPort(), alfrescoSyncConfig.getContextRoot());
    }

    public JSONObject service(String applicationName, long startingAuditId) throws Exception
    {
        String url = baseUrl() + "/service/api/audit/query/" + applicationName;
        url = url + "?verbose=true&forward=true&limit=50&fromId=" + startingAuditId;

        HttpEntity<String> getEntity = buildRestEntity();

        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, getEntity, String.class);

        LOG.debug("query audit response: {}", response.getBody());

        if (HttpStatus.OK.equals(response.getStatusCode()))
        {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse;
        }
        else
        {
            throw new Exception("Could not complete record: " + response.getStatusCode());
        }
    }

    protected HttpEntity<String> buildRestEntity()
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

        return new HttpEntity<>(headers);
    }

    private AlfrescoAuthenticationType findAlfrescoAuthenticationType()
    {
        if (alfrescoSyncConfig.getUsername().startsWith(KERBEROS_USERNAME_PREFIX))
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
            String auth = alfrescoSyncConfig.getUsername() + ":" + alfrescoSyncConfig.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
            basicAuthenticationHeaderValue = "Basic " + new String(encodedAuth);
        }

        return basicAuthenticationHeaderValue;
    }

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

    public AlfrescoSyncConfig getAlfrescoSyncConfig()
    {
        return alfrescoSyncConfig;
    }

    public void setAlfrescoSyncConfig(AlfrescoSyncConfig alfrescoSyncConfig)
    {
        this.alfrescoSyncConfig = alfrescoSyncConfig;
    }

    private enum AlfrescoAuthenticationType
    {
        BASIC, KERBEROS
    }

}
