package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 1/14/15.
 */
public class TaskToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTask>
{
    private UserDao userDao;
    private TaskDao taskDao;
    private SearchAccessControlFields searchAccessControlFields;

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<AcmTask> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTaskDao().getTasksModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmTask in)
    {
        log.trace("converting to advanced search doc: " + in.getId());
        SolrAdvancedSearchDocument doc = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(doc, in);

        doc.setId(in.getId() + "-TASK");
        doc.setTitle_parseable(in.getTitle());
        doc.setCreate_date_tdt(in.getCreateDate());
        doc.setCreator_lcs(in.getOwner());
        doc.setDescription_no_html_tags_parseable(in.getDetails());
        doc.setDueDate_tdt(in.getDueDate());
        doc.setModified_date_tdt(new Date());  // theoretically it's being indexed b/c it just changed
                                               // activiti does not keep a last modified field
        doc.setObject_id_s(Long.toString(in.getId()));
        doc.setObject_type_s("TASK");
        doc.setObject_sub_type_s(in.getBusinessProcessName());
        doc.setPriority_lcs(in.getPriority());
        doc.setParent_type_s(in.getParentObjectType());
        if ( in.getParentObjectId() != null )
        {
            doc.setParent_id_s(Long.toString(in.getParentObjectId()));
            doc.setParent_ref_s(Long.toString(in.getParentObjectId()) + "-" + in.getParentObjectType());
        }
        doc.setName(in.getTitle());
        doc.setStatus_lcs(in.getStatus());

        String assigneeUserId = in.getAssignee();
        doc.setAssignee_id_lcs(assigneeUserId);

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if ( assignee != null )
        {
            doc.setAssignee_first_name_lcs(assignee.getFirstName());
            doc.setAssignee_last_name_lcs(assignee.getLastName());
            doc.setAssignee_full_name_lcs(assignee.getFirstName() + " " + assignee.getLastName());
        }

        doc.setAdhocTask_b(in.isAdhocTask());
        doc.setOwner_lcs(in.getOwner());
        doc.setBusiness_process_name_lcs(in.getBusinessProcessName());

        log.trace("returning an advanced search doc");

        return doc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmTask in)
    {
        log.trace("converting to quick search doc: " + in.getId());
        SolrDocument doc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(doc, in);

        doc.setTitle_parseable(in.getTitle());
        doc.setDescription_no_html_tags_parseable(in.getDetails());
        doc.setObject_id_s(Long.toString(in.getId()));
        doc.setCreate_tdt(in.getCreateDate());
        doc.setName(in.getTitle());
        doc.setStatus_s(in.getStatus());
        doc.setAssignee_s(in.getAssignee());
        doc.setOwner_s(in.getOwner());
        doc.setId(in.getId() + "-TASK");
        doc.setPriority_s(in.getPriority());
        doc.setParent_object_type_s(in.getParentObjectType());
        doc.setParent_object_id_i(in.getParentObjectId());

        if ( in.getParentObjectId() != null )
        {
            doc.setParent_ref_s(Long.toString(in.getParentObjectId()) + "-" + in.getParentObjectType());
        }
        doc.setDue_tdt(in.getDueDate());
        doc.setAdhocTask_b(in.isAdhocTask());
        doc.setObject_type_s("TASK");
        doc.setAuthor_s(in.getOwner());
        doc.setLast_modified_tdt(new Date());

        log.trace("returning a quick search doc");

        return doc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmTask in) {
        //No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        boolean objectNotNull = acmObjectType != null;
        boolean isSupported = objectNotNull && AcmTask.class.equals(acmObjectType);

        return isSupported;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}
