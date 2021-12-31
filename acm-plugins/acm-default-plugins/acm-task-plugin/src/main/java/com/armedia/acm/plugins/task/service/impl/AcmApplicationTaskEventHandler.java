package com.armedia.acm.plugins.task.service.impl;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmApplicationTaskEventHandler implements ApplicationListener<AcmApplicationTaskEvent>
{

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private TaskDao taskDao;
    private SendDocumentsToSolr sendDocumentsToSolr;
    private TaskToSolrTransformer taskToSolrTransformer;
    private List<String> eventList;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {

        if (getEventList().contains(event.getTaskEvent()))
        {
            AcmTask acmTask = (AcmTask) event.getSource();

            if (event.getTaskEvent().equals("create"))
            {
                getAuditPropertyEntityAdapter().setUserId(event.getUserId());
                try
                {
                    getTaskDao().createFolderForTaskEvent(acmTask);
                }
                catch (AcmTaskException | AcmCreateObjectFailedException e)
                {
                    log.error("Failed to create task container folder!", e.getMessage(), e);
                }
            }

            SolrAdvancedSearchDocument advancedDocument = getTaskToSolrTransformer().toSolrAdvancedSearch(acmTask);

            getSendDocumentsToSolr().sendSolrAdvancedSearchDocuments(Arrays.asList(advancedDocument));
        }

    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public SendDocumentsToSolr getSendDocumentsToSolr()
    {
        return sendDocumentsToSolr;
    }

    public void setSendDocumentsToSolr(SendDocumentsToSolr sendDocumentsToSolr)
    {
        this.sendDocumentsToSolr = sendDocumentsToSolr;
    }

    public TaskToSolrTransformer getTaskToSolrTransformer()
    {
        return taskToSolrTransformer;
    }

    public void setTaskToSolrTransformer(TaskToSolrTransformer taskToSolrTransformer)
    {
        this.taskToSolrTransformer = taskToSolrTransformer;
    }

    public List<String> getEventList()
    {
        return eventList;
    }

    public void setEventList(List<String> eventList)
    {
        this.eventList = eventList;
    }

}
