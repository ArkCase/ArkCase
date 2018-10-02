package com.armedia.acm.services.alfresco.ldap.syncer;

/*-
 * #%L
 * ACM Service: Alfresco LDAP Syncer
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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.data.AcmServiceLdapSyncEvent;
import com.armedia.acm.data.AcmServiceLdapSyncResult;
import com.armedia.acm.services.ldap.syncer.ExternalLdapSyncer;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 3, 2018
 *
 */
public class AlfrescoLdapSyncer implements ApplicationEventPublisherAware, ExternalLdapSyncer
{

    private static final String DEFAULT_ALFRESCO_BASEURL = "https://acm-arkcase/alfresco/s/enterprise/admin/admin-sync";

    private static final String DEFAULT_ADMIN_USERNAME = "admin";

    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

    private static final String US_ASCII_CHARSET = "US-ASCII";

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource alfrescoPropertiesResource;

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private String baseUrl;

    private byte[] encodedAuth;

    private ApplicationEventPublisher applicationEventPublisher;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.alfresco.ldap.syncer.ExternalLdapSyncer#initiateSync()
     */
    @Override
    public void initiateSync()
    {

        AsyncRestTemplate restTemplate = new AsyncRestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.TEXT_PLAIN);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        loadFromProperties();

        String authHeader = "Basic " + new String(encodedAuth);
        encodedAuth = null;
        requestHeaders.add("Authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity<>("parameters", requestHeaders);
        ListenableFuture<ResponseEntity<SyncResult>> futureEntity = restTemplate.postForEntity(baseUrl, entity, SyncResult.class);
        baseUrl = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<SyncResult>>()
        {
            @Override
            public void onSuccess(ResponseEntity<SyncResult> result)
            {
                AcmServiceLdapSyncResult syncResult = new AcmServiceLdapSyncResult();

                if (result.getBody().isSuccess())
                {
                    syncResult.setMessage("Alfresco sync with LDAP server succeeded.");
                    log.debug("Alfresco sync with LDAP server succeeded.");
                }
                else
                {
                    syncResult.setMessage("Alfresco sync with LDAP server failed.");
                    log.warn("Alfresco sync with LDAP server failed.");
                }

                syncResult.setService("Alfresco");
                String userName = authentication.getName();
                syncResult.setUser(userName);
                syncResult.setResult(result.getBody().isSuccess());

                applicationEventPublisher.publishEvent(new AcmServiceLdapSyncEvent(syncResult));
            }

            @Override
            public void onFailure(Throwable ex)
            {
                log.error("Could not initiate Alfresco sync with LDAP server due to: [{}].", ex.getMessage());

                AcmServiceLdapSyncResult syncResult = new AcmServiceLdapSyncResult();
                syncResult.setService("Alfresco");
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userName = authentication.getName();
                syncResult.setUser(userName);
                syncResult.setResult(false);
                syncResult.setMessage("Sync with Alfresco failed due to: [" + ex.getMessage() + "]");

                applicationEventPublisher.publishEvent(new AcmServiceLdapSyncEvent(syncResult));
            }
        });
    }

    private void loadFromProperties()
    {
        Properties alfrescoProperties = new Properties();
        try (InputStream objectTypesInputStream = alfrescoPropertiesResource.getInputStream())
        {

            alfrescoProperties.load(objectTypesInputStream);
            baseUrl = alfrescoProperties.getProperty("alfresco.admin.baseurl", DEFAULT_ALFRESCO_BASEURL);
            String username = alfrescoProperties.getProperty("alfresco.admin.username", DEFAULT_ADMIN_USERNAME);
            String password = getPassword(alfrescoProperties);
            String auth = String.format("%s:%s", username, password);
            encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName(US_ASCII_CHARSET)));

        }
        catch (IOException ioe)
        {

            log.error("Could not read properties from [{}] file.", alfrescoPropertiesResource.getFilename(), ioe);

            baseUrl = DEFAULT_ALFRESCO_BASEURL;
            String auth = String.format("%s:%s", DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
            encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName(US_ASCII_CHARSET)));

        }
    }

    private String getPassword(Properties alfrescoProperties)
    {
        String password = alfrescoProperties.getProperty("alfresco.admin.password");
        if (password != null)
        {
            try
            {
                return encryptablePropertyUtils.decryptPropertyValue(password);
            }
            catch (AcmEncryptionException e)
            {
                log.error("Could not decrypt \"alfresco.admin.password\" from  " + alfrescoPropertiesResource.getFilename() + ".");
                return DEFAULT_ADMIN_PASSWORD;
            }
        }
        else
        {
            return DEFAULT_ADMIN_PASSWORD;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * @param alfrescoPropertiesResource
     *            the alfrescoPropertiesResource to set
     */
    public void setAlfrescoPropertiesResource(Resource alfrescoPropertiesResource)
    {
        this.alfrescoPropertiesResource = alfrescoPropertiesResource;
    }

    /**
     * @param encryptablePropertyUtils
     *            the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

}
