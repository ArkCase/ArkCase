package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.dao.RecycleBinItemDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author darko.dimitrievski
 */

public class RecycleBinItemToSolrTransformer implements AcmObjectToSolrDocTransformer<RecycleBinItem>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private RecycleBinItemDao recycleBinItemDao;
    private EcmFileDao ecmFileDao;
    private AcmContainerDao acmContainerDao;
    private AcmFolderService folderService;
    private EcmFileService ecmFileService;

    @Override
    public List<RecycleBinItem> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getRecycleBinItemDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(RecycleBinItem in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + RecycleBinConstants.OBJECT_TYPE_ITEM);
        solr.setObject_id_s(in.getSourceObjectId() + "");
        solr.setObject_id_i(in.getSourceObjectId());
        solr.setObject_type_s(RecycleBinConstants.OBJECT_TYPE_ITEM);

        mapAdditionalPropertiesForRecycleBinItem(in, solr.getAdditionalProperties());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(RecycleBinItem in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + RecycleBinConstants.OBJECT_TYPE_ITEM);
        solr.setObject_id_s(in.getSourceObjectId() + "");
        solr.setObject_id_i(in.getSourceObjectId());
        solr.setObject_type_s(RecycleBinConstants.OBJECT_TYPE_ITEM);
        solr.setLast_modified_tdt(in.getModified());

        mapAdditionalPropertiesForRecycleBinItem(in, solr.getAdditionalProperties());
        return solr;
    }

    private void mapAdditionalPropertiesForRecycleBinItem(RecycleBinItem in, Map<String, Object> additionalProperties)
    {
        try
        {
            EcmFile file = getEcmFileDao().find(in.getSourceObjectId());
            AcmContainer container = getFolderService().findContainerByFolderIdTransactionIndependent(in.getSourceFolderId());
            if (file != null)
            {
                additionalProperties.put("object_name_s", file.getFileName());
                additionalProperties.put("object_item_type_s", file.getObjectType());
                additionalProperties.put("item_type_s", file.getFileActiveVersionNameExtension());
                additionalProperties.put("object_item_size_l", getSizeBytes(file));
                additionalProperties.put("object_folder_id_i", file.getFolder().getId());
                additionalProperties.put("item_id_i", in.getId());
            }
            additionalProperties.put("object_container_object_id_i", container.getId());
            additionalProperties.put("object_container_object_title_s", container.getContainerObjectTitle());
            additionalProperties.put("object_container_object_type_s", container.getContainerObjectType());

        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("Container with folder id: {} does not exists, reason {}  ", in.getSourceFolderId(), e.getMessage());
        }
    }

    private Long getSizeBytes(EcmFile ecmFile)
    {
        Optional<EcmFileVersion> ecmFileVersion = ecmFile.getVersions().stream()
                .filter(fileVersion -> fileVersion.getVersionTag().equals(ecmFile.getActiveVersionTag()))
                .findFirst();
        return ecmFileVersion.map(EcmFileVersion::getFileSizeBytes).orElse(0L);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return RecycleBinItem.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return RecycleBinItem.class;
    }

    public RecycleBinItemDao getRecycleBinItemDao()
    {
        return recycleBinItemDao;
    }

    public void setRecycleBinItemDao(RecycleBinItemDao recycleBinItemDao)
    {
        this.recycleBinItemDao = recycleBinItemDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
