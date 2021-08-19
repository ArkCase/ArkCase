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
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinDTO;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
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
    private transient final Logger log = LogManager.getLogger(getClass());
    private RecycleBinItemDao recycleBinItemDao;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;
    private ExecuteSolrQuery solrQuery;
    private SearchResults searchResults;
    private AcmFolderService folderService;
    private FileEventPublisher fileEventPublisher;
    private FolderEventPublisher folderEventPublisher;

    @Override
    public RecycleBinItem save(RecycleBinItem recycleBinItem)
    {
        return recycleBinItemDao.save(recycleBinItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecycleBinItem putFileIntoRecycleBin(EcmFile ecmFile, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException {
        AcmContainer destinationContainer = getOrCreateContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE,
                ecmFile.getCmisRepositoryId());
        AcmContainer sourceContainer = getFolderService().findContainerByFolderIdTransactionIndependent(ecmFile.getFolder().getId());

        RecycleBinItem recycleBinItem = new RecycleBinItem(ecmFile.getId(), ecmFile.getObjectType(),
                ecmFile.getFolder().getId(), ecmFile.getCmisRepositoryId(), sourceContainer.getId());
        getRecycleBinItemDao().save(recycleBinItem);
        moveToCMISFolder(ecmFile, destinationContainer.getContainerObjectId(), destinationContainer.getContainerObjectType(),
                destinationContainer.getFolder().getId());

        log.info("File {} successfully moved in Recycle Bin, by user {}", ecmFile.getFileName(), authentication.getName());
        return recycleBinItem;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecycleBinItem putFolderIntoRecycleBin(AcmFolder folder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException
    {
        AcmFolder recycleBinFolder = getOrCreateContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE,
                folder.getCmisRepositoryId()).getFolder();

        AcmContainer sourceContainer = getFolderService().findContainerByFolderIdTransactionIndependent(folder.getId());

        RecycleBinItem recycleBinItem = new RecycleBinItem(folder.getId(), folder.getObjectType(),
                folder.getParentFolder().getId(), folder.getCmisRepositoryId(), sourceContainer.getId());
        getRecycleBinItemDao().save(recycleBinItem);

        getFolderService().moveFolder(folder, recycleBinFolder);
        return recycleBinItem;
    }

    private EcmFile moveToCMISFolder(EcmFile ecmFile, Long sourceContainerObjectId, String sourceContainerObjectType,
            Long destinationFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException {
        return getEcmFileService().moveFile(ecmFile.getId(), sourceContainerObjectId,
                sourceContainerObjectType, destinationFolderId);
    }

    @Override
    public RecycleBinDTO findRecycleBinItems(Authentication authentication, String sortBy, String sortDir, int pageNumber, int pageSize)
            throws ParseException, SolrException
    {
        List<RecycleBinItemDTO> recycleBinItemDTOS = new ArrayList<>();
        String query = "object_type_s:" + RecycleBinConstants.OBJECT_TYPE_ITEM;
        String sortParam = sortBy + " " + sortDir;
        String results = getSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, pageNumber, pageSize,
                sortParam);
        for (int i = 0; i < getSearchResults().getDocuments(results).length(); i++)
        {
            JSONObject recycleBinItem = getSearchResults().getDocuments(results).getJSONObject(i);
            RecycleBinItemDTO recycleBinItemDTO = new RecycleBinItemDTO();

            recycleBinItemDTO.setObjectName(recycleBinItem.optString("object_name_s"));
            recycleBinItemDTO.setObjectType(recycleBinItem.optString("object_item_type_s"));
            recycleBinItemDTO.setDateModified(generateDate(recycleBinItem.optString("modified_date_tdt")));
            recycleBinItemDTO.setFileSizeBytes(recycleBinItem.optLong("object_item_size_l"));
            recycleBinItemDTO.setFileActiveVersionNameExtension(recycleBinItem.optString("item_type_s"));
            recycleBinItemDTO.setObjectId(recycleBinItem.optLong("object_id_i"));
            recycleBinItemDTO.setContainerId(recycleBinItem.optLong("object_container_object_id_i"));
            recycleBinItemDTO.setContainerObjectTitle(recycleBinItem.optString("object_container_object_title_s"));
            recycleBinItemDTO.setContainerObjectType(recycleBinItem.optString("object_container_object_type_s"));
            recycleBinItemDTO.setId(recycleBinItem.optLong("item_id_i"));
            if (recycleBinItemDTO.getObjectType().equals(AcmFolderConstants.OBJECT_FOLDER_TYPE))
            {
                recycleBinItemDTO.setFileSizeBytes(null);
            }

            recycleBinItemDTOS.add(recycleBinItemDTO);
        }
        RecycleBinDTO recycleBinDTO = new RecycleBinDTO();
        recycleBinDTO.setNumRecycleBinItems(getSearchResults().getNumFound(results));
        recycleBinDTO.setRecycleBinItems(recycleBinItemDTOS);
        log.info("All Recycle Bin items retrieved by {}, are received from Solr", authentication.getName());
        return recycleBinDTO;
    }

    private Date generateDate(String date) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat(DateFormats.DEFAULT_DATE_FORMAT);
        return format.parse(date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RecycleBinItemDTO> restoreItemsFromRecycleBin(List<RecycleBinItemDTO> itemsToBeRestored, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException, LinkAlreadyExistException {
        for (RecycleBinItemDTO fileFromTrash : itemsToBeRestored)
        {
            RecycleBinItem recycleBinItem = getRecycleBinItemDao().find(fileFromTrash.getId());
            AcmContainer destinationContainer = getFolderService()
                    .findContainerByFolderIdTransactionIndependent(recycleBinItem.getSourceFolderId());

            if (recycleBinItem.getSourceObjectType().equals(AcmFolderConstants.OBJECT_FOLDER_TYPE))
            {
                AcmFolder acmFolder = getFolderService().findById(fileFromTrash.getObjectId());
                AcmFolder destinationFolder = getFolderService().findById(recycleBinItem.getSourceFolderId());
                getFolderService().moveFolder(acmFolder, destinationFolder);
                removeItemFromRecycleBin(recycleBinItem.getId());
            }
            else
            {
                EcmFile ecmFile = getEcmFileDao().find(fileFromTrash.getObjectId());
                moveToCMISFolder(ecmFile, destinationContainer.getContainerObjectId(), destinationContainer.getContainerObjectType(),
                        recycleBinItem.getSourceFolderId());
                removeItemFromRecycleBin(recycleBinItem.getId());
                getEcmFileService().checkAndSetDuplicatesByHash(ecmFile);
            }
            log.info("Item {} from Recycle Bin successfully restored, by user {}", fileFromTrash.getObjectId(), authentication.getName());
        }
        return itemsToBeRestored;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecycleBinItemPermanently(RecycleBinItemDTO itemToDelete, Authentication authentication, String ipAddress)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        Long itemId = itemToDelete.getObjectId();
        Long recycleBinId = itemToDelete.getId();

        if (itemToDelete.getObjectType().equals(AcmFolderConstants.OBJECT_FOLDER_TYPE))
        {
            AcmFolder source = getFolderService().findById(itemId);

            getFolderService().deleteFolderTreeSafe(itemId, authentication);
            removeItemFromRecycleBin(recycleBinId);

            log.info("Folder with id: [{}] permanently deleted", itemId);
            getFolderEventPublisher().publishFolderDeletedEvent(source, authentication, ipAddress, true);

        }
        else
        {
            EcmFile source = getEcmFileService().findById(itemId);

            getEcmFileService().deleteFilePermanently(itemId, recycleBinId);

            log.info("File with id: {} permanently deleted", itemId);
            getFileEventPublisher().publishFileDeletedEvent(source, authentication, ipAddress, true);

        }
    }

    @Override
    @Transactional
    public AcmContainer getOrCreateContainerForRecycleBin(String objectType, String cmisRepositoryId)
            throws AcmCreateObjectFailedException
    {
        AcmContainer recycleBinContainer = getRecycleBinItemDao().getContainerForRecycleBin(objectType, cmisRepositoryId);
        if (recycleBinContainer == null)
        {
            log.debug("Recycle Bin container for cmis repository {} is not found, and will be created", cmisRepositoryId);
            String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            long objectId = Long.parseLong(currentDate);
            return getEcmFileService().createContainerFolder(objectType, objectId, cmisRepositoryId);
        }
        else
        {
            return recycleBinContainer;
        }
    }

    @Override
    @Transactional
    public void removeItemFromRecycleBin(Long fileId)
    {
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

    public FolderEventPublisher getFolderEventPublisher()
    {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher)
    {
        this.folderEventPublisher = folderEventPublisher;
    }

}
