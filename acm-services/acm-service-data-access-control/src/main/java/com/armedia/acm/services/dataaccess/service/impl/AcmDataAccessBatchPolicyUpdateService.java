package com.armedia.acm.services.dataaccess.service.impl;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/28/14.
 */
public class AcmDataAccessBatchPolicyUpdateService
{
    private final Logger log = LogManager.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmDataAccessBatchUpdater dataAccessBatchUpdater;
    private DataAccessControlConfig dacConfig;

    public void batchPolicyUpdate(Date lastRunDate)
    {
        log.debug("DAC batch update enabled: {}", dacConfig.getBatchUpdateBasedOnLastModifiedEnabled());

        if (!dacConfig.getBatchUpdateBasedOnLastModifiedEnabled())
        {
            return;
        }
        getAuditPropertyEntityAdapter().setUserId("DAC-BATCH-UPDATE");

        lastRunDate = lastRunDate == null ? new Date() : lastRunDate;

        Date lastBatchRunDate = getLastBatchRunDate(lastRunDate);

        log.debug("Checking for objects modified since: [{}]", lastBatchRunDate);

        Collection<? extends AcmObjectDataAccessBatchUpdateLocator> locators = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectDataAccessBatchUpdateLocator.class).values();
        log.debug("[{}] object locators found.", locators.size());

        for (AcmObjectDataAccessBatchUpdateLocator locator : locators)
        {
            try
            {
                updateDataAccessControlPolicy(lastBatchRunDate, locator);
            }
            catch (Exception exception)
            {
                log.error("Could not update data access controls for locator [{}]", locator.getClass(), exception);
            }
        }
    }

    private Date getLastBatchRunDate(Date sinceWhen)
    {
        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(sinceWhen);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        sinceWhen = cal.getTime();
        return sinceWhen;
    }

    private void updateDataAccessControlPolicy(Date lastUpdate, AcmObjectDataAccessBatchUpdateLocator locator)
            throws AcmAccessControlException
    {
        log.debug("Handling locator type: [{}]; last mod date: [{}]", locator.getClass().getName(), lastUpdate);

        int current = 0;
        int batchSize = dacConfig.getBatchUpdateBatchSize();

        // keep retrieving another batch of objects modified since the last update, until we find no more objects.
        List<AcmAssignedObject> updatedObjects;
        do
        {
            updatedObjects = locator.getObjectsModifiedSince(lastUpdate, current, batchSize);
            log.debug("Number of objects for [{}]:[{}]", locator.getClass().getName(), updatedObjects.size());

            if (!updatedObjects.isEmpty())
            {
                current += batchSize;
                getDataAccessBatchUpdater().updateDataAccessPolicy(updatedObjects, locator);
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmDataAccessBatchUpdater getDataAccessBatchUpdater()
    {
        return dataAccessBatchUpdater;
    }

    public void setDataAccessBatchUpdater(AcmDataAccessBatchUpdater dataAccessBatchUpdater)
    {
        this.dataAccessBatchUpdater = dataAccessBatchUpdater;
    }

    public DataAccessControlConfig getDacConfig()
    {
        return dacConfig;
    }

    public void setDacConfig(DataAccessControlConfig dacConfig)
    {
        this.dacConfig = dacConfig;
    }
}
