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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.JobDataMap;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncService implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AlfrescoAuditApplicationRestClient auditApplicationRestClient;
    private Map<String, EcmAuditResponseReader> auditApplications;
    private ApplicationEventPublisher applicationEventPublisher;

    public void queryAlfrescoAuditApplications(JobDataMap lastAuditIdsPerApplication)
    {
        if (MDC.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY) == null)
        {
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        }

        List<EcmEvent> allEvents = new ArrayList<>();

        for (Map.Entry<String, EcmAuditResponseReader> auditApplication : getAuditApplications().entrySet())
        {
            List<EcmEvent> eventsForAuditApp = collectAuditEvents(auditApplication.getKey(), auditApplication.getValue(),
                    lastAuditIdsPerApplication);
            if (eventsForAuditApp != null && !eventsForAuditApp.isEmpty())
            {
                log.info("Fetched {} audit records for audit application {}", eventsForAuditApp.size(), auditApplication.getKey());
                allEvents.addAll(eventsForAuditApp);
            }
        }

        allEvents.stream()
                .sorted(Comparator.comparing(EcmEvent::getAuditId))
                .forEach(e -> applicationEventPublisher.publishEvent(e));
    }

    protected List<EcmEvent> collectAuditEvents(String applicationName, EcmAuditResponseReader reader,
            JobDataMap lastAuditIdsPerApplication)
    {
        log.info("Starting Alfresco sync for audit application {}", applicationName);

        try
        {
            String lastAuditIdKey = applicationName + ".lastAuditId";
            long lastAuditId;
            try
            {
                lastAuditId = lastAuditIdsPerApplication.getLong(lastAuditIdKey) + 1;
            }
            catch (ClassCastException e)
            {
                log.debug("Last audit id is not found, start from the first one");
                lastAuditId = 1;
            }

            JSONObject auditEntries = getAuditApplicationRestClient().service(applicationName, lastAuditId);
            updatePropertiesWithLastAuditId(lastAuditIdKey, auditEntries, lastAuditIdsPerApplication);

            return reader.read(auditEntries);
        }
        catch (AcmEncryptionException e)
        {
            log.error("Could not decrypt property {}.lastAuditId", applicationName, e);
        }
        catch (Exception e)
        {
            log.error("Could not query Alfresco audit records for application {}", applicationName, e);
        }

        return null;
    }

    protected void updatePropertiesWithLastAuditId(String lastAuditIdKey, JSONObject fullAuditResponse,
            JobDataMap lastAuditIdsPerApplication)
    {
        // some of the readers ignore some events. So we want to store the last audit id from the full
        // response... not the last audit id returned by the reader.
        JSONArray allAudits = fullAuditResponse.getJSONArray("entries");
        int numAudits = allAudits.length();

        if (numAudits > 0)
        {
            long lastAuditFromFullResponse = allAudits.getJSONObject(numAudits - 1).getLong("id");
            String lastAuditId = String.valueOf(lastAuditFromFullResponse);
            Map<String, String> properties = Collections.singletonMap(lastAuditIdKey, lastAuditId);
            lastAuditIdsPerApplication.putAll(properties);
        }
    }

    public AlfrescoAuditApplicationRestClient getAuditApplicationRestClient()
    {
        return auditApplicationRestClient;
    }

    public void setAuditApplicationRestClient(AlfrescoAuditApplicationRestClient auditApplicationRestClient)
    {
        this.auditApplicationRestClient = auditApplicationRestClient;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Map<String, EcmAuditResponseReader> getAuditApplications()
    {
        return auditApplications;
    }

    public void setAuditApplications(Map<String, EcmAuditResponseReader> auditApplications)
    {
        this.auditApplications = auditApplications;
    }
}
