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

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.dataaccess.service.impl.DataAccessPrivilegeListener;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

/**
 * @author sasko.tanaskoski
 */
public class AcmTaskActivitiEventHandler implements ApplicationListener<AcmTaskActivitiEvent>
{

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private TaskDao taskDao;
    private DataAccessPrivilegeListener dataAccessPrivilegeListener;
    private AcmParticipantDao acmParticipantDao;
    private SendDocumentsToSolr sendDocumentsToSolr;
    private TaskToSolrTransformer taskToSolrTransformer;
    private List<String> eventList;
    private Logger log = LogManager.getLogger(getClass());
    private TaskService taskService;

    @Override
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {

        if (getEventList().contains(event.getTaskEvent()))
        {
            AcmTask acmTask = getTaskDao().acmTaskFromActivitiTask((Task) event.getSource(), event.getProcessVariables(),
                    event.getLocalVariables(), event.getTaskEvent());

            // ensure we set the right modifier and creator for any objects we end up inserting or updating
            getAuditPropertyEntityAdapter().setUserId(event.getUserId());

            if (event.getTaskEvent().equals("create"))
            {
                try
                {
                    getTaskDao().createFolderForTaskEvent(acmTask);
                }
                catch (AcmTaskException | AcmCreateObjectFailedException e)
                {
                    log.error("Failed to create task container folder!", e.getMessage(), e);
                }

                Task task = (Task) event.getSource();

                String user = SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                        ? SecurityContextHolder.getContext().getAuthentication().getName()
                        : "";

                log.debug("Setting OWNER=[{}] for Task ID=[{}]", user, task.getId());

                taskService.setOwner(task.getId(), user);
                acmTask.setOwner(user);

            }
            // next if blocks make sure Solr gets the right task status
            else if ("delete".equals(event.getTaskEvent()))
            {
                acmTask.setStatus(TaskConstants.STATE_DELETE);
            }
            else if ("complete".equals(event.getTaskEvent()))
            {
                acmTask.setStatus(TaskConstants.STATE_CLOSED);
            }
            else if ("terminate".equals(event.getTaskEvent()))
            {
                acmTask.setStatus(TaskConstants.STATE_TERMINATED);
            }

            getTaskDao().ensureCorrectAssigneeInParticipants(acmTask);

            try
            {
                getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(acmTask);
            }
            catch (AcmAccessControlException e)
            {
                log.error("Error applying assignment and access rules on AcmTaskActivitiEvent", e);
            }

            // gotta check the assignee again to be sure the assignment rules didn't mess with it
            getTaskDao().ensureCorrectAssigneeInParticipants(acmTask);

            getAcmParticipantDao().removeAllOtherParticipantsForObject("TASK", acmTask.getTaskId(), acmTask.getParticipants());

            acmTask.setParticipants(getAcmParticipantDao().saveParticipants(acmTask.getParticipants()));

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

    public DataAccessPrivilegeListener getDataAccessPrivilegeListener()
    {
        return dataAccessPrivilegeListener;
    }

    public void setDataAccessPrivilegeListener(DataAccessPrivilegeListener dataAccessPrivilegeListener)
    {
        this.dataAccessPrivilegeListener = dataAccessPrivilegeListener;
    }

    public AcmParticipantDao getAcmParticipantDao()
    {
        return acmParticipantDao;
    }

    public void setAcmParticipantDao(AcmParticipantDao acmParticipantDao)
    {
        this.acmParticipantDao = acmParticipantDao;
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

    public TaskService getTaskService()
    {
        return taskService;
    }

    public void setTaskService(TaskService taskService)
    {
        this.taskService = taskService;
    }
}
