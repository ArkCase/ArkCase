package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_FOLDER_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_OBJECT_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 3/23/15.
 */
public class AcmFolderToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmFolder>
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService folderService;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<AcmFolder> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getFolderService().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmFolder in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for FOLDER.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(), "FOLDER",
                in.getName());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());
        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmFolder in, Map<String, Object> additionalProperties)
    {
        AcmFolder parentFolder = in.getParentFolder();

        additionalProperties.put(TITLE_PARSEABLE, in.getName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getName());
        additionalProperties.put("title_t", in.getName());

        additionalProperties.put(PARENT_FOLDER_ID_I, parentFolder == null ? null : parentFolder.getId());

        additionalProperties.put(STATUS_LCS, in.getStatus());

        additionalProperties.put(PARENT_OBJECT_ID_I, parentFolder == null ? null : parentFolder.getId());
        additionalProperties.put(PARENT_ID_S, parentFolder == null ? null : "" + parentFolder.getId());
        additionalProperties.put(PARENT_TYPE_S, parentFolder == null ? null : in.getObjectType());

        // folder id will be used to find files and folders that belong to this container
        additionalProperties.put("folder_id_i", in.getId());
        additionalProperties.put("folder_name_s", in.getName());

        additionalProperties.put("name_partial", in.getName());

        if (parentFolder != null)
        {
            try
            {
                AcmContainer container = getFolderService().findContainerByFolderIdTransactionIndependent(parentFolder.getId());

                additionalProperties.put("parent_container_object_type_s", container.getContainerObjectType());
                additionalProperties.put("parent_container_object_id_s", container.getContainerObjectId());
                additionalProperties.put("link_b", in.isLink());
            }
            catch (AcmObjectNotFoundException e)
            {
                log.debug("Failed to index AcmContainer info fields for folder with id: [{}] ", in.getId(), e);
            }
        }

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        additionalProperties.put(ACM_PARTICIPANTS_LCS, participantsListJson);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmFolder.class.equals(acmObjectType);
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
        return AcmFolder.class;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }
}
