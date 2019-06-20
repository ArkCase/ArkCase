package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.admin.exception.AcmPropertiesManagementException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.json.JSONException;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by will.phillips on 8/18/2016.
 */
public class HistoryCleanService
{
    private JsonPropertiesManagementService jsonPropertiesManagementService;
    private AuditDao auditDao;
    private NotificationDao notificationDao;
    private ExecuteSolrQuery executeSolrQuery;

    private Logger log = LogManager.getLogger(getClass());

    public void cleanHistory()
    {
        int historyDays = 0;
        try
        {
            historyDays = jsonPropertiesManagementService.getProperties().getInt("historyDays");
        }
        catch (AcmPropertiesManagementException | JSONException | NullPointerException | ClassCastException e)
        {
            log.warn("History clean setting is not defined, disabling by default.");
            return;
        }

        if (historyDays <= 0)
        {
            log.debug("History cleaning is disabled. Stopping now.");
            return;
        }

        Date threshold = getDateThreshold(historyDays);

        log.info("Cleaning out audit events older than {} days...", historyDays);
        auditDao.purgeAudits(threshold);

        log.info("Cleaning out notifications older than {} days...", historyDays);
        notificationDao.purgeNotifications(threshold);

        // Delete notifications from solr, using same query as for database
        try
        {
            executeSolrQuery.sendSolrDeleteQuery("solrAdvancedSearch.in", createDeleteNotificationSolrQuery(threshold));
        }
        catch (MuleException e)
        {
            log.error("couldn't delete notifications in solr.", e);
        }
    }

    private String createDeleteNotificationSolrQuery(Date threshold)
    {
        SimpleDateFormat dateParser = new SimpleDateFormat(DateFormats.DEFAULT_DATE_FORMAT);
        String query = "object_type_facet:" + NotificationConstants.OBJECT_TYPE
                + " AND modified_date_tdt:";
        query += "[* TO " + dateParser.format(threshold) + "]";
        return query;
    }

    public Date getDateThreshold(int historyDays)
    {
        // Create Calendar object. (Is set with current datetime.)
        Calendar calendar = Calendar.getInstance();

        // Go back X days into the past.
        calendar.add(Calendar.DAY_OF_MONTH, historyDays * -1);

        return calendar.getTime();
    }

    public JsonPropertiesManagementService getJsonPropertiesManagementService()
    {
        return jsonPropertiesManagementService;
    }

    public void setJsonPropertiesManagementService(JsonPropertiesManagementService jsonPropertiesManagementService)
    {
        this.jsonPropertiesManagementService = jsonPropertiesManagementService;
    }

    public AuditDao getAuditDao()
    {
        return auditDao;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
