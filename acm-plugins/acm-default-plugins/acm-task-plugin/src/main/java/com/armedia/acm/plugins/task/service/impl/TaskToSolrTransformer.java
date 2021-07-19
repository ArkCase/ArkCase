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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ADHOC_TASK_B;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_ID_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.BUSINESS_PROCESS_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.BUSINESS_PROCESS_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_NO_HTML_TAGS_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.OWNER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PRIORITY_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.plugins.ecm.service.FileAclSolrUpdateHelper;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        log.debug("Creating Solr advanced search document for TASK.");
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        mapRequiredProperties(solrDoc, in.getId(), in.getOwner(), in.getCreateDate(), null, new Date(), "TASK", in.getTitle());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        solrDoc.setDueDate_tdt(in.getDueDate());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        mapParentAclProperties(solrDoc, in);
        log.trace("returning an advanced search doc");

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmTask in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getTitle());
        additionalProperties.put(DESCRIPTION_NO_HTML_TAGS_PARSEABLE, in.getDetails());
        additionalProperties.put("object_sub_type_s", in.getBusinessProcessName());
        additionalProperties.put(PRIORITY_LCS, in.getPriority());
        additionalProperties.put(TYPE_LCS, in.getType());
        if (in.getParentObjectId() != null)
        {
            additionalProperties.put(PARENT_TYPE_S, in.getParentObjectType());
            additionalProperties.put(PARENT_ID_S, Long.toString(in.getParentObjectId()));
            additionalProperties.put(PARENT_REF_S, in.getParentObjectId() + "-" + in.getParentObjectType());
            additionalProperties.put(PARENT_NUMBER_LCS, in.getParentObjectName());
        }
        additionalProperties.put(STATUS_LCS, in.getStatus());

        String assigneeUserId = in.getAssignee();
        additionalProperties.put(ASSIGNEE_ID_LCS, assigneeUserId);

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if (assignee != null)
        {
            additionalProperties.put(ASSIGNEE_FIRST_NAME_LCS, assignee.getFirstName());
            additionalProperties.put(ASSIGNEE_LAST_NAME_LCS, assignee.getLastName());
            additionalProperties.put(ASSIGNEE_FULL_NAME_LCS, assignee.getFirstName() + " " + assignee.getLastName());
        }

        additionalProperties.put(ADHOC_TASK_B, in.isAdhocTask());
        additionalProperties.put(OWNER_LCS, in.getOwner());
        additionalProperties.put(BUSINESS_PROCESS_NAME_LCS, in.getBusinessProcessName());
        additionalProperties.put(BUSINESS_PROCESS_ID_I, in.getBusinessProcessId());

        AcmParticipant owningGroupParticipantLdapId = in.getParticipants().stream().filter(p -> p.getParticipantType().equals("owning group")).findFirst().orElse(null);
        if (owningGroupParticipantLdapId != null)
        {
            additionalProperties.put("candidate_group_ss", in.getParticipants().stream()
                    .filter(p -> p.getParticipantType().equals("owning group")).findFirst().get().getParticipantLdapId());
        }
        else
        {
            additionalProperties.put("candidate_group_ss", in.getCandidateGroups());
        }

        // needed a _lcs property for sorting
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getTitle());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getOwner());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        additionalProperties.put("parent_title_s", in.getParentObjectTitle());
        additionalProperties.put("outcome_name_s", in.getOutcomeName());

        if (in.getAvailableOutcomes() != null)
        {
            List<String> outcomeValues = in.getAvailableOutcomes().stream().map(ao -> ao.getDescription()).collect(Collectors.toList());
            additionalProperties.put("outcome_value_ss", outcomeValues);
        }
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
