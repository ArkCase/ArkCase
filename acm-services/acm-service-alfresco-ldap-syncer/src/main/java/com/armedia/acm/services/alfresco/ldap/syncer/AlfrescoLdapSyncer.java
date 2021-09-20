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

import com.armedia.acm.data.AcmServiceLdapSyncEvent;
import com.armedia.acm.data.AcmServiceLdapSyncResult;
import com.armedia.acm.services.ldap.syncer.ExternalLdapSyncer;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 3, 2018
 *
 */
public class AlfrescoLdapSyncer implements ApplicationEventPublisherAware, ExternalLdapSyncer
{

    private static final String US_ASCII_CHARSET = "US-ASCII";

    private Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    private AlfrescoLdapSyncerConfig config;

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

        String username = config.getAdminUsername();
        String password = config.getAdminPassword();
        String auth = String.format("%s:%s", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName(US_ASCII_CHARSET)));

        String authHeader = "Basic " + new String(encodedAuth);
        requestHeaders.add("Authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity<>("parameters", requestHeaders);
        ListenableFuture<ResponseEntity<SyncResult>> futureEntity = restTemplate.postForEntity(config.getAdminBaseUrl(), entity,
                SyncResult.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<SyncResult>>()
        {
            @Override
            public void onSuccess(ResponseEntity<SyncResult> result)
            {
                AcmServiceLdapSyncResult syncResult = new AcmServiceLdapSyncResult();

                if (result.getBody().isSuccess())
                {
                    log.debug("Alfresco sync with LDAP server succeeded.");
                }
                else
                {
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

                applicationEventPublisher.publishEvent(new AcmServiceLdapSyncEvent(syncResult));
            }
        });
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

    public AlfrescoLdapSyncerConfig getConfig()
    {
        return config;
    }

    public void setConfig(AlfrescoLdapSyncerConfig config)
    {
        this.config = config;
    }
}
