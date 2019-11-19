package com.armedia.acm.plugins.ecm.service.impl;

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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmProgressEvent;
import com.armedia.acm.data.AcmProgressIndicator;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.DeleteFolderInfo;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.service.objectlock.annotation.AcmAcquireAndReleaseObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */

public class AcmFolderServiceImpl implements AcmFolderService, ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private AcmFolderDao folderDao;
    private AcmContainerDao containerDao;
    private EcmFileDao fileDao;
    private MuleContextManager muleContextManager;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;
    private CmisConfigUtils cmisConfigUtils;
    private EcmFileParticipantService fileParticipantService;
    private EcmFileConfig ecmFileConfig;
    private MessageChannel genericMessagesChannel;
    private AcmObjectLockService objectLockService;
    private CamelContextManager camelContextManager;

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder addNewFolder(Long parentFolderId, String newFolderName)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        AcmFolder folder = getFolderDao().find(parentFolderId);

        return addNewFolder(folder, newFolderName);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder addNewFolder(Long parentFolderId, String newFolderName, Long parentId, String parentType)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        AcmFolder folder = getFolderDao().find(parentFolderId);

        return addNewFolder(folder, newFolderName);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder addNewFolder(AcmFolder parentFolder, String newFolderName)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        if (parentFolder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Folder not found", null);
        }
        String safeName = getFolderAndFilesUtils().buildSafeFolderName(newFolderName);
        String uniqueFolderName = getFolderAndFilesUtils().createUniqueFolderName(safeName);
        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.PARENT_FOLDER_ID, parentFolder.getCmisFolderId());
        properties.put(AcmFolderConstants.NEW_FOLDER_NAME, uniqueFolderName);

        String cmisRepositoryId = getCmisRepositoryId(parentFolder);

        properties.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        String cmisFolderId = null;
        try
        {

            cmisFolderId = createNewFolderAndReturnCmisID(parentFolder, properties);
            log.debug("Folder with name: {}  exists inside the folder: {}", newFolderName, parentFolder.getName());
            return prepareFolder(parentFolder, cmisFolderId, newFolderName, cmisRepositoryId);
        }
        catch (NoResultException e)
        {
            AcmFolder newFolder = new AcmFolder();
            if (cmisFolderId != null)
            {
                newFolder.setCmisRepositoryId(parentFolder.getCmisRepositoryId());
                newFolder.setCmisFolderId(cmisFolderId);
            }
            else
            {

                log.error("Folder {} not added successfully under as a child to folder {} with error mesage: {}", newFolderName,
                        parentFolder.getName(), e.getMessage(), e);
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        parentFolder.getId(), "Folder was no added under " + parentFolder.getName() + " successfully", null);
            }
            newFolder.setName(newFolderName);
            newFolder.setParentFolder(parentFolder);

            AcmFolder result = getFolderDao().save(newFolder);

            getFileParticipantService().setFolderParticipantsFromParentFolder(result);
            result = getFolderDao().save(result);

            return result;
        }
        catch (PersistenceException | AcmFolderException e)
        {
            log.error("Folder not added under {} successfully {}", parentFolder.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    parentFolder.getId(), "Folder was no added under " + parentFolder.getName() + " successfully", e);
        }
    }

    @Override
    public AcmFolder addNewFolderByPath(String targetObjectType, Long targetObjectId, String newPath)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        log.info("New folder path: {}", newPath);

        AcmContainer container = getContainerDao().findFolderByObjectTypeAndId(targetObjectType, targetObjectId);
        if (container == null)
        {
            throw new AcmObjectNotFoundException(targetObjectType, targetObjectId, "Container object not found", null);
        }

        // the new path could start with /, which will make
        // the first element in a split return null. It could end with / too.
        if (newPath.startsWith("/"))
        {
            newPath = newPath.substring(1);
        }
        if (newPath.endsWith("/"))
        {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        log.info("Trimmed new path: {}", newPath);

        String[] targetPathComponents = newPath.split("/");
        AcmFolder parent = container.getFolder();
        if (newPath != null && newPath.trim().length() < 1)
        {
            return parent;
        }
        for (String targetPathComponent : targetPathComponents)
        {
            log.info("Checking for folder named {}", targetPathComponent);
            try
            {
                AcmFolder folder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                parent = folder;
            }
            catch (NoResultException nre)
            {
                // theoretically the folder could be created between when we check for it, and when we insert it
                // so we'll catch the key violation here
                try
                {
                    AcmFolder newFolder = addNewFolder(parent, targetPathComponent);
                    parent = newFolder;
                }
                catch (Exception e)
                {
                    AcmFolder foundFolder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                    parent = foundFolder;
                }

            }
        }

        // by now "parent" should be the last folder found/created
        return parent;
    }

    @Override
    public String findFolderPath(String cmisFolderObjectId) throws MuleException, AcmUserActionFailedException
    {

        Map<String, Object> findFolderProperties = new HashMap<>();
        findFolderProperties.put("parentFolderId", cmisFolderObjectId);

        AcmFolder acmFolder = folderDao.findByCmisFolderId(cmisFolderObjectId);
        if (acmFolder != null && acmFolder.getCmisRepositoryId() != null)
        {
            findFolderProperties.put(AcmFolderConstants.CONFIGURATION_REFERENCE,
                    cmisConfigUtils.getCmisConfiguration(acmFolder.getCmisRepositoryId()));
        }
        else
        {
            String defaultCmisId = ecmFileConfig.getDefaultCmisId();
            findFolderProperties.put(AcmFolderConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(defaultCmisId));
        }
        MuleMessage findFolderMessage = getMuleContextManager().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, null,
                findFolderProperties);
        if (findFolderMessage.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY))
        {
            MuleException muleException = findFolderMessage.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);

            log.error("Folder can not be found {}", muleException.getMessage(), muleException);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_CREATE_FOLDER_BY_PATH,
                    AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Folder not found", muleException);
        }

        CmisObject containerFolderObject = findFolderMessage.getPayload(CmisObject.class);
        Folder containerFolder = (Folder) containerFolderObject;

        return containerFolder.getPath();
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
    public AcmFolder renameFolder(Long folderId, String newFolderName)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {

        AcmFolder folder = getFolderDao().find(folderId);
        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        }
        AcmFolder renamedFolder;
        try
        {
            folder.setName(newFolderName);
            renamedFolder = getFolderDao().save(folder);
            log.debug("Folder name is changed to {}", newFolderName);
            return renamedFolder;
        }
        catch (Exception e)
        {
            Throwable t = ExceptionUtils.getRootCause(e);
            if (t instanceof SQLIntegrityConstraintViolationException)
            {
                log.error("Folder {} was not renamed successfully", folder.getName(), e.getMessage(), e);
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_RENAME_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        folder.getId(), "Folder " + folder.getName() + " was not renamed successfully", e);
            }
            else
            {
                log.error("Folder with name {} already exists {}", newFolderName, e.getMessage());
                throw new AcmFolderException(e);
            }
        }
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public List<AcmObject> getFolderChildren(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        List<AcmObject> objectList = new ArrayList<>();

        List<AcmFolder> subfolders = getFolderDao().findSubFolders(folderId);
        if (subfolders != null && !subfolders.isEmpty())
        {
            objectList.addAll(subfolders);
        }

        List<EcmFile> files = getFileDao().findByFolderId(folderId);
        if (files != null && !files.isEmpty())
        {
            objectList.addAll(files);
        }

        return objectList;
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException
    {

        AcmFolder movedFolder;

        if (folderForMoving == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Folder that need to be moved not found",
                    null);
        }
        if (dstFolder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Destination folder not found", null);
        }
        if (folderForMoving.getParentFolder() == null)
        {
            log.info("The folder: {} is a root folder, can not be moved!", folderForMoving.getName());
            throw new AcmFolderException("The folder: " + folderForMoving.getName() + " is a root folder, can not be moved!");
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folderForMoving.getCmisFolderId());
        properties.put(AcmFolderConstants.DESTINATION_FOLDER_ID, dstFolder.getCmisFolderId());

        String cmisRepositoryId = getCmisRepositoryId(folderForMoving);

        properties.put(EcmFileConstants.CMIS_REPOSITORY_ID, "camelAlfresco");
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {
            FileableCmisObject result = (FileableCmisObject) getCamelContextManager().send(ArkCaseCMISActions.MOVE_FOLDER, properties);
            String newFolderId = result.getId();

            folderForMoving.setCmisRepositoryId(cmisRepositoryId);
            folderForMoving.setCmisFolderId(newFolderId);
            folderForMoving.setParentFolder(dstFolder);

            movedFolder = getFolderDao().save(folderForMoving);
            getFileParticipantService().setFolderParticipantsFromParentFolder(movedFolder);
            movedFolder = getFolderDao().save(movedFolder);
        }
        catch (PersistenceException | ArkCaseFileRepositoryException e)
        {
            log.error("Folder {} not moved successfully {}", folderForMoving.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderForMoving.getId(), "Folder was not moved under " + dstFolder.getName() + " successfully", e);
        }
        return movedFolder;
    }

    @Override
    public AcmFolder moveFolderInArkcase(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmUserActionFailedException, AcmFolderException
    {

        AcmFolder movedFolder;

        if (folderForMoving.getParentFolder() == null)
        {
            log.info("The folder: {} is a root folder, can not be moved!", folderForMoving.getName());
            throw new AcmFolderException("The folder: " + folderForMoving.getName() + " is a root folder, can not be moved!");
        }

        try
        {
            folderForMoving.setParentFolder(dstFolder);

            movedFolder = getFolderDao().save(folderForMoving);
            getFileParticipantService().setFolderParticipantsFromParentFolder(movedFolder);
            movedFolder = getFolderDao().save(movedFolder);
        }
        catch (PersistenceException e)
        {
            log.error("Folder {} not moved successfully {}", folderForMoving.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderForMoving.getId(), "Folder was not moved under " + dstFolder.getName() + " successfully", e);
        }
        return movedFolder;
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder moveRootFolder(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException
    {

        AcmFolder movedFolder;

        if (folderForMoving == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Folder that need to be moved not found",
                    null);
        }
        if (dstFolder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Destination folder not found", null);
        }

        if (folderForMoving.getParentFolder() != null)
        {
            log.info("The folder: {} is not root folder, can not be moved!", folderForMoving.getName());
            throw new AcmFolderException("The folder: " + folderForMoving.getName() + " is not root folder, can not be moved!");
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folderForMoving.getCmisFolderId());
        properties.put(AcmFolderConstants.DESTINATION_FOLDER_ID, dstFolder.getCmisFolderId());

        String cmisRepositoryId = getCmisRepositoryId(folderForMoving);

        properties.put(EcmFileConstants.CMIS_REPOSITORY_ID, "camelAlfresco");
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {
            FileableCmisObject result = (FileableCmisObject) getCamelContextManager().send(ArkCaseCMISActions.MOVE_FOLDER, properties);

            String newFolderId = result.getId();

            folderForMoving.setCmisRepositoryId(cmisRepositoryId);
            folderForMoving.setCmisFolderId(newFolderId);
            folderForMoving.setParentFolder(dstFolder);
            movedFolder = getFolderDao().save(folderForMoving);
            getFileParticipantService().setFolderParticipantsFromParentFolder(movedFolder);
            movedFolder = getFolderDao().save(movedFolder);
        }
        catch (PersistenceException | ArkCaseFileRepositoryException e)
        {
            log.error("Folder {} not moved successfully {}", folderForMoving.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderForMoving.getId(), "Folder was not moved under " + dstFolder.getName() + " successfully", e);
        }
        return movedFolder;
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId, String targetObjectType)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException
    {

        AcmFolder toBeCopied = getFolderDao().find(folderToBeCopiedId);
        AcmFolder dstFolder = getFolderDao().find(copyDstFolderId);

        return copyFolder(toBeCopied, dstFolder, targetObjectId, targetObjectType);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public AcmFolder copyFolder(AcmFolder toBeCopied, AcmFolder dstFolder, Long targetObjectId, String targetObjectType)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException
    {

        if (toBeCopied == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Folder that need to be copied not found",
                    null);
        }
        if (dstFolder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, null, "Destination folder not found", null);
        }

        if (toBeCopied.getParentFolder() == null)
        {
            log.info("The folder: {} is a root folder, can not be moved!", toBeCopied.getName());
            throw new AcmFolderException("The folder: " + toBeCopied.getName() + " is a root folder, can not be moved!");
        }

        Map<String, Object> toBeCopiedFolderProperties = new HashMap<>();
        toBeCopiedFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, toBeCopied.getCmisFolderId());

        String cmisRepositoryId = getCmisRepositoryId(toBeCopied);

        toBeCopiedFolderProperties.put(AcmFolderConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(cmisRepositoryId));

        Map<String, Object> parentFolderProperties = new HashMap<>();
        parentFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, dstFolder.getCmisFolderId());
        parentFolderProperties.put(AcmFolderConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(cmisRepositoryId));

        Folder folderToBeCopied;
        Folder parentFolder;
        AcmFolder copiedFolder = null;
        boolean isFirstFolderFetched = false;
        try
        {
            MuleMessage message = getMuleContextManager().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, toBeCopied,
                    toBeCopiedFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY))
            {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                log.error("Folder not fetched successfully {}", muleException.getMessage(), muleException);
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        toBeCopied.getId(), "Folder  " + toBeCopied.getName() + "was not fetched successfully", muleException);
            }
            CmisObject cmisObjectNewFolder = message.getPayload(CmisObject.class);
            folderToBeCopied = (Folder) cmisObjectNewFolder;

            isFirstFolderFetched = true;

            MuleMessage msg = getMuleContextManager().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, dstFolder, parentFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY))
            {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                log.error("Folder not fetched successfully {}", muleException.getMessage(), muleException);
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        dstFolder.getId(), "Folder  " + dstFolder.getName() + "was not fetched successfully", muleException);
            }
            CmisObject cmisObjectParentFolder = msg.getPayload(CmisObject.class);
            parentFolder = (Folder) cmisObjectParentFolder;
        }
        catch (MuleException e)
        {
            if (isFirstFolderFetched)
            {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        dstFolder.getId(), "Folder  " + dstFolder.getName() + "was not fetched successfully", e);
            }
            else
            {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        toBeCopied.getId(), "Folder  " + toBeCopied.getName() + "was not fetched successfully", e);
            }
        }
        copiedFolder = copyDir(parentFolder, folderToBeCopied, targetObjectId, targetObjectType, cmisRepositoryId);
        return copiedFolder;
    }

    private AcmFolder copyDir(Folder parentFolder, Folder toBeCopiedFolder, Long targetObjectId, String targetObjectType,
            String cmisRepositoryId) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        Map<String, Object> newFolderProperties = new HashMap<>();

        // TODO: We can remove this when mule will be completely removed from all Folder flows.
        // Camel getId() method return only id without workspace and spacestore. alfCmis:nodeRef returns all this
        // values, that means that we should use getPropertyValue("alfcmis:nodeRef") instead of getId().
        String folderId = StringUtils.isNotEmpty(toBeCopiedFolder.getPropertyValue("alfcmis:nodeRef"))
                ? toBeCopiedFolder.getPropertyValue("alfcmis:nodeRef")
                : toBeCopiedFolder.getId();

        AcmFolder toCopyFolder = getFolderDao().findByCmisFolderId(folderId);

        String uniqueFolderName = getFolderAndFilesUtils().createUniqueFolderName(toCopyFolder.getName());

        // TODO: We can remove this when mule will be completely removed from all Folder flows.
        String parentFolderId = StringUtils.isNotEmpty(parentFolder.getPropertyValue("alfcmis:nodeRef"))
                ? parentFolder.getPropertyValue("alfcmis:nodeRef")
                : parentFolder.getId();
        newFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, parentFolderId);
        newFolderProperties.put(AcmFolderConstants.NEW_FOLDER_NAME, uniqueFolderName);
        newFolderProperties.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        newFolderProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        Folder newFolder;
        AcmFolder copiedFolder = null;
        AcmFolder acmNewFolder = new AcmFolder();
        try
        {
            newFolder = (Folder) getCamelContextManager().send(ArkCaseCMISActions.CREATE_FOLDER, newFolderProperties);

            acmNewFolder.setCmisRepositoryId(ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
            acmNewFolder.setCmisFolderId(newFolder.getPropertyValue("alfcmis:nodeRef"));
            AcmFolder pFolder = getFolderDao().findByCmisFolderId(parentFolderId);
            acmNewFolder.setParentFolder(pFolder);
            acmNewFolder.setName(toCopyFolder.getName());
            copiedFolder = getFolderDao().save(acmNewFolder);
            getFileParticipantService().setFolderParticipantsFromParentFolder(copiedFolder);
            copiedFolder = getFolderDao().save(copiedFolder);
        }
        catch (PersistenceException | ArkCaseFileRepositoryException e)
        {
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    null, "Folder was not created under " + toBeCopiedFolder.getName() + " successfully", e);
        }
        copyChildren(newFolder, toBeCopiedFolder, targetObjectId, targetObjectType, cmisRepositoryId);

        return copiedFolder;
    }

    private void copyChildren(Folder parentFolder, Folder toCopyFolder, Long targetObjectId, String targetObjectType,
            String cmisRepositoryId) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        ItemIterable<CmisObject> immediateChildren = toCopyFolder.getChildren();
        for (CmisObject child : immediateChildren)
        {
            if (child instanceof Document)
            {
                AcmFolder acmParent = getFolderDao().findByCmisFolderId(toCopyFolder.getId());
                AcmFolder dstFolder = getFolderDao().findByCmisFolderId(parentFolder.getId());
                EcmFile ecmFile = null;
                try
                {
                    ecmFile = getFileDao().findByCmisFileIdAndFolderId(child.getId(), acmParent.getId());
                }
                catch (NoResultException e)
                {
                    log.debug("File with cmisId: {} not found in the DB, but returned from content repository!", child.getId(), e);
                    continue;
                }
                if (ecmFile != null)
                {
                    try
                    {
                        getFileService().copyFile(ecmFile.getFileId(), targetObjectId, targetObjectType, dstFolder.getId());
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        throw e;
                    }
                }
            }
            else if (child instanceof Folder)
            {
                copyDir(parentFolder, (Folder) child, targetObjectId, targetObjectType, cmisRepositoryId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
    public void deleteFolderTreeSafe(Long folderId, Authentication authentication)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        AcmFolder folder = getFolderDao().find(folderId);

        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        }

        log.info("Deleting folder tree");
        deleteFolderTree(folderId, authentication);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
    public void deleteFolderTree(Long folderId, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        AcmFolder folder = getFolderDao().find(folderId);

        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        }

        deleteFolderContent(folder, authentication.getName());

        deleteAlfrescoFolderTree(folder);
    }

    public void deleteAlfrescoFolderTree(AcmFolder folder) throws AcmUserActionFailedException
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folder.getCmisFolderId());

        String cmisRepositoryId = getCmisRepositoryId(folder);
        properties.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        try
        {
            getCamelContextManager().send(ArkCaseCMISActions.DELETE_FOLDER, properties);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Folder {} not deleted successfully {}", folder.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_DELETE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folder.getId(), "Folder was not deleted successfully", e);
        }
    }

    @Override
    @Transactional
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "CONTAINER", lockType = "DELETE")
    public void deleteContainerSafe(AcmContainer container, Authentication authentication) throws AcmUserActionFailedException
    {
        log.info("Deleting container and it's content");
        deleteContainer(container.getId(), authentication);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "CONTAINER", lockType = "DELETE")
    public void deleteContainer(Long containerId, Authentication authentication) throws AcmUserActionFailedException
    {
        AcmContainer container = containerDao.find(containerId);
        AcmFolder rootFolder = container.getFolder();
        deleteContainerAndContent(container, authentication.getName());
        deleteAlfrescoFolderTree(rootFolder);
    }

    private void deleteFilesWithProgress(Set<EcmFile> files, AcmProgressIndicator acmProgressIndicator, int progressCounter, int total)
    {
        for (EcmFile file : files)
        {
            log.info("Delete file with id: [{}]", file.getId());
            fileDao.deleteFile(file.getId());

            acmProgressIndicator.setProgress(calculateProgress(progressCounter, total));
            applicationEventPublisher.publishEvent(new AcmProgressEvent(acmProgressIndicator));
            progressCounter++;
        }
    }

    private void deleteFoldersWithProgress(Set<AcmFolder> folders, AcmProgressIndicator acmProgressIndicator, int progressCounter,
            int total)
    {
        for (AcmFolder subFolder : folders)
        {
            log.info("Delete folder with id: [{}]", subFolder.getId());
            folderDao.deleteFolder(subFolder.getId());

            acmProgressIndicator.setProgress(calculateProgress(progressCounter, total));
            applicationEventPublisher.publishEvent(new AcmProgressEvent(acmProgressIndicator));
            progressCounter++;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "CONTAINER", lockType = "DELETE")
    public void deleteContainerAndContent(AcmContainer container, String user)
    {
        AcmProgressIndicator acmProgressIndicator = new AcmProgressIndicator();
        acmProgressIndicator.setObjectId(container.getId());
        acmProgressIndicator.setObjectType(AcmFolderConstants.CONTAINER_OBJECT_TYPE);
        acmProgressIndicator.setUser(user);

        AcmFolder rootFolder = container.getFolder();
        Set<EcmFile> childrenFiles = new HashSet<>();
        Set<AcmFolder> childrenFolders = new HashSet<>();

        findFolderChildren(rootFolder, childrenFiles, childrenFolders);
        int totalEntriesCount = childrenFiles.size() + childrenFolders.size() + 1;
        int counter = 1;

        // delete everything from the root folder first
        // EcmFile has foreign key to the container
        deleteFilesWithProgress(childrenFiles, acmProgressIndicator, counter, totalEntriesCount);

        counter += childrenFiles.size();

        // sort by latest entry to avoid constraint violation
        childrenFolders = childrenFolders.stream().sorted(Comparator.comparing(AcmFolder::getId).reversed()).collect(Collectors.toSet());

        deleteFoldersWithProgress(childrenFolders, acmProgressIndicator, counter, totalEntriesCount);

        // somehow deleting folders and deleting container below are not in the same persistence context
        // must flush here, otherwise deleting container fails on reference (any sub-folders) to the ROOT folder
        // of the container
        getFolderDao().getEm().flush();

        counter += childrenFolders.size();

        // deleting the container will delete the ROOT folder
        log.info("Delete container with id: [{}]", container.getId());
        containerDao.delete(container.getId());
        acmProgressIndicator.setProgress(calculateProgress(counter, totalEntriesCount)); // 100%
        applicationEventPublisher.publishEvent(new AcmProgressEvent(acmProgressIndicator));
    }

    /**
     * Delete folder content in arkcase.
     *
     * @param folder
     * @param user
     */
    @Override
    public void deleteFolderContent(AcmFolder folder, String user)
    {
        AcmProgressIndicator acmProgressIndicator = new AcmProgressIndicator();
        acmProgressIndicator.setObjectId(folder.getId());
        acmProgressIndicator.setObjectType(AcmFolderConstants.OBJECT_FOLDER_TYPE);
        acmProgressIndicator.setUser(user);

        Set<EcmFile> childrenFiles = new HashSet<>();
        Set<AcmFolder> childrenFolders = new HashSet<>();

        if (folder == null)
        {
            return;
        }
        findFolderChildren(folder, childrenFiles, childrenFolders);

        int totalEntriesCount = childrenFiles.size() + childrenFolders.size() + 1;
        int counter = 1;

        deleteFilesWithProgress(childrenFiles, acmProgressIndicator, counter, totalEntriesCount);

        counter += childrenFiles.size();

        // sort by latest entry to avoid constraint violation
        childrenFolders = childrenFolders.stream().sorted(Comparator.comparing(AcmFolder::getId).reversed()).collect(Collectors.toSet());

        counter += childrenFolders.size();

        deleteFoldersWithProgress(childrenFolders, acmProgressIndicator, counter, totalEntriesCount);

        log.info("Delete root folder with id: [{}]", folder.getId());
        folderDao.deleteFolder(folder.getId());
        acmProgressIndicator.setProgress(calculateProgress(counter, totalEntriesCount)); // 100%
        applicationEventPublisher.publishEvent(new AcmProgressEvent(acmProgressIndicator));
    }

    private int calculateProgress(int current, int total)
    {
        return (int) (current * 1.0 / total * 100);
    }

    @Override
    public String getCmisRepositoryId(AcmFolder folder)
    {
        String cmisRepositoryId = folder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        return cmisRepositoryId;
    }

    @Override
    public DeleteFolderInfo getFolderToDeleteInfo(Long folderId) throws AcmObjectNotFoundException
    {
        AcmFolder folder = getFolderDao().find(folderId);

        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        }

        Set<AcmFolder> subFolderList = new HashSet<>();
        Set<EcmFile> filesList = new HashSet<>();
        findFolderChildren(folder, filesList, subFolderList);

        DeleteFolderInfo deleteFolderInfo = new DeleteFolderInfo();
        deleteFolderInfo.setFilesToDeleteNum(filesList.size());
        deleteFolderInfo.setFoldersToDeleteNum(subFolderList.size());
        return deleteFolderInfo;
    }

    void findFolderChildren(AcmFolder folder, Set<EcmFile> childFiles, Set<AcmFolder> childFolders)
    {
        if (folder == null)
        {
            return;
        }

        childFiles.addAll(fileDao.findByFolderId(folder.getId()));

        for (AcmFolder childFolder : folder.getChildrenFolders())
        {
            childFolders.add(childFolder);
            findFolderChildren(childFolder, childFiles, childFolders);
        }
    }

    /**
     * Makes call to content repository and returns children if there are any.
     * 
     * @param folderId
     *            - folder id on the folder which is checked for children
     * @return
     * @throws AcmUserActionFailedException
     * @throws AcmObjectNotFoundException
     */
    private ItemIterable<CmisObject> getFolderChildrenFromContentRepository(Long folderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        AcmFolder folder = getFolderDao().find(folderId);
        if (folder == null)
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        ItemIterable<CmisObject> cmisObjects;
        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folder.getCmisFolderId());

        String cmisRepositoryId = getCmisRepositoryId(folder);

        properties.put(AcmFolderConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(cmisRepositoryId));
        try
        {
            MuleMessage message = getMuleContextManager().send(AcmFolderConstants.MULE_ENDPOINT_LIST_FOLDER, folder, properties);
            if (message.getInboundPropertyNames().contains(AcmFolderConstants.LIST_FOLDER_EXCEPTION_INBOUND_PROPERTY))
            {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.LIST_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                log.error("Folders children can not be fetched: {}", muleException.getMessage(), muleException);
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        folder.getId(),
                        "Folder " + folder.getName() + "can not be listed successfully", muleException);
            }
            cmisObjects = message.getPayload(ItemIterable.class);
        }
        catch (PersistenceException | MuleException e)
        {
            log.error("Folder [{}] can not be listed: [{}]", folder.getName(), e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folder.getId(), "Folder " + folder.getName() + "can not be listed successfully", e);
        }
        return cmisObjects;
    }

    /**
     * If folder has children in the content repository then it creates them in arkcase.
     *
     * @param parentFolder
     * @param userId
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     */
    @Override
    public void recordMetadataOfExistingFolderChildren(AcmFolder parentFolder, String userId)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        ItemIterable<CmisObject> folderChildren = getFolderChildrenFromContentRepository(parentFolder.getId());
        for (CmisObject cmisObject : folderChildren)
        {
            if (cmisObject instanceof Document)
            {
                Document file = (Document) cmisObject;
                String cmisFileId = file.getVersionSeriesId();
                try
                {
                    if (getFileDao().findByCmisFileId(cmisFileId).size() == 0)
                    {
                        try
                        {
                            getFolderAndFilesUtils().uploadFile(cmisFileId, cmisObject.getName(), userId, parentFolder);
                        }
                        catch (AcmCreateObjectFailedException e)
                        {
                            log.debug("Could not add file with CMIS ID [{}] to ArkCase: [{}]", cmisFileId, e.getMessage(), e);
                        }
                    }
                }
                catch (NoResultException e)
                {
                    log.debug("File with cmisId: {} not found in the DB, but returned from content repository!",
                            cmisObject.getId(), e);
                    continue;
                }
            }
            else if (cmisObject instanceof Folder)
            {
                // create the folder and check for children, if any create them
                Folder folder = (Folder) cmisObject;
                String cmisFolderId = folder.getId();
                AcmFolder created = null;
                try
                {
                    created = createFolder(parentFolder, cmisFolderId, cmisObject.getName());
                }
                catch (AcmFolderException e)
                {
                    log.debug("Can't create new folder with id {}", cmisObject.getId());
                }
                recordMetadataOfExistingFolderChildren(created, userId);
            }
        }
    }

    private AcmFolder prepareFolder(AcmFolder folder, String cmisFolderId, String folderName, String cmisRepositoryId)
            throws AcmUserActionFailedException, PersistenceException, AcmFolderException
    {
        AcmFolder newFolder = new AcmFolder();
        if (cmisFolderId != null)
        {
            newFolder.setCmisRepositoryId(cmisRepositoryId);
            newFolder.setCmisFolderId(cmisFolderId);
        }
        else
        {
            log.error("Folder not added under {} successfully", folder.getName());
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folder.getId(), "Folder was no added under " + folder.getName() + " successfully", null);
        }
        newFolder.setName(folderName);
        newFolder.setParentFolder(folder);
        AcmFolder result;
        try
        {
            result = getFolderDao().save(newFolder);
            getFileParticipantService().setFolderParticipantsFromParentFolder(result);
            result = getFolderDao().save(result);
        }
        catch (Exception e)
        {
            Throwable t = ExceptionUtils.getRootCause(e);
            if (t instanceof SQLIntegrityConstraintViolationException)
            {
                log.debug("Folder with name {} already exists {}", folderName, e.getMessage());
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                        folder.getId(), "Folder with name " + folderName + " already exists", e);
            }
            else
            {
                log.error("Folder with name {}  already exists {}", folderName, e.getMessage());
                throw new AcmFolderException(e);
            }
        }
        return result;
    }

    @Override
    public AcmFolder createFolder(AcmFolder targetParentFolder, String cmisFolderId, String folderName)
            throws AcmFolderException, AcmUserActionFailedException
    {
        String cmisRepositoryId = getCmisRepositoryId(targetParentFolder);
        return prepareFolder(targetParentFolder, cmisFolderId, folderName, cmisRepositoryId);
    }

    private String createNewFolderAndReturnCmisID(AcmFolder folder, Map<String, Object> properties)
            throws AcmUserActionFailedException
    {
        Folder result;
        try
        {
            result = (Folder) getCamelContextManager().send(ArkCaseCMISActions.CREATE_FOLDER, properties);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Folder not added successfully {}", e.getMessage(), e);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folder.getId(), "Folder was not created under " + folder.getName() + " successfully", e);
        }

        return result.getPropertyValue("alfcmis:nodeRef");
    }

    @Override
    public AcmFolder findById(Long folderId)
    {
        return getFolderDao().find(folderId);
    }

    @Override
    public AcmFolder findByNameAndParent(String name, AcmFolder parent)
    {
        return getFolderDao().findFolderByNameInTheGivenParentFolder(name, parent.getId());
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "CONTAINER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        if (folderStructure != null)
        {
            for (int i = 0; i < folderStructure.length(); i++)
            {
                if (isJSONObject(folderStructure, i))
                {
                    JSONObject folderJSONObject = folderStructure.getJSONObject(i);

                    String name = folderJSONObject.getString(AcmFolderConstants.FOLDER_STRUCTURE_KEY_NAME);
                    Boolean attachments = folderJSONObject.getBoolean(AcmFolderConstants.FOLDER_STRUCTURE_KEY_ATTACHMENT);

                    AcmFolder folder = addNewFolder(parentFolder, name);

                    folder = addFolderParticipants(folderJSONObject, folder);

                    if (attachments != null && attachments)
                    {
                        container.setAttachmentFolder(folder);
                        getContainerDao().save(container);
                    }

                    if (isJSONArray(folderJSONObject, AcmFolderConstants.FOLDER_STRUCTURE_KEY_CHILDREN))
                    {
                        addFolderStructure(container, folder,
                                folderJSONObject.getJSONArray(AcmFolderConstants.FOLDER_STRUCTURE_KEY_CHILDREN));
                    }
                }
            }
        }
    }

    private AcmFolder addFolderParticipants(JSONObject folderJSONObject, AcmFolder folder)
    {
        if (folderJSONObject.has("participants"))
        {
            JSONArray participants = folderJSONObject.getJSONArray("participants");
            for (int a = 0; a < participants.length(); a++)
            {
                JSONObject participant = participants.getJSONObject(a);
                AcmParticipant ap = new AcmParticipant();
                ap.setParticipantType(participant.getString("type"));
                ap.setParticipantLdapId(participant.getString("id"));
                folder.getParticipants().add(ap);
            }
            folder = getFolderDao().save(folder);
        }
        return folder;
    }

    private boolean isJSONObject(Object json, Object key)
    {
        if (json != null)
        {
            try
            {
                if (json instanceof JSONObject && key instanceof String)
                {
                    ((JSONObject) json).getJSONObject((String) key);
                    return true;
                }

                if (json instanceof JSONArray && key instanceof Integer)
                {
                    ((JSONArray) json).getJSONObject((Integer) key);
                    return true;
                }
            }
            catch (Exception e)
            {
                log.debug("Element with key={} in the json={} is not JSONObject.", key, json.toString());
            }
        }

        return false;
    }

    private boolean isJSONArray(Object json, Object key)
    {
        if (json != null)
        {
            try
            {
                if (json instanceof JSONObject && key instanceof String)
                {
                    ((JSONObject) json).getJSONArray((String) key);
                    return true;
                }

                if (json instanceof JSONArray && key instanceof Integer)
                {
                    ((JSONArray) json).getJSONArray((Integer) key);
                    return true;
                }
            }
            catch (Exception e)
            {
                log.debug("Element with key={} in the json={} is not JSONObject.", key, json.toString());
            }
        }

        return false;
    }

    @Override
    public String getFolderPath(AcmFolder folder) throws AcmObjectNotFoundException
    {
        if (folder.getParentFolder() != null)
        {
            AcmFolder parent = findById(folder.getParentFolder().getId());
            if (parent == null)
            {
                throw new AcmObjectNotFoundException(folder.getObjectType(), folder.getParentFolder().getId(),
                        "Folder not found in database");
            }
            return getFolderPath(parent) + "/" + folder.getName();
        }
        else
        {
            return "";
        }
    }

    @Override
    public boolean folderPathExists(String folderPath, AcmContainer container) throws AcmFolderException
    {
        log.info("Checking existence of path {} in container id = {}", folderPath, container.getId());

        Objects.requireNonNull(container, "Container should not be null");
        if (folderPath != null)
        {
            folderPath = folderPath.trim();
        }
        if (StringUtils.isEmpty(folderPath))
        {
            throw new AcmFolderException("Folder path should not be null/empty");
        }

        // format path and prepare for split
        if (folderPath.startsWith("/"))
        {
            folderPath = folderPath.substring(1);
        }
        if (folderPath.endsWith("/"))
        {
            folderPath = folderPath.substring(0, folderPath.length() - 1);
        }

        log.info("Formatted path: {}", folderPath);

        String[] targetPathComponents = folderPath.split("/");
        AcmFolder parent = container.getFolder();

        for (String targetPathComponent : targetPathComponents)
        {
            log.info("Checking for folder named {}", targetPathComponent);
            try
            {
                AcmFolder folder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                parent = folder;
            }
            catch (NoResultException nre)
            {
                // this folder doesn't exists in the path
                return false;
            }
        }

        return true;
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 2, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public void copyFolderStructure(Long folderId, AcmContainer containerOfCopy, AcmFolder rootFolderOfCopy)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException
    {

        AcmFolder folderForCopying = findById(folderId);
        List<AcmObject> folderChildren = getFolderChildren(folderForCopying.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());

        copyFolderChildrenStructure(folderChildren, containerOfCopy, rootFolderOfCopy);
    }

    private void copyFolderInnerStructure(Long folderId, AcmContainer containerOfCopy, AcmFolder destinationFolder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException
    {

        AcmFolder folderForCopying = findById(folderId);

        if (folderForCopying.getParentFolder() == null)
        {
            return; // cannot copy ROOT folder
        }

        List<AcmObject> folderChildren = getFolderChildren(folderForCopying.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());
        AcmFolder copiedFolder = addNewFolder(destinationFolder, folderForCopying.getName());

        copyFolderChildrenStructure(folderChildren, containerOfCopy, copiedFolder);
    }

    private void copyFolderChildrenStructure(List<AcmObject> folderChildren, AcmContainer containerOfCopy, AcmFolder destinationFolder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException
    {
        for (AcmObject obj : folderChildren)
        {
            if (EcmFileConstants.OBJECT_FILE_TYPE.equals(obj.getObjectType().toUpperCase()))
            {
                fileService.copyFile(obj.getId(), destinationFolder, containerOfCopy);

            }
            else if (EcmFileConstants.OBJECT_FOLDER_TYPE.equals(obj.getObjectType().toUpperCase()))
            {
                copyFolderInnerStructure(obj.getId(), containerOfCopy, destinationFolder);
            }
        }
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 2, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public void copyDocumentStructure(Long documentId, AcmContainer containerOfCopy, AcmFolder rootFolderOfCopy)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        EcmFile fileForCopying = fileService.findById(documentId);

        AcmFolder documentFolder = fileForCopying.getFolder();
        if (documentFolder.getParentFolder() == null)
        {
            // recreate folder structure as on source
            if (rootFolderOfCopy.getId() == null)
            {
                AcmFolder savedRootFolderOfCopy = getFolderDao().save(rootFolderOfCopy);
                if (containerOfCopy.getFolder().equals(rootFolderOfCopy))
                {
                    containerOfCopy.setFolder(savedRootFolderOfCopy);
                }
                if (containerOfCopy.getAttachmentFolder().equals(rootFolderOfCopy))
                {
                    containerOfCopy.setAttachmentFolder(savedRootFolderOfCopy);
                }
                rootFolderOfCopy = savedRootFolderOfCopy;
            }

            // document is under root folder, no need to create additional folders
            fileService.copyFile(documentId, rootFolderOfCopy, containerOfCopy);
        }
        else
        {
            // create folder structure in saved case file same as in source for the document
            String folderPath = getFolderPath(fileForCopying.getFolder());
            log.debug("folder path = '{}' for folder(id={}, name={})", folderPath, fileForCopying.getFolder().getId(),
                    fileForCopying.getFolder().getName());
            try
            {
                AcmFolder createdFolder = addNewFolderByPath(containerOfCopy.getContainerObjectType(),
                        containerOfCopy.getContainerObjectId(), folderPath);
                fileService.copyFile(documentId, createdFolder, containerOfCopy);
            }
            catch (Exception e)
            {
                log.error("Couldn't create folder structure for document with id={} and will not be copied.", documentId, e);
            }
        }
    }

    @Override
    public AcmContainer findContainerByFolderId(Long folderId) throws AcmObjectNotFoundException
    {
        AcmFolder acmFolder = folderDao.find(folderId);
        if (acmFolder != null)
        {
            AcmFolder rootFolder = findRootParentFolder(acmFolder);
            return getContainerDao().findByFolderId(rootFolder.getId());
        }
        else
        {
            log.warn("Couldn't find folder with id [{}]", folderId);
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found");
        }
    }

    @Override
    public AcmContainer findContainerByFolderIdTransactionIndependent(Long folderId) throws AcmObjectNotFoundException
    {
        AcmFolder acmFolder = folderDao.find(folderId);
        if (acmFolder != null)
        {
            AcmFolder rootFolder = findRootParentFolder(acmFolder);
            return getContainerDao().findByFolderIdTransactionIndependent(rootFolder.getId());
        }
        else
        {
            log.warn("Couldn't find folder with id [{}]", folderId);
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found");
        }
    }

    @Override
    public List<AcmFolder> findModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getFolderDao().findModifiedSince(lastModified, start, pageSize);
    }

    private AcmFolder findRootParentFolder(AcmFolder folder)
    {
        if (folder.getParentFolder() == null)
        {
            return folder;
        }
        return findRootParentFolder(folder.getParentFolder());
    }

    /**
     * retrieves root folder
     *
     * @param parentObjectId
     * @param parentObjectType
     * @return AcmFolder root folder for given objectId, objectType
     */
    @Override
    public AcmFolder getRootFolder(Long parentObjectId, String parentObjectType) throws AcmObjectNotFoundException
    {
        log.debug("get root folder for[{},{}] ", parentObjectId, parentObjectType);

        AcmContainer container = getContainerDao().findFolderByObjectTypeAndId(parentObjectType, parentObjectId);
        if (container == null)
        {
            throw new AcmObjectNotFoundException(parentObjectType, parentObjectId, "Container object not found", null);
        }
        return container.getFolder();
    }

    @Override
    @Transactional
    public void removeLockAndSendMessage(Long objectId, String message)
    {
        AcmObjectLock lock = getObjectLockService().findLock(objectId, AcmFolderConstants.OBJECT_FOLDER_TYPE);
        if (lock != null)
        {
            Map<String, Object> payload = new HashMap<>();
            payload.put("user", lock.getCreator());
            payload.put("eventType", "sync-progress");
            payload.put("message", message);

            getObjectLockService().removeLock(lock);

            getGenericMessagesChannel().send(MessageBuilder.withPayload(payload).build());
        }
    }

    @Override
    public AcmFolder saveFolder(AcmFolder folder)
    {
        return getFolderDao().save(folder);
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public AcmContainerDao getContainerDao()
    {
        return containerDao;
    }

    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }

    public MessageChannel getGenericMessagesChannel()
    {
        return genericMessagesChannel;
    }

    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService) {
        this.objectLockService = objectLockService;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }
}
