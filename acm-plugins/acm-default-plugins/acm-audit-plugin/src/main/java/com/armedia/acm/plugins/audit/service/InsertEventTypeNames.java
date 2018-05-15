package com.armedia.acm.plugins.audit.service;

/*-
 * #%L
 * ACM Default Plugin: Audit
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

import com.armedia.acm.audit.dao.AuditLookupDao;
import com.armedia.acm.audit.model.AcmAuditLookup;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.audit.model.AuditConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 26.06.2015.
 */
public class InsertEventTypeNames implements ApplicationContextAware
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmPlugin pluginEventType;
    private AuditLookupDao auditLookupDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {

        getAuditPropertyEntityAdapter().setUserId("audit-lookup");

        try
        {
            updateEventTypeNamesInTheDb();
        }
        catch (SQLException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Audit msgs was not inserted successfully" + e.getMessage(), e);
            }
        }
    }

    public void updateEventTypeNamesInTheDb() throws SQLException
    {
        Map<String, Object> props = getPluginEventType().getPluginProperties();
        boolean isForUpdate = false;
        if (props.containsKey(AuditConstants.AUDIT_UPDATE))
        {
            String forUpdate = (String) props.get(AuditConstants.AUDIT_UPDATE);
            isForUpdate = new Boolean(forUpdate).booleanValue();
        }
        if (isForUpdate)
        {
            try
            {
                getAuditLookupDao().deleteAllAuditsFormLookupTabel();
            }
            catch (NoResultException e)
            {
                if (log.isInfoEnabled())
                {
                    log.info("No Data into acm_audit_event_type_lu table found " + e.getMessage(), e);
                }
            }
            catch (PersistenceException e)
            {
                if (log.isInfoEnabled())
                {
                    log.info("No Data into acm_audit_event_type_lu table found " + e.getMessage(), e);
                }
            }
            insertNewDataIntoDb();
            if (log.isInfoEnabled())
            {
                log.info("New Audit Data inserted into acm_audit_event_type_lu table");
            }
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("No new data for inserting into acm_audit_event_type_lu table");
            }
        }
    }

    private void insertNewDataIntoDb()
    {
        Map<String, Object> props = getPluginEventType().getPluginProperties();
        long i = 0;
        for (Map.Entry<String, Object> entry : props.entrySet())
        {
            if (entry.getKey().contains(AuditConstants.EVENT_TYPE))
            {
                String key = entry.getKey().split(AuditConstants.EVENT_TYPE)[1];
                String value = (String) entry.getValue();
                if ("".equals(value))
                {
                    value = key;
                }
                AcmAuditLookup auditLookup = new AcmAuditLookup();
                auditLookup.setAuditBuisinessName(value);
                auditLookup.setAuditStatus(AuditConstants.AUDIT_STATUS_ACTIVE);
                auditLookup.setOrder(new Long(++i));
                auditLookup.setAuditEventName(key);
                auditLookup.setCreator(AuditConstants.ARK_AUDIT_USER);
                auditLookup.setModifier(AuditConstants.ARK_AUDIT_USER);
                try
                {
                    getAuditLookupDao().save(auditLookup);
                }
                catch (PersistenceException e)
                {
                    if (log.isInfoEnabled())
                    {
                        log.info("No Data into acm_audit_event_type_lu table found " + e.getMessage(), e);
                    }
                    throw e;
                }
                catch (Exception e)
                {
                    if (log.isInfoEnabled())
                    {
                        log.info("No Data into acm_audit_event_type_lu table found " + e.getMessage(), e);
                    }
                    throw e;
                }
            }
        }
    }

    public AcmPlugin getPluginEventType()
    {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType)
    {
        this.pluginEventType = pluginEventType;
    }

    public AuditLookupDao getAuditLookupDao()
    {
        return auditLookupDao;
    }

    public void setAuditLookupDao(AuditLookupDao auditLookupDao)
    {
        this.auditLookupDao = auditLookupDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
