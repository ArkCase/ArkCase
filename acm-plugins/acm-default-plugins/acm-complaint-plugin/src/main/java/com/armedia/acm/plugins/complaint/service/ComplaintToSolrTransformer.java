package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.plugins.businessprocess.dao.BusinessProcessDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.FileAclSolrUpdateHelper;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/28/14.
 */
public class ComplaintToSolrTransformer implements AcmObjectToSolrDocTransformer<Complaint>
{
    private UserDao userDao;
    private ComplaintDao complaintDao;
    private FileAclSolrUpdateHelper fileAclSolrUpdateHelper;
    private SearchAccessControlFields searchAccessControlFields;
    private BusinessProcessDao businessProcessDao;

    @Override
    public List<Complaint> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getComplaintDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Complaint in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(in.getComplaintId() + "-COMPLAINT");
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_id_i(in.getComplaintId());
        solr.setObject_type_s("COMPLAINT");
        solr.setTitle_parseable(in.getComplaintTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getComplaintNumber());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setDueDate_tdt(in.getDueDate());

        solr.setIncident_date_tdt(in.getIncidentDate());
        solr.setPriority_lcs(in.getPriority());
        solr.setIncident_type_lcs(in.getComplaintType());
        solr.setStatus_lcs(in.getStatus());

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_id_lcs(assigneeUserId);

        solr.setTitle_parseable_lcs(in.getComplaintTitle());

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if (assignee != null)
        {
            solr.setAssignee_first_name_lcs(assignee.getFirstName());
            solr.setAssignee_last_name_lcs(assignee.getLastName());
            solr.setAssignee_full_name_lcs(assignee.getFirstName() + " " + assignee.getLastName());
        }

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        solr.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        // The property "assignee_group_id_lcs" is used only for showing/hiding claim/unclaim buttons
        solr.setAdditionalProperty("assignee_group_id_lcs", in.getAssigneeGroup());

        // This property is used for showin the owning group for the object
        solr.setAdditionalProperty("owning_group_id_lcs", ParticipantUtils.getOwningGroupIdFromParticipants(in.getParticipants()));
        solr.setAdditionalProperty("owning_group_id_s", ParticipantUtils.getOwningGroupIdFromParticipants(in.getParticipants()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Complaint in)
    {
        SolrDocument solr = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setName(in.getComplaintNumber());
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s("COMPLAINT");
        solr.setId(in.getComplaintId() + "-COMPLAINT");

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setDue_tdt(in.getDueDate());
        solr.setTitle_parseable(in.getComplaintTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setStatus_s(in.getStatus());

        if (in.getDisposition() != null && in.getDisposition().getId() != null)
        {
            solr.setDisposition_id_s(in.getDisposition().getId() + "-" + in.getDisposition().getObjectType());
        }

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_s(assigneeUserId);

        // needed a _lcs property for sorting
        solr.setTitle_parseable_lcs(in.getComplaintTitle());
        return solr;
    }

    @Override
    public JSONArray childrenUpdatesToSolr(Complaint in)
    {
        JSONArray docUpdates = fileAclSolrUpdateHelper.buildFileAclUpdates(in.getContainer().getId(), in);
        List<Long> childTasksIds = businessProcessDao.findTasksIdsForParentObjectIdAndParentObjectType(in.getObjectType(), in.getId());
        childTasksIds.forEach(it -> {
            JSONObject doc = searchAccessControlFields.buildParentAccessControlFieldsUpdate(in, String.format("%d-%s", it,
                    TaskConstants.OBJECT_TYPE));
            docUpdates.put(doc);
        });
        return docUpdates;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Complaint.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
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
        return Complaint.class;
    }

    public FileAclSolrUpdateHelper getFileAclSolrUpdateHelper()
    {
        return fileAclSolrUpdateHelper;
    }

    public void setFileAclSolrUpdateHelper(FileAclSolrUpdateHelper fileAclSolrUpdateHelper)
    {
        this.fileAclSolrUpdateHelper = fileAclSolrUpdateHelper;
    }

    public BusinessProcessDao getBusinessProcessDao() 
    {
        return businessProcessDao;
    }

    public void setBusinessProcessDao(BusinessProcessDao businessProcessDao) 
    {
        this.businessProcessDao = businessProcessDao;
    }
}
