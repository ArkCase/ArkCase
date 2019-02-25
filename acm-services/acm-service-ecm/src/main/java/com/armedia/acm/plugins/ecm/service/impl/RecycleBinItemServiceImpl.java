package com.armedia.acm.plugins.ecm.service.impl;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.dao.RecycleBinItemDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinDTO;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author darko.dimitrievski
 */

public class RecycleBinItemServiceImpl implements RecycleBinItemService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private RecycleBinItemDao recycleBinItemDao;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;
    private ExecuteSolrQuery solrQuery;
    private SearchResults searchResults;
    private AcmFolderService folderService;
    private FileEventPublisher fileEventPublisher;

    @Override
    public RecycleBinItem save(RecycleBinItem recycleBinItem)
    {
        return recycleBinItemDao.save(recycleBinItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecycleBinItem putFileIntoRecycleBin(EcmFile ecmFile, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        AcmContainer destinationContainer = getOrCreateContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE, ecmFile.getCmisRepositoryId(), authentication);
        AcmContainer sourceContainer = getFolderService().findContainerByFolderIdTransactionIndependent(ecmFile.getFolder().getId());

        RecycleBinItem recycleBinItem = new RecycleBinItem(ecmFile.getId(), ecmFile.getObjectType(),
               ecmFile.getFolder().getId(), ecmFile.getCmisRepositoryId(), sourceContainer.getId());
        getRecycleBinItemDao().save(recycleBinItem);
        moveToCMISFolder(ecmFile, destinationContainer.getContainerObjectId(), destinationContainer.getContainerObjectType(), destinationContainer.getFolder().getId());
        log.info("File {} successfully moved in Recycle Bin, by user {}", ecmFile.getFileName(), authentication.getName());
        getFileEventPublisher().publishFileMovedInRecycleBinEvent(ecmFile, authentication, ipAddress, true);
        return recycleBinItem;
    }

    private EcmFile moveToCMISFolder(EcmFile ecmFile, Long sourceContainerObjectId, String sourceContainerObjectType, Long destinationFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
       return getEcmFileService().moveFile(ecmFile.getId(), sourceContainerObjectId,
                sourceContainerObjectType, destinationFolderId);
    }

    @Override
    public RecycleBinDTO findRecycleBinItems(Authentication authentication, String sortBy, String sortDir, int pageNumber, int pageSize) throws MuleException, ParseException
    {
        List<RecycleBinItemDTO> recycleBinItemDTOS = new ArrayList<>();
        String query = "object_type_s:" + RecycleBinConstants.OBJECT_TYPE_ITEM;
        String sortParam = sortBy + " " + sortDir;
        String results = getSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, pageNumber, pageSize, sortParam);
        for (int i = 0; i < getSearchResults().getDocuments(results).length(); i++)
        {
            JSONObject recycleBinItem = getSearchResults().getDocuments(results).getJSONObject(i);
            RecycleBinItemDTO recycleBinItemDTO = new RecycleBinItemDTO();

            recycleBinItemDTO.setFileName(recycleBinItem.optString("object_name_s"));
            recycleBinItemDTO.setDateModified(generateDate(recycleBinItem.optString("modified_date_tdt")));
            recycleBinItemDTO.setFileSizeBytes(recycleBinItem.optLong("object_item_size_l"));
            recycleBinItemDTO.setFileActiveVersionNameExtension(recycleBinItem.optString("item_type_s"));
            recycleBinItemDTO.setFileId( recycleBinItem.optLong("object_id_i"));
            recycleBinItemDTO.setContainerId(recycleBinItem.optLong("object_container_object_id_i"));
            recycleBinItemDTO.setContainerObjectTitle(recycleBinItem.optString("object_container_object_title_s"));
            recycleBinItemDTO.setContainerObjectType(recycleBinItem.optString("object_container_object_type_s"));
            recycleBinItemDTO.setRecycleBinItemId(recycleBinItem.optLong("item_id_i"));

            recycleBinItemDTOS.add(recycleBinItemDTO);
        }
        RecycleBinDTO recycleBinDTO = new RecycleBinDTO();
        recycleBinDTO.setNumRecycleBinItems(getSearchResults().getNumFound(results));
        recycleBinDTO.setRecycleBinItems(recycleBinItemDTOS);
        log.info("All Recycle Bin items retrieved by {}, are received from Solr", authentication.getName());
        return recycleBinDTO;
    }

    private Date generateDate (String date) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat(DateFormats.DEFAULT_DATE_FORMAT);
        return format.parse (date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RecycleBinItemDTO> restoreItemsFromRecycleBin(List<RecycleBinItemDTO> itemsToBeRestored, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        for (RecycleBinItemDTO fileFromTrash : itemsToBeRestored)
        {
            EcmFile ecmFile = getEcmFileDao().find(fileFromTrash.getFileId());
            RecycleBinItem recycleBinItem = getRecycleBinItemDao().find(fileFromTrash.getRecycleBinItemId());
            removeItemFromRecycleBin(recycleBinItem.getId());
            AcmContainer destinationContainer = getFolderService().findContainerByFolderIdTransactionIndependent(recycleBinItem.getSourceFolderId());
            moveToCMISFolder(ecmFile, destinationContainer.getContainerObjectId(), destinationContainer.getContainerObjectType(), recycleBinItem.getSourceFolderId());
            log.info("Item {} from Recycle Bin successfully restored, by user {}", fileFromTrash.getFileId(), authentication.getName());
        }

        return itemsToBeRestored;
    }

    @Override
    @Transactional
    public AcmContainer getOrCreateContainerForRecycleBin(String objectType, String cmisRepositoryId, Authentication authentication) throws AcmCreateObjectFailedException
    {
        AcmContainer recycleBinContainer = getRecycleBinItemDao().getContainerForRecycleBin(objectType, cmisRepositoryId);
        log.info("Container for the Recycle Bin successfully retrieved (created if don't exist), by user {}", authentication.getName());
        if (recycleBinContainer == null)
        {
            log.debug("Recycle Bin container for cmis repository {} is not found, by user {}", cmisRepositoryId, authentication.getName());
            return getEcmFileService().createContainerFolder(objectType, 0L, EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        }
        else {
            log.debug("Recycle Bin container {} for cmis repository {} is successfully created, by user {}", recycleBinContainer.getContainerObjectId(), cmisRepositoryId, authentication.getName());
            return recycleBinContainer;
        }
    }

    @Override
    public void removeItemFromRecycleBin(Long fileId) {
        getRecycleBinItemDao().removeItemFromRecycleBin(fileId);
    }

    public RecycleBinItemDao getRecycleBinItemDao()
    {
        return recycleBinItemDao;
    }

    public void setRecycleBinItemDao(RecycleBinItemDao recycleBinItemDao)
    {
        this.recycleBinItemDao = recycleBinItemDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }


    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ExecuteSolrQuery getSolrQuery()
    {
        return solrQuery;
    }

    public void setSolrQuery(ExecuteSolrQuery solrQuery)
    {
        this.solrQuery = solrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }
}
