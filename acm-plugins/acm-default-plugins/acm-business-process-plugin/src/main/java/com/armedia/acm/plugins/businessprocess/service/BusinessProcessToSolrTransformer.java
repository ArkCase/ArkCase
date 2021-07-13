package com.armedia.acm.plugins.businessprocess.service;

/*-
 * #%L
 * ACM Default Plugin: Acm Business Process
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_ID_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;

import com.armedia.acm.plugins.businessprocess.dao.BusinessProcessDao;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcess;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcessConstants;
import com.armedia.acm.plugins.ecm.service.FileAclSolrUpdateHelper;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BusinessProcessToSolrTransformer implements AcmObjectToSolrDocTransformer<BusinessProcess>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private UserDao userDao;
    private FileAclSolrUpdateHelper fileAclSolrUpdateHelper;
    private SearchAccessControlFields searchAccessControlFields;
    private BusinessProcessDao businessProcessDao;

    @Override
    public List<BusinessProcess> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return businessProcessDao.findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(BusinessProcess in)
    {

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for BUSINESS_PROCESS.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                BusinessProcessConstants.OBJECT_TYPE, solrDoc.getId());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(BusinessProcess in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(STATUS_LCS, in.getStatus());

        String assigneeUserId = ParticipantUtils.getOwnerIdFromParticipants(in.getParticipants());
        additionalProperties.put(ASSIGNEE_ID_LCS, assigneeUserId);

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if (assignee != null)
        {
            additionalProperties.put(ASSIGNEE_FIRST_NAME_LCS, assignee.getFirstName());
            additionalProperties.put(ASSIGNEE_LAST_NAME_LCS, assignee.getLastName());
            additionalProperties.put(ASSIGNEE_FULL_NAME_LCS, assignee.getFirstName() + " " + assignee.getLastName());
        }

        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFirstName() + " " + modifier.getLastName());
        }

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        additionalProperties.put(ACM_PARTICIPANTS_LCS, participantsListJson);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return BusinessProcess.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return BusinessProcess.class;
    }

    @Override
    public JSONArray childrenUpdatesToSolr(BusinessProcess in)
    {
        JSONArray docUpdates = fileAclSolrUpdateHelper.buildFileAclUpdates(in.getContainer().getId(), in);
        List<Long> childTasks = businessProcessDao.findTasksIdsForParentObjectIdAndParentObjectType(in.getObjectType(), in.getId());
        childTasks.forEach(it -> {
            JSONObject doc = searchAccessControlFields.buildParentAccessControlFieldsUpdate(in, String.format("%d-%s", it,
                    BusinessProcessConstants.OBJECT_TYPE_TASK));
            docUpdates.put(doc);
        });
        return docUpdates;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
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
