package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrConfig;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by armdev on 10/28/14.
 */
public class AcmJpaBatchUpdateService
{
    private final Logger log = LogManager.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private JpaObjectsToSearchService objectsToSearchService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SolrConfig solrConfig;
    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is
     * missing)
     */
    private static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    public void jpaBatchUpdate(JobDataMap lastRunDatePerObject) throws InterruptedException
    {
        log.debug("JPA batch update enabled: [{}]", solrConfig.getEnableBatchUpdateBasedOnLastModified());

        if (!solrConfig.getEnableBatchUpdateBasedOnLastModified())
        {
            return;
        }

        // Wait for all IJpaBatchUpdatePrerequisite implementations to finish their work
        while (!prerequisitesFinished())
        {
            log.debug("Waiting for the IJpaBatchUpdatePrerequisite implementations to finish...");
            Thread.sleep(1000L);
        }

        // The Alfresco user id to use, to retrieve the files to be indexed
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        getAuditPropertyEntityAdapter().setUserId("SOLR-BATCH-UPDATE");

        Collection<? extends AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectToSolrDocTransformer.class).values();

        log.debug("[{}] object transformers found.", transformers.size());

        DateFormat solrDateFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);

        for (AcmObjectToSolrDocTransformer transformer : transformers)
        {
            String acmObjectClassName = transformer.getAcmObjectTypeSupported().getCanonicalName();
            try
            {
                Date lastBatchRunDate = getLastBatchRunDate(lastRunDatePerObject.getString(acmObjectClassName),
                        solrDateFormat);

                String solrNow = solrDateFormat.format(new Date());
                lastRunDatePerObject.put(acmObjectClassName, solrNow);

                log.debug("Checking for [{}] objects modified since [{}]", acmObjectClassName, lastBatchRunDate);
                sendUpdatedObjectsToSolr(lastBatchRunDate, transformer);
            }
            catch (Exception exception)
            {
                log.error("Could not send index updates to SOLR for transformer [{}]", transformer.getClass(), exception);
            }
        }
    }

    private boolean prerequisitesFinished()
    {
        Map<String, IJpaBatchUpdatePrerequisite> prerequisites = springContextHolder.getAllBeansOfType(IJpaBatchUpdatePrerequisite.class);
        for (IJpaBatchUpdatePrerequisite prerequisite : prerequisites.values())
        {
            if (!prerequisite.isFinished())
            {
                return false;
            }
        }
        return true;
    }

    private Date getLastBatchRunDate(String lastRunDate, DateFormat solrDateFormat) throws ParseException
    {
        if (lastRunDate == null)
        {
            lastRunDate = DEFAULT_LAST_RUN_DATE;
        }
        Date sinceWhen = solrDateFormat.parse(lastRunDate);

        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(sinceWhen);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        sinceWhen = cal.getTime();
        return sinceWhen;
    }

    private void sendUpdatedObjectsToSolr(Date lastUpdate, AcmObjectToSolrDocTransformer transformer)
    {
        log.debug("Handling transformer type: {}, last mod date: {}", transformer.getClass().getName(), lastUpdate);

        int current = 0;
        int batchSize = solrConfig.getBatchUpdateBatchSize();

        // keep retrieving another batch of objects modified since the last update, until we find no more objects.
        List<Object> updatedObjects;
        do
        {
            updatedObjects = transformer.getObjectsModifiedSince(lastUpdate, current, batchSize);

            log.debug("Number of objects for {} : {}", transformer.getClass().getName(), updatedObjects.size());

            if (!updatedObjects.isEmpty())
            {
                current += batchSize;

                AcmObjectChangelist changelist = new AcmObjectChangelist();
                changelist.setUpdatedObjects(updatedObjects);
                getObjectsToSearchService().updateObjectsInSolr(changelist);
            }
        } while (!updatedObjects.isEmpty());

    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public JpaObjectsToSearchService getObjectsToSearchService()
    {
        return objectsToSearchService;
    }

    public void setObjectsToSearchService(JpaObjectsToSearchService objectsToSearchService)
    {
        this.objectsToSearchService = objectsToSearchService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public SolrConfig getSolrConfig()
    {
        return solrConfig;
    }

    public void setSolrConfig(SolrConfig solrConfig)
    {
        this.solrConfig = solrConfig;
    }
}
