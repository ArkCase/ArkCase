package com.armedia.acm.plugins.documentrepository.service;

/*-
 * #%L
 * ACM Default Plugin: Document Repository
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_ID_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_NO_HTML_TAGS_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DocumentRepositoryToSolrTransformer implements AcmObjectToSolrDocTransformer<DocumentRepository>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private UserDao userDao;
    private FileAclSolrUpdateHelper fileAclSolrUpdateHelper;
    private SearchAccessControlFields searchAccessControlFields;
    private DocumentRepositoryDao documentRepositoryDao;

    @Override
    public List<DocumentRepository> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return documentRepositoryDao.findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(DocumentRepository in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for DOC_REPO.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getName());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(DocumentRepository in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getName());
        additionalProperties.put(DESCRIPTION_NO_HTML_TAGS_PARSEABLE, in.getDetails());
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

        /** Additional properties for full names instead of ID's */
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

        // This property is used for show in the owning group for the object
        additionalProperties.put("owning_group_id_lcs", ParticipantUtils.getOwningGroupIdFromParticipants(in.getParticipants()));
        additionalProperties.put("owning_group_id_s", ParticipantUtils.getOwningGroupIdFromParticipants(in.getParticipants()));

        additionalProperties.put("document_repository_name_s", in.getName());

        additionalProperties.put("repository_type_s", in.getRepositoryType());
    }

    @Override
    public JSONArray childrenUpdatesToSolr(DocumentRepository in)
    {
        return fileAclSolrUpdateHelper.buildFileAclUpdates(in.getContainer().getId(), in);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return DocumentRepository.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return DocumentRepository.class;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    public DocumentRepositoryDao getDocumentRepositoryDao()
    {
        return documentRepositoryDao;
    }

    public void setDocumentRepositoryDao(DocumentRepositoryDao documentRepositoryDao)
    {
        this.documentRepositoryDao = documentRepositoryDao;
    }

    public FileAclSolrUpdateHelper getFileAclSolrUpdateHelper()
    {
        return fileAclSolrUpdateHelper;
    }

    public void setFileAclSolrUpdateHelper(FileAclSolrUpdateHelper fileAclSolrUpdateHelper)
    {
        this.fileAclSolrUpdateHelper = fileAclSolrUpdateHelper;
    }
}
