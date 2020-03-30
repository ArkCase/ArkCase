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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.plugins.ecm.service.FileAclSolrUpdateHelper;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.json.JSONArray;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by armdev on 1/14/15.
 */
public class TaskToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTask>
{
    private final transient Logger log = LogManager.getLogger(getClass());
    private UserDao userDao;
    private TaskDao taskDao;
    private AcmDataService acmDataService;
    private FileAclSolrUpdateHelper fileAclSolrUpdateHelper;
    private SearchAccessControlFields searchAccessControlFields;

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
        doc.setModified_date_tdt(new Date()); // theoretically it's being indexed b/c it just changed
        // activiti does not keep a last modified field
        doc.setObject_id_s(Long.toString(in.getId()));
        doc.setObject_id_i(in.getId());
        doc.setObject_type_s("TASK");
        doc.setObject_sub_type_s(in.getBusinessProcessName());
        doc.setPriority_lcs(in.getPriority());
        doc.setType_lcs(in.getType());
        if (in.getParentObjectId() != null)
        {
            doc.setParent_type_s(in.getParentObjectType());
            doc.setParent_id_s(Long.toString(in.getParentObjectId()));
            doc.setParent_ref_s(Long.toString(in.getParentObjectId()) + "-" + in.getParentObjectType());
            doc.setParent_number_lcs(in.getParentObjectName());
        }
        doc.setName(in.getTitle());
        doc.setStatus_lcs(in.getStatus());

        String assigneeUserId = in.getAssignee();
        doc.setAssignee_id_lcs(assigneeUserId);

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if (assignee != null)
        {
            doc.setAssignee_first_name_lcs(assignee.getFirstName());
            doc.setAssignee_last_name_lcs(assignee.getLastName());
            doc.setAssignee_full_name_lcs(assignee.getFirstName() + " " + assignee.getLastName());
        }

        doc.setAdhocTask_b(in.isAdhocTask());
        doc.setOwner_lcs(in.getOwner());
        doc.setBusiness_process_name_lcs(in.getBusinessProcessName());
        doc.setBusiness_process_id_i(in.getBusinessProcessId());

        doc.setAdditionalProperty("candidate_group_ss", in.getCandidateGroups());

        // needed a _lcs property for sorting
        doc.setTitle_parseable_lcs(in.getTitle());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getOwner());
        if (creator != null)
        {
            doc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        doc.setAdditionalProperty("parent_title_s", in.getParentObjectTitle());

        doc.setAdditionalProperty("outcome_name_s", in.getOutcomeName());

        if (in.getAvailableOutcomes() != null)
        {
            List<String> outcomeValues = in.getAvailableOutcomes().stream().map(ao -> ao.getDescription()).collect(Collectors.toList());
            doc.setAdditionalProperty("outcome_value_ss", outcomeValues);
        }

        mapParentAclProperties(doc, in);
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
        doc.setObject_id_i(in.getId());
        doc.setCreate_tdt(in.getCreateDate());
        doc.setName(in.getTitle());
        doc.setStatus_s(in.getStatus());
        doc.setAssignee_s(in.getAssignee());
        doc.setOwner_s(in.getOwner());
        doc.setId(in.getId() + "-TASK");
        doc.setPriority_s(in.getPriority());
        doc.setType_s(in.getType());

        if (in.getParentObjectId() != null)
        {
            doc.setParent_object_type_s(in.getParentObjectType());
            doc.setParent_object_id_i(in.getParentObjectId());
            doc.setParent_ref_s(Long.toString(in.getParentObjectId()) + "-" + in.getParentObjectType());
        }
        doc.setBusiness_process_name_lcs(in.getBusinessProcessName());
        doc.setBusiness_process_id_i(in.getBusinessProcessId());
        doc.setDue_tdt(in.getDueDate());
        doc.setAdhocTask_b(in.isAdhocTask());
        doc.setObject_type_s("TASK");
        doc.setAuthor_s(in.getOwner());
        doc.setLast_modified_tdt(new Date());

        doc.setAdditionalProperty("candidate_group_ss", in.getCandidateGroups());
        doc.setAdditionalProperty("parent_title_s", in.getParentObjectTitle());
        /*
         * 'task_owner_s' is explicitly added as an additional property, because the current schema does not support
         * multivalues
         * and doc.setOwner_s would not work
         */
        doc.setAdditionalProperty("task_owner_s", in.getOwner());

        doc.setTitle_parseable_lcs(in.getTitle());

        doc.setAdditionalProperty("outcome_name_s", in.getOutcomeName());

        if (in.getAvailableOutcomes() != null)
        {
            List<String> outcomeValues = in.getAvailableOutcomes().stream().map(ao -> ao.getDescription()).collect(Collectors.toList());
            doc.setAdditionalProperty("outcome_value_ss", outcomeValues);
        }

        mapParentAclProperties(doc, in);

        log.trace("returning a quick search doc");

        return doc;
    }

    private void mapParentAclProperties(SolrBaseDocument doc, AcmTask in)
    {
        if (in.getParentObjectType() != null && in.getParentObjectId() != null)
        {
            AcmAbstractDao<AcmObject> parentDAO = acmDataService.getDaoByObjectType(in.getParentObjectType());
            if ( parentDAO != null )
            {
                AcmObject parent = parentDAO.find(in.getParentObjectId());
                if (parent instanceof AcmAssignedObject)
                {
                        getSearchAccessControlFields().setParentAccessControlFields(doc, (AcmAssignedObject) parent);
                }
            }
        }
    }

    @Override
    public JSONArray childrenUpdatesToSolr(AcmTask in)
    {
        if (in.getContainer() != null)
        {
            return fileAclSolrUpdateHelper.buildFileAclUpdates(in.getContainer().getId(), in);
        }
        return new JSONArray();
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmTask.class.equals(acmObjectType);
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

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmTask.class;
    }

    public FileAclSolrUpdateHelper getFileAclSolrUpdateHelper()
    {
        return fileAclSolrUpdateHelper;
    }

    public void setFileAclSolrUpdateHelper(FileAclSolrUpdateHelper fileAclSolrUpdateHelper)
    {
        this.fileAclSolrUpdateHelper = fileAclSolrUpdateHelper;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }
}
