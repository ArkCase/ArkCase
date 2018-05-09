/**
 *
 */
package com.armedia.acm.audit.service;

/*-
 * #%L
 * ACM Service: Audit Library
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

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.log4j2.ConfidentialDataConverter;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.systemlogger.ISystemLogger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class AuditServiceImpl implements AuditService
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private boolean batchRun;
    private int purgeDays;
    private boolean databaseLoggerEnabled;
    private boolean systemLogLoggerEnabled;
    private ISystemLogger systemLogger;
    private AuditDao auditDao;
    private ConfidentialDataConverter confidentialDataConverter = ConfidentialDataConverter.newInstanceWithoutFormatters();

    /**
     * This method is called by scheduled task
     */
    @Override
    public void purgeBatchRun()
    {
        if (!isBatchRun())
        {
            return;
        }

        Date dateThreshold = createPurgeThreshold();

        int deletedAudits = getAuditDao().purgeAudits(dateThreshold);

        LOG.debug(deletedAudits + " audits was deleted.");
    }

    @Override
    public void audit(AuditEvent auditEvent)
    {
        try
        {
            convertConfidentialProperties(auditEvent);

            if (isDatabaseLoggerEnabled())
            {
                auditDao.save(auditEvent);
            }
            if (isSystemLogLoggerEnabled())
            {
                systemLogger.log(auditEvent.toString());
            }
        }
        catch (Throwable t)
        {
            LOG.error("Error auditing event.", t);
        }
    }

    private void convertConfidentialProperties(AuditEvent auditEvent)
    {
        for (Map.Entry<String, String> entry : auditEvent.getEventProperties().entrySet())
        {
            // convert by matching the value against the patterns
            entry.setValue(confidentialDataConverter.convert(entry.getValue()));

            // substitute value if the key contains 'password'
            if (StringUtils.containsIgnoreCase(entry.getKey(), "password"))
            {
                entry.setValue(confidentialDataConverter.getSubstitution());
            }
        }
    }

    private Date createPurgeThreshold()
    {
        int purgeDays = getPurgeDays();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -purgeDays);

        return calendar.getTime();
    }

    public boolean isBatchRun()
    {
        return batchRun;
    }

    public void setBatchRun(boolean batchRun)
    {
        this.batchRun = batchRun;
    }

    public int getPurgeDays()
    {
        return purgeDays;
    }

    public void setPurgeDays(int purgeDays)
    {
        this.purgeDays = purgeDays;
    }

    public AuditDao getAuditDao()
    {
        return auditDao;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }

    public ISystemLogger getSystemLogger()
    {
        return systemLogger;
    }

    public void setSystemLogger(ISystemLogger systemLogger)
    {
        this.systemLogger = systemLogger;
    }

    public boolean isDatabaseLoggerEnabled()
    {
        return databaseLoggerEnabled;
    }

    public void setDatabaseLoggerEnabled(boolean databaseLoggerEnabled)
    {
        this.databaseLoggerEnabled = databaseLoggerEnabled;
    }

    public boolean isSystemLogLoggerEnabled()
    {
        return systemLogLoggerEnabled;
    }

    public void setSystemLogLoggerEnabled(boolean systemLogLoggerEnabled)
    {
        this.systemLogLoggerEnabled = systemLogLoggerEnabled;
    }

    /**
     * @return the confidentialDataConverter
     */
    public ConfidentialDataConverter getConfidentialDataConverter()
    {
        return confidentialDataConverter;
    }

    /**
     * @param confidentialDataConverter
     *            the confidentialDataConverter to set
     */
    public void setConfidentialDataConverter(ConfidentialDataConverter confidentialDataConverter)
    {
        this.confidentialDataConverter = confidentialDataConverter;
    }
}
