package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;

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

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {

        if (getEventList().contains(event.getTaskEvent()))
        {
            AcmTask acmTask = (AcmTask) event.getSource();

            SolrAdvancedSearchDocument advancedDocument = getTaskToSolrTransformer().toSolrAdvancedSearch(acmTask);
            SolrDocument quickDocument = getTaskToSolrTransformer().toSolrQuickSearch(acmTask);

            getSendDocumentsToSolr().sendSolrAdvancedSearchDocuments(Arrays.asList(advancedDocument));
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
