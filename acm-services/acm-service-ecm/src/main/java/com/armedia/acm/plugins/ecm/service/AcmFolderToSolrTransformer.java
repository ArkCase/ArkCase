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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;

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
        // no implementation needed yet
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmFolder in)
    {

        AcmFolder parentFolder = in.getParentFolder();

        SolrDocument doc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(doc, in);

        doc.setAuthor_s(in.getCreator());
        doc.setAuthor(in.getCreator());
        doc.setObject_type_s(in.getObjectType());
        doc.setObject_id_s("" + in.getId());
        doc.setCreate_tdt(in.getCreated());
        doc.setId(in.getId() + "-" + in.getObjectType());
        doc.setLast_modified_tdt(in.getModified());
        doc.setName(in.getName());
        doc.setModifier_s(in.getModifier());
        doc.setParent_object_id_i(parentFolder == null ? null : parentFolder.getId());
        doc.setParent_object_id_s(parentFolder == null ? null : "" + parentFolder.getId());
        doc.setParent_object_type_s(parentFolder == null ? null : in.getObjectType());
        doc.setTitle_parseable(in.getName());
        doc.setTitle_t(in.getName());

        // folder id will be used to find files and folders that belong to this container
        doc.setFolder_id_i(in.getId());
        doc.setFolder_name_s(in.getName());

        // need an _lcs field for sorting
        doc.setName_lcs(in.getName());

        doc.setParent_folder_id_i(parentFolder == null ? null : parentFolder.getId());

        doc.setStatus_s(in.getStatus());

        doc.setAdditionalProperty("name_partial", in.getName());

        if (parentFolder != null)
        {
            try
            {
                AcmContainer container = getFolderService().findContainerByFolderIdTransactionIndependent(parentFolder.getId());
                doc.getAdditionalProperties().put("parent_container_object_type_s", container.getContainerObjectType());
                doc.getAdditionalProperties().put("parent_container_object_id_s", container.getContainerObjectId());
                doc.getAdditionalProperties().put("link_b", in.isLink());
            }
            catch (AcmObjectNotFoundException e)
            {
                log.debug("Failed to index AcmContainer info fields for folder with id: [{}] ", in.getId(), e);
            }
        }

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        doc.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        return doc;
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
