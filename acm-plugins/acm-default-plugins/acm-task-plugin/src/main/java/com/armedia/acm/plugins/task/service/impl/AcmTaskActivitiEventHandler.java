package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.dataaccess.service.impl.DataAccessPrivilegeListener;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
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
    private Logger log = LoggerFactory.getLogger(getClass());

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
                } catch (AcmTaskException | AcmCreateObjectFailedException e)
                {
                    log.error("Failed to create task container folder!", e.getMessage(), e);
                }
            }

            getTaskDao().ensureCorrectAssigneeInParticipants(acmTask);

            getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(acmTask);

            // gotta check the assignee again to be sure the assignment rules didn't mess with it
            getTaskDao().ensureCorrectAssigneeInParticipants(acmTask);

            getAcmParticipantDao().removeAllOtherParticipantsForObject("TASK", acmTask.getTaskId(), acmTask.getParticipants());

            acmTask.setParticipants(getAcmParticipantDao().saveParticipants(acmTask.getParticipants()));

            SolrAdvancedSearchDocument advancedDocument = getTaskToSolrTransformer().toSolrAdvancedSearch(acmTask);
            getSendDocumentsToSolr().sendSolrAdvancedSearchDocuments(Arrays.asList(advancedDocument));

            SolrDocument quickDocument = getTaskToSolrTransformer().toSolrQuickSearch(acmTask);
            getSendDocumentsToSolr().sendSolrQuickSearchDocuments(Arrays.asList(quickDocument));
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

}
