package com.armedia.acm.plugins.consultation.transformer;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
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
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationToSolrTransformer implements AcmObjectToSolrDocTransformer<Consultation>
{
    private UserDao userDao;
    private ConsultationDao consultationDao;
    private FileAclSolrUpdateHelper fileAclSolrUpdateHelper;
    private SearchAccessControlFields searchAccessControlFields;
    private BusinessProcessDao businessProcessDao;

    @Override
    public List<Consultation> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getConsultationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Consultation in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(in.getId() + "-CONSULTATION");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s(ConsultationConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getConsultationNumber());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setDueDate_tdt(in.getDueDate());

        solr.setIncident_date_tdt(in.getIncidentDate());
        solr.setPriority_lcs(in.getPriority());
        solr.setStatus_lcs(in.getStatus());

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_id_lcs(assigneeUserId);

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

        solr.setAdditionalProperty("security_field_lcs", in.getSecurityField());

        solr.setTitle_parseable_lcs(in.getTitle());

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
    public SolrDocument toSolrQuickSearch(Consultation in)
    {
        SolrDocument solr = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setName(in.getConsultationNumber());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s(ConsultationConstants.OBJECT_TYPE);
        solr.setId(in.getId() + "-CONSULTATION");

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus());

        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setTitle_parseable(in.getTitle());

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_s(assigneeUserId);

        // needed a _lcs property for sorting
        solr.setTitle_parseable_lcs(in.getTitle());

        return solr;
    }

    @Override
    public JSONArray childrenUpdatesToSolr(Consultation in)
    {
        JSONArray docUpdates = fileAclSolrUpdateHelper.buildFileAclUpdates(in.getContainer().getId(), in);
        List<Long> childTasks = businessProcessDao.findTasksIdsForParentObjectIdAndParentObjectType(in.getObjectType(), in.getId());
        childTasks.forEach(it -> {
            JSONObject doc = searchAccessControlFields.buildParentAccessControlFieldsUpdate(in, String.format("%d-%s", it,
                    TaskConstants.OBJECT_TYPE));
            docUpdates.put(doc);
        });
        return docUpdates;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Consultation.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
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
        return Consultation.class;
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
