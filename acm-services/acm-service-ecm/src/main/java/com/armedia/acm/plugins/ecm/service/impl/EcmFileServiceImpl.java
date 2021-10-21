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
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.EcmFileLinkException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFilePostUploadEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.LinkTargetFileDTO;
import com.armedia.acm.plugins.ecm.model.ProgressbarDetails;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.plugins.ecm.model.event.EcmFileConvertEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.ProgressIndicatorService;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.service.objectlock.annotation.AcmAcquireAndReleaseObjectLock;
import com.armedia.acm.service.objectlock.annotation.AcmAcquireObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileServiceImpl implements ApplicationEventPublisherAware, EcmFileService
{
    private Logger log = LogManager.getLogger(getClass());

    private EcmFileTransaction ecmFileTransaction;

    private EcmFileDao ecmFileDao;

    private AcmContainerDao containerFolderDao;

    private AcmFolderDao folderDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private Map<String, String> sortParameterNameToCmisFieldName;

    private Map<String, String> solrObjectTypeToAcmType;

    private ExecuteSolrQuery solrQuery;

    private Map<String, String> categoryMap;

    private SearchResults searchResults;

    private FolderAndFilesUtils folderAndFilesUtils;

    private CmisConfigUtils cmisConfigUtils;

    private EcmFileParticipantService fileParticipantService;

    private AcmParticipantService participantService;

    private RecycleBinItemService recycleBinItemService;

    private ProgressIndicatorService progressIndicatorService;

    private ProgressbarDetails progressbarDetails;

    private EcmFileConfig ecmFileConfig;

    private MessageChannel genericMessagesChannel;

    private AcmObjectLockService objectLockService;

    private EmailSenderConfig emailSenderConfig;

    private CamelContextManager camelContextManager;

    private FileEventPublisher fileEventPublisher;

    private EmailAttachmentExtractorComponent emailAttachmentExtractorComponent;

    private AcmFolderService acmFolderService;

    @Override
    public CmisObject findObjectByPath(String path) throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        String cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        properties.put(PropertyIds.PATH, path);
        properties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        return (CmisObject) getCamelContextManager().send(ArkCaseCMISActions.GET_OBJECT_BY_PATH, properties);
    }

    @Override
    public CmisObject findObjectById(String cmisRepositoryId, String cmisId) throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        properties.put(ArkCaseCMISConstants.CMIS_OBJECT_ID, cmisId);

        return (CmisObject) getCamelContextManager().send(ArkCaseCMISActions.GET_OBJECT_BY_ID, properties);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public EcmFile upload(String arkcaseFileName, String fileType, String fileCategory, InputStream fileContents, String fileContentType,
            String fileName, Authentication authentication, String targetCmisFolderId, String parentObjectType, Long parentObjectId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        String cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        return upload(arkcaseFileName, fileType, fileCategory, fileContents, fileContentType, fileName, authentication, targetCmisFolderId,
                parentObjectType, parentObjectId, cmisRepositoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public EcmFile upload(String arkcaseFileName, String fileType, String fileCategory, InputStream fileContents, String fileContentType,
            String fileName, Authentication authentication, String targetCmisFolderId, String parentObjectType, Long parentObjectId,
            String cmisRepositoryId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        // the normal method, when a file is uploaded from ArkCase, so there is no existing file in the ECM repository
        // yet
        Document cmisDocument = null;

        return upload(arkcaseFileName, fileType, fileCategory, fileContents, fileContentType, fileName, authentication, targetCmisFolderId,
                parentObjectType, parentObjectId, cmisRepositoryId, cmisDocument);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(String arkcaseFileName, String fileType, String fileCategory, InputStream fileContents, String fileContentType,
            String fileName, Authentication authentication, String targetCmisFolderId, String parentObjectType, Long parentObjectId,
            String cmisRepositoryId, Document existingCmisDocument) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        // typically this method is calld directly only by the ECM file sync feature, when a user has added a file to
        // the
        // ECM system directly, without using ArkCase.

        log.info("The user '{}' uploaded file: '{}'", authentication.getName(), fileName);

        EcmFile metadata = new EcmFile();
        metadata.setFileType(fileType);
        metadata.setCategory(fileCategory);
        metadata.setFileActiveVersionMimeType(fileContentType);
        metadata.setFileName(arkcaseFileName);
        metadata.setCmisRepositoryId(cmisRepositoryId);
        return upload(authentication, parentObjectType, parentObjectId, targetCmisFolderId, fileName, fileContents, metadata,
                existingCmisDocument);
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(String arkcaseFileName, String fileType, MultipartFile file, Authentication authentication,
            String targetCmisFolderId, String parentObjectType, Long parentObjectId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        return upload(arkcaseFileName, fileType, null, file, authentication, targetCmisFolderId, parentObjectType, parentObjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(String arkcaseFileName, String fileType, String fileLang, MultipartFile file, Authentication authentication,
            String targetCmisFolderId, String parentObjectType, Long parentObjectId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        EcmFile metadata = new EcmFile();
        metadata.setFileType(fileType);
        metadata.setFileLang(fileLang);
        metadata.setFileName(arkcaseFileName);
        metadata.setFileActiveVersionMimeType(file.getContentType());
        return upload(authentication, file, targetCmisFolderId, parentObjectType, parentObjectId, metadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(Authentication authentication, String parentObjectType, Long parentObjectId, String targetCmisFolderId,
            String arkcaseFileName, InputStream fileContents, EcmFile metadata)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        return upload(authentication, parentObjectType, parentObjectId, targetCmisFolderId, arkcaseFileName, fileContents, metadata, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(Authentication authentication, String parentObjectType, Long parentObjectId, String targetCmisFolderId,
            String arkcaseFileName, InputStream fileContents, EcmFile metadata, Document existingCmisDocument)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = getOrCreateContainer(parentObjectType, parentObjectId, metadata.getCmisRepositoryId());

        // TODO: disgusting hack here. getOrCreateContainer is transactional, and may update the container or the
        // container folder, e.g. by adding participants. If it does, the object we get back won't have those changes,
        // so we could get a unique constraint violation later on. Hence the need to update the object
        // here. BETTER SOLUTION: split "getOrCreateContainer" into a readonly get, and then a writable create if the
        // get doesn't find anything. Or else find some other way not to have to refresh the object here.
        getContainerFolderDao().getEm().refresh(container);

        EcmFileAddedEvent event = null;

        try
        {
            String cmisRepositoryId = metadata.getCmisRepositoryId() == null ? ecmFileConfig.getDefaultCmisId()
                    : metadata.getCmisRepositoryId();
            metadata.setCmisRepositoryId(cmisRepositoryId);

            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(authentication, arkcaseFileName, container, targetCmisFolderId,
                    fileContents, metadata, existingCmisDocument);

            event = new EcmFileAddedEvent(uploaded, authentication);
            event.setUserId(authentication.getName());
            event.setSucceeded(true);
            applicationEventPublisher.publishEvent(event);

            return uploaded;
        }
        catch (IOException | ArkCaseFileRepositoryException e)
        {
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException(metadata.getFileName(),
                    e.getCause() == null ? e.getMessage() : e.getCause().getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcmFile upload(Authentication authentication, MultipartFile file, String targetCmisFolderId, String parentObjectType,
            Long parentObjectId, EcmFile metadata) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.info("The user '{}' uploaded file: '{}'", authentication.getName(), file.getOriginalFilename());
        log.info("File size: {}; content type: {}", file.getSize(), file.getContentType());

        AcmContainer container = getOrCreateContainer(parentObjectType, parentObjectId);
        // makes a problem when trying to upload file to a children node folder

        // TODO: disgusting hack here. getOrCreateContainer is transactional, and may update the container or the
        // container folder, e.g. by adding participants. If it does, the object we get back won't have those changes,
        // so we could get a unique constraint violation later on. Hence the need to update the object
        // here. BETTER SOLUTION: split "getOrCreateContainer" into a readonly get, and then a writable create if the
        // get doesn't find anything. Or else find some other way not to have to refresh the object here.
        getContainerFolderDao().getEm().refresh(container);

        EcmFileAddedEvent event = null;
        try
        {

            String cmisRepositoryId = metadata.getCmisRepositoryId() == null ? ecmFileConfig.getDefaultCmisId()
                    : metadata.getCmisRepositoryId();

            metadata.setCmisRepositoryId(cmisRepositoryId);
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(authentication, file.getOriginalFilename(), container,
                    targetCmisFolderId, metadata, file);

            if (StringUtils.isNotEmpty(uploaded.getUuid()))
            {
                log.debug("Stop progressbar executor in stage 3, for file {} and set file upload success to {}", uploaded.getUuid(), true);
                progressIndicatorService.end(uploaded.getUuid(), true);
            }

            event = new EcmFileAddedEvent(uploaded, authentication);
            event.setUserId(authentication.getName());
            event.setSucceeded(true);
            applicationEventPublisher.publishEvent(event);

            return uploaded;
        }
        catch (IOException | ArkCaseFileRepositoryException e)
        {
            log.error("Could not upload file: {}", e.getMessage(), e);
            throw new AcmCreateObjectFailedException(file.getOriginalFilename(), e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmCreateObjectFailedException.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile update(EcmFile ecmFile, MultipartFile file, Authentication authentication) throws AcmCreateObjectFailedException
    {
        try
        {
            return update(ecmFile, file.getInputStream(), authentication);
        }
        catch (IOException e)
        {
            throw new AcmCreateObjectFailedException(ecmFile.getFileName(), e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmCreateObjectFailedException.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile update(EcmFile ecmFile, InputStream inputStream, Authentication authentication) throws AcmCreateObjectFailedException
    {
        log.info("The user '{}' is updating file: '{}'", authentication.getName(), ecmFile.getFileName());

        EcmFileUpdatedEvent event = null;
        File tempFile = null;
        try
        {
            tempFile = File.createTempFile("arkcase-update-file-transaction", null);
            FileUtils.copyInputStreamToFile(inputStream, tempFile);

            EcmFile updated = getEcmFileTransaction().updateFileTransaction(authentication, ecmFile, tempFile, null);

            event = new EcmFileUpdatedEvent(updated, authentication);

            event.setSucceeded(true);
            applicationEventPublisher.publishEvent(event);

            return updated;
        }
        catch (IOException | ArkCaseFileRepositoryException e)
        {
            log.error("Could not update file: {} ", e.getMessage(), e);
            throw new AcmCreateObjectFailedException(ecmFile.getFileName(), e.getMessage(), e);
        }
        finally
        {
            FileUtils.deleteQuietly(tempFile);
        }

    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    public String download(Long id) throws AcmUserActionFailedException
    {
        try
        {
            EcmFile ecmFile = getEcmFileDao().find(id);
            String content = getEcmFileTransaction().downloadFileTransaction(ecmFile);

            return content;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DOWNLOAD_FILE,
                    EcmFileConstants.OBJECT_FILE_TYPE, id, "Download file failed", e);
        }
    }

    @Override
    @AcmAcquireObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public String checkout(Long id) throws AcmUserActionFailedException
    {
        return download(id);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    public InputStream downloadAsInputStream(Long id) throws AcmUserActionFailedException
    {
        return performDownloadAsInputStream(id, new String());
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    public InputStream downloadAsInputStream(Long id, String version) throws AcmUserActionFailedException
    {
        return performDownloadAsInputStream(id, version);
    }

    private InputStream performDownloadAsInputStream(Long id, String version) throws AcmUserActionFailedException
    {
        try
        {
            EcmFile ecmFile = getEcmFileDao().find(id);
            InputStream content = getEcmFileTransaction().downloadFileTransactionAsInputStream(ecmFile, version);

            return content;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not create folder: {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DOWNLOAD_FILE_AS_INPUTSTREAM,
                    EcmFileConstants.OBJECT_FILE_TYPE, id, "Download as InputStream failed", e);
        }
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "READ")
    public InputStream downloadAsInputStream(EcmFile ecmFile) throws AcmUserActionFailedException
    {
        try
        {
            InputStream content = getEcmFileTransaction().downloadFileTransactionAsInputStream(ecmFile);

            return content;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Content could not be retrieved from {}. Cause: {} ", ecmFile.getCmisRepositoryId(), e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DOWNLOAD_FILE_AS_INPUTSTREAM,
                    EcmFileConstants.OBJECT_FILE_TYPE, ecmFile.getId(), "Download as InputStream failed", e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmCreateObjectFailedException.class)
    public String createFolder(String folderPath) throws AcmCreateObjectFailedException
    {
        String path = addDateInPath(folderPath, true);
        if (path != null)
        {
            int endIndex = folderPath.lastIndexOf("/");
            path = path + folderPath.substring(endIndex);
            return createFolder(path, ecmFileConfig.getDefaultCmisId());
        }
        else
        {
            return createFolder(folderPath, ecmFileConfig.getDefaultCmisId());
        }
    }

    @Override
    public String addDateInPath(String folderPath, Boolean flag) throws AcmCreateObjectFailedException
    {
        String path = folderPath;

        if (flag)
        {
            int endIndex = folderPath.lastIndexOf("/");
            if (endIndex == -1)
            {
                return null;
            }
            path = folderPath.substring(0, endIndex);
        }

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        path = path + "/" + String.valueOf(calendar.get(Calendar.YEAR));
        createFolder(path, ecmFileConfig.getDefaultCmisId());

        int month = calendar.get(Calendar.MONTH) + 1;
        path = addValueToPath(path, month);
        createFolder(path, ecmFileConfig.getDefaultCmisId());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        path = addValueToPath(path, day);
        createFolder(path, ecmFileConfig.getDefaultCmisId());

        return path;
    }

    private String addValueToPath(String path, int dayOrMonth)
    {
        String value;
        if (dayOrMonth < 10)
        {
            value = "0" + String.valueOf(dayOrMonth);
        }
        else
        {
            value = String.valueOf(dayOrMonth);
        }
        path = path + "/" + value;
        return path;
    }

    @Override
    @Transactional(rollbackFor = AcmCreateObjectFailedException.class)
    public String createFolder(String folderPath, String cmisRepositoryId) throws AcmCreateObjectFailedException
    {
        try
        {
            Map<String, Object> properties = new HashMap<>();
            properties.put(PropertyIds.PATH, folderPath);
            properties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
            properties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

            Folder result = (Folder) getCamelContextManager().send(ArkCaseCMISActions.GET_OR_CREATE_FOLDER_BY_PATH, properties);

            return result.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not create folder: {} ", e.getMessage(), e);
            throw new AcmCreateObjectFailedException("Folder", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AcmContainer getOrCreateContainer(String objectType, Long objectId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        return getOrCreateContainer(objectType, objectId, ecmFileConfig.getDefaultCmisId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AcmContainer getOrCreateContainer(String objectType, Long objectId, String cmisRepositoryId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.info("Finding folder for object: {} id: {}", objectType, objectId);

        try
        {
            AcmContainer retval = getContainerFolderDao().findFolderByObjectTypeIdAndRepositoryId(objectType, objectId, cmisRepositoryId);
            return retval;
        }
        catch (AcmObjectNotFoundException e)
        {
            return createContainerFolder(objectType, objectId, cmisRepositoryId);
        }
        catch (PersistenceException pe)
        {
            throw new AcmUserActionFailedException("Find container folder", objectType, objectId, pe.getMessage(), pe);
        }
    }

    /**
     * Objects should really have a folder already. Since we got here the object does not actually have one. The
     * application doesn't really care where the folder is, so we'll just create a folder in a sensible location.
     *
     * @param objectType
     * @param objectId
     * @return
     */
    @Override
    @Transactional
    public AcmContainer createContainerFolder(String objectType, Long objectId, String cmisRepositoryId)
            throws AcmCreateObjectFailedException
    {
        log.debug("Creating new folder for object: {} id: {}", objectType, objectId);

        String path = ecmFileConfig.getDefaultBasePath();
        path += ecmFileConfig.getDefaultPathForObject(objectType);
        path = addDateInPath(path, false);
        path += "/" + objectId;

        String cmisFolderId = createFolder(path, cmisRepositoryId);

        log.info("Created new folder " + cmisFolderId + "for object " + objectType + " id " + objectId);

        AcmContainer newContainer = new AcmContainer();
        newContainer.setContainerObjectId(objectId);
        newContainer.setContainerObjectType(objectType);
        newContainer.setCmisRepositoryId(cmisRepositoryId);

        // the container needs a container name, so we'll make one up here, just like we made up a CMIS folder path
        String containerName = objectType + "-" + objectId;
        newContainer.setContainerObjectTitle(containerName);

        AcmFolder newFolder = new AcmFolder();
        newFolder.setCmisFolderId(cmisFolderId);
        newFolder.setCmisRepositoryId(cmisRepositoryId);
        newFolder.setName(EcmFileConstants.CONTAINER_FOLDER_NAME);
        newContainer.setFolder(newFolder);
        newContainer.setAttachmentFolder(newFolder);

        newFolder.setParticipants(getFileParticipantService().getFolderParticipantsFromParentAssignedObject(objectType, objectId));

        newContainer = getContainerFolderDao().save(newContainer);

        return newContainer;
    }

    @Override
    public AcmCmisObjectList allFilesForContainer(Authentication auth, AcmContainer container) throws AcmListObjectsFailedException
    {

        log.debug("All files for container: {} {} ", container.getContainerObjectType(), container.getContainerObjectId());
        // This method is to search for all files that belong to a container, no matter where they are in the
        // folder hierarchy.
        String query = "{!join from=parent_object_id_i to=parent_object_id_i}object_type_s:" + "CONTAINER AND parent_object_id_i:"
                + container.getContainerObjectId() + " AND parent_object_type_s:" + container.getContainerObjectType();

        String filterQuery = "fq=object_type_s:FILE";

        // search for 50 records at a time until we find them all
        int start = 0;
        int max = 50;
        String sortBy = "created";
        String sortDirection = "ASC";

        AcmCmisObjectList retval = findObjects(auth, container, container.getFolder().getId(), EcmFileConstants.CATEGORY_ALL, query,
                filterQuery, start, max, sortBy, sortDirection);

        int totalFiles = retval.getTotalChildren();
        int foundSoFar = retval.getChildren().size();

        log.debug("Got files {} to {} of a total of {}", start, foundSoFar, totalFiles);

        while (foundSoFar < totalFiles)
        {
            start += max;

            AcmCmisObjectList more = findObjects(auth, container, container.getFolder().getId(), EcmFileConstants.CATEGORY_ALL, query,
                    filterQuery, start, max, sortBy, sortDirection);
            retval.getChildren().addAll(more.getChildren());

            foundSoFar += more.getChildren().size();

            log.debug("Got files {} to {} of a total of {}", start, foundSoFar, totalFiles);
        }

        retval.setMaxRows(totalFiles);

        return retval;
    }

    @Override
    public AcmCmisObjectList allFilesForFolder(Authentication auth, AcmContainer container, Long folderId)
            throws AcmListObjectsFailedException
    {
        AcmFolder folder = getFolderDao().find(folderId);
        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
            folderId = folder.getId();
        }
        log.debug("All files for folder with ID " + folderId + "container " + container.getContainerObjectType() + "with ID "
                + container.getContainerObjectId());

        String query = "(object_type_s:FILE OR object_type_s:FOLDER) AND parent_folder_id_i:" + folderId;

        String filterQuery = "fq=object_type_s:FILE";

        // search for 50 records at a time until we find them all
        int start = 0;
        int max = 50;
        String sortBy = "created";
        String sortDirection = "ASC";

        AcmCmisObjectList retval = findObjects(auth, container, folderId, EcmFileConstants.CATEGORY_ALL, query, filterQuery, start, max,
                sortBy, sortDirection);

        int totalFiles = retval.getTotalChildren();
        int foundSoFar = retval.getChildren().size();

        log.debug("Got files {} to {} of a total of {}", start, foundSoFar, totalFiles);

        while (foundSoFar < totalFiles)
        {
            start += max;

            AcmCmisObjectList more = findObjects(auth, container, container.getFolder().getId(), EcmFileConstants.CATEGORY_ALL, query,
                    filterQuery, start, max, sortBy, sortDirection);
            retval.getChildren().addAll(more.getChildren());

            foundSoFar += more.getChildren().size();

            log.debug("Got files {} to {} of a total of {}", start, foundSoFar, totalFiles);
        }

        retval.setMaxRows(totalFiles);

        return retval;
    }

    @Override
    @Transactional(rollbackFor = PersistenceException.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile setFilesActiveVersion(Long fileId, String versionTag) throws PersistenceException
    {

        EcmFile file = getEcmFileDao().find(fileId);
        file.setActiveVersionTag(versionTag);
        for (EcmFileVersion fileVersion : file.getVersions())
        {
            if (fileVersion.getVersionTag().equals(versionTag))
            {
                file.setFileActiveVersionMimeType(fileVersion.getVersionMimeType());
                file.setFileActiveVersionNameExtension(fileVersion.getVersionFileNameExtension());
                break;
            }
        }
        List<EcmFile> frevvoFiles = getEcmFileDao().findForContainer(file.getContainer().getId());
        for (EcmFile frevvoFile : frevvoFiles)
        {
            if (frevvoFile.getFileType().equals("case_file_xml") && frevvoFile.getFileName().equals("form_case_file")
                    || frevvoFile.getFileType().equals("complaint_file_xml") && frevvoFile.getFileName().equals("form_complaint_file")
                    || frevvoFile.getFileType().equals("timesheet_xml") && frevvoFile.getFileName().equals("form_timesheet")
                    || frevvoFile.getFileType().equals("costsheet_xml") && frevvoFile.getFileName().equals("form_costsheet"))
            {
                if (!frevvoFile.getVersions().isEmpty())
                {
                    frevvoFile.setActiveVersionTag(versionTag);
                    frevvoFile.setFileActiveVersionMimeType(frevvoFile.getVersions().get(0).getVersionMimeType());
                    frevvoFile.setFileActiveVersionNameExtension(frevvoFile.getVersions().get(0).getVersionFileNameExtension());
                    getEcmFileDao().save(frevvoFile);
                }
            }
        }

        return getEcmFileDao().save(file);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 3, objectType = "FOLDER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public AcmCmisObjectList listAllSubFolderChildren(String category, Authentication auth, AcmContainer container, Long folderId,
            int startRow, int maxRows, String sortBy, String sortDirection) throws AcmListObjectsFailedException, AcmObjectNotFoundException
    {

        log.debug("All children objects from folder: {}", folderId);

        AcmFolder folder = getFolderDao().find(folderId);
        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
        }
        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
            folderId = folder.getId();
        }
        String query = "(object_type_s:FILE OR object_type_s:FOLDER) AND parent_folder_id_i:" + folderId;
        String filterQuery = category == null ? "fq=hidden_b:false"
                : "fq=(category_s:" + category + " OR category_s:" + category.toUpperCase() + ") AND hidden_b:false"; // in
        // case some bad data gets through

        AcmCmisObjectList retval = findObjects(auth, container, folderId, EcmFileConstants.CATEGORY_ALL, query, filterQuery, startRow,
                maxRows, sortBy, sortDirection);
        return retval;
    }

    @Override
    @PreAuthorize("hasPermission(#container.folder.id, 'FOLDER', 'read|group-read|write|group-write')")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "CONTAINER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public AcmCmisObjectList listFolderContents(Authentication auth, AcmContainer container, String category, String sortBy,
            String sortDirection, int startRow, int maxRows) throws AcmListObjectsFailedException
    {

        // This method is to search for objects in the root of a container. So we restrict the return list
        // to those items whose parent folder ID is the container folder id.... Note, this query assumes
        // only files and folders will have a "folder_id_i" attribute with a value that matches a container
        // folder id
        String query = "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:" + "CONTAINER AND parent_object_id_i:"
                + container.getContainerObjectId() + " AND parent_object_type_s:" + container.getContainerObjectType();

        String filterQuery = category == null ? "fq=hidden_b:false"
                : "fq=(category_s:" + category + " OR category_s:" + category.toUpperCase() + ") AND hidden_b:false"; // in
        // case some bad data gets through

        return findObjects(auth, container, container.getFolder().getId(), category, query, filterQuery, startRow, maxRows, sortBy,
                sortDirection);

    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "CONTAINER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public AcmCmisObjectList listFlatSearchResults(Authentication auth, AcmContainer container, String category, String sortBy,
            String sortDirection, int startRow, int maxRows, String searchFilter) throws AcmListObjectsFailedException
    {

        String query = String.format("(object_type_s:FILE AND parent_object_type_s:%s AND parent_object_id_s:%s) OR "
                + "(object_type_s:FOLDER AND parent_container_object_type_s:%s AND parent_container_object_id_s:%s)",
                container.getContainerObjectType(), container.getContainerObjectId(), container.getContainerObjectType(),
                container.getContainerObjectId());

        String fq = String.format("fq=name_partial:%s AND hidden_b:false", searchFilter);

        return findObjects(auth, container, container.getFolder().getId(), category, query, fq, startRow, maxRows, sortBy, sortDirection);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "CONTAINER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public AcmCmisObjectList listFlatSearchResultsAdvanced(Authentication auth, AcmContainer container, String category, String sortBy,
            String sortDirection, int startRow, int maxRows, String searchFilter) throws AcmListObjectsFailedException
    {
        String query = String.format("object_type_s:FILE AND parent_object_type_s:%s AND parent_object_id_s:%s",
                container.getContainerObjectType(), container.getContainerObjectId());
        String fq = searchFilter.equals("") ? "hidden_b:false" : String.format("fq=(%s) AND hidden_b:false", searchFilter);

        return findObjects(auth, container, container.getFolder().getId(), category, query, fq, startRow, maxRows, sortBy, sortDirection);
    }

    @Override
    @PreAuthorize("hasPermission(#container.folder.id, 'FOLDER', 'read|group-read|write|group-write')")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "CONTAINER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    public AcmCmisObjectList listFileFolderByCategory(Authentication auth, AcmContainer container, String sortBy, String sortDirection,
            int startRow, int maxRows, String category) throws AcmListObjectsFailedException
    {
        String query = "parent_object_id_i:" + container.getContainerObjectId() + " AND parent_object_type_s:"
                + container.getContainerObjectType();

        String filterQuery = "fq=(object_type_s:FILE OR object_type_s:FOLDER) AND (category_s:" + category + " OR category_s:"
                + category.toUpperCase() + ") AND hidden_b:false"; // in case some bad data gets through

        return findObjects(auth, container, container.getFolder().getId(), category, query, filterQuery, startRow, maxRows, sortBy,
                sortDirection);
    }

    @Override
    @Transactional(rollbackFor = AcmObjectNotFoundException.class)
    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write')")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public void declareFileAsRecord(Long fileId, Authentication authentication) throws AcmObjectNotFoundException
    {

        if (null != fileId)
        {
            EcmFile ecmFile = findById(fileId);
            if (ecmFile == null)
            {
                log.error("File with id: {} does not exists", fileId);
                throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
            }
            else
            {
                if (!((EcmFileConstants.RECORD).equals(ecmFile.getStatus())))
                {
                    EcmFileDeclareRequestEvent event = new EcmFileDeclareRequestEvent(ecmFile, authentication);
                    event.setSucceeded(true);
                    getApplicationEventPublisher().publishEvent(event);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasPermission(#folderId, 'FOLDER', 'read|group-read|write|group-write')")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "WRITE")
    public void declareFolderAsRecord(Long folderId, Authentication authentication, String parentObjectType, Long parentObjectId)
            throws AcmObjectNotFoundException, AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmFolder folder = getFolderDao().find(folderId);
        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
            folderId = folder.getId();
        }
        if (null != folderId)
        {
            if (folder == null)
            {
                log.error("Folder with id: {} does not exists", folderId);
                throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FOLDER_TYPE, folderId, "Folder not found", null);
            }
            else
            {
                List<EcmFile> filesInFolderAndSubfolders = getAcmFolderService().getFilesInFolderAndSubfolders(folderId);
                filesInFolderAndSubfolders.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase(EcmFileConstants.ACTIVE))
                        .map(file -> new EcmFileDeclareRequestEvent(file, true, authentication))
                        .forEach(event -> getApplicationEventPublisher().publishEvent(event));
            }
        }
    }

    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 2, objectType = "FOLDER", lockType = "READ", lockChildObjects = false, unlockChildObjects = false)
    private AcmCmisObjectList findObjects(Authentication auth, AcmContainer container, Long folderId, String category, String query,
            String filterQuery, int startRow, int maxRows, String sortBy, String sortDirection) throws AcmListObjectsFailedException
    {
        AcmFolder folder = getFolderDao().find(folderId);
        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
            folderId = folder.getId();
        }
        try
        {
            String sortParam = listFolderContents_getSortSpec(sortBy, sortDirection);

            String results = getSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.QUICK_SEARCH, query, startRow, maxRows, sortParam,
                    filterQuery);
            JSONArray docs = getSearchResults().getDocuments(results);
            int numFound = getSearchResults().getNumFound(results);

            AcmCmisObjectList retval = buildAcmCmisObjectList(container, folderId, category, numFound, sortBy, sortDirection, startRow,
                    maxRows);

            buildChildren(docs, retval);

            return retval;
        }
        catch (Exception e)
        {
            log.error("Could not list folder contents: {}", e.getMessage(), e);
            throw new AcmListObjectsFailedException("Folder Contents", e.getMessage(), e);
        }
    }

    private void buildChildren(JSONArray docs, AcmCmisObjectList retval) throws ParseException
    {
        List<AcmCmisObject> cmisObjects = new ArrayList<>();
        retval.setChildren(cmisObjects);

        int count = docs.length();
        SimpleDateFormat solrFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);
        for (int a = 0; a < count; a++)
        {
            JSONObject doc = docs.getJSONObject(a);

            AcmCmisObject object = buildAcmCmisObject(solrFormat, doc);

            cmisObjects.add(object);
        }
    }

    private AcmCmisObjectList buildAcmCmisObjectList(AcmContainer container, Long folderId, String category, int numFound, String sortBy,
            String sortDirection, int startRow, int maxRows)
    {
        AcmFolder folder = getFolderDao().find(folderId);
        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
            folderId = folder.getId();
        }
        AcmCmisObjectList retval = new AcmCmisObjectList();
        retval.setContainerObjectId(container.getContainerObjectId());
        retval.setContainerObjectType(container.getContainerObjectType());
        retval.setFolderId(folderId);
        retval.setTotalChildren(numFound);
        retval.setCategory(category == null ? "all" : category);
        retval.setSortBy(sortBy);
        retval.setSortDirection(sortDirection);
        retval.setStartRow(startRow);
        retval.setMaxRows(maxRows);
        return retval;
    }

    protected AcmCmisObject buildAcmCmisObject(SimpleDateFormat solrFormat, JSONObject doc) throws ParseException
    {
        AcmCmisObject object = new AcmCmisObject();

        String categoryText = getSearchResults().extractString(doc, SearchConstants.PROPERTY_FILE_CATEGORY);
        if (categoryText != null && getCategoryMap().containsKey(categoryText.toLowerCase()))
        {
            object.setCategory(getCategoryMap().get(categoryText.toLowerCase()));
        }

        Date created = getSearchResults().extractDate(solrFormat, doc, SearchConstants.PROPERTY_CREATED);
        object.setCreated(created);

        String solrType = getSearchResults().extractString(doc, SearchConstants.PROPERTY_OBJECT_TYPE);
        String objectType = getSolrObjectTypeToAcmType().get(solrType);
        object.setObjectType(objectType);

        object.setCreator(getSearchResults().extractString(doc, SearchConstants.PROPERTY_CREATOR));

        object.setModified(getSearchResults().extractDate(solrFormat, doc, SearchConstants.PROPERTY_MODIFIED));

        object.setName(getSearchResults().extractString(doc, SearchConstants.PROPERTY_NAME));

        object.setObjectId(getSearchResults().extractLong(doc, SearchConstants.PROPERTY_OBJECT_ID_S));

        object.setType(getSearchResults().extractString(doc, SearchConstants.PROPERTY_FILE_TYPE));

        object.setVersion(getSearchResults().extractString(doc, SearchConstants.PROPERTY_VERSION));

        object.setModifier(getSearchResults().extractString(doc, SearchConstants.PROPERTY_MODIFIER));

        object.setCmisObjectId(getSearchResults().extractString(doc, SearchConstants.PROPERTY_CMIS_VERSION_SERIES_ID));

        object.setMimeType(getSearchResults().extractString(doc, SearchConstants.PROPERTY_MIME_TYPE));

        object.setExt(getSearchResults().extractString(doc, SearchConstants.PROPERTY_EXT));

        object.setStatus(getSearchResults().extractString(doc, SearchConstants.PROPERTY_STATUS));

        object.setPublicFlag(getSearchResults().extractBoolean(doc, SearchConstants.PROPERTY_PUBLIC_FLAG));

        object.setRedactionStatus(getSearchResults().extractString(doc, SearchConstants.PROPERTY_REDACTION_STATUS));

        object.setReviewStatus(getSearchResults().extractString(doc, SearchConstants.PROPERTY_REVIEW_STATUS));

        object.setLink(getSearchResults().extractBoolean(doc, SearchConstants.PROPERTY_LINK));

        object.setDuplicate(getSearchResults().extractBoolean(doc, SearchConstants.PROPERTY_DUPLICATE));

        object.setCustodian(getSearchResults().extractString(doc, SearchConstants.PROPERTY_FILE_CUSTODIAN));

        if (object.getObjectType().equals(EcmFileConstants.FILE))
        {
            EcmFile file = getEcmFileDao().find(object.getObjectId());
            if (file != null)
            {
                object.setVersionList(file.getVersions());
                object.setPageCount(file.getPageCount());
                object.setLock(file.getLock());
            }
        }

        return object;
    }

    private String listFolderContents_getSortSpec(String sortBy, String sortDirection)
    {
        String sortParam = EcmFileConstants.FOLDER_LIST_DEFAULT_SORT_PARAM;
        if (getSortParameterNameToCmisFieldName().containsKey(sortBy))
        {
            sortParam = getSortParameterNameToCmisFieldName().get(sortBy);
        }
        sortParam = sortParam + " " + sortDirection;
        return sortParam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 3, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        try
        {
            AcmFolder folder = folderDao.find(dstFolderId);

            if (folder.isLink())
            {
                folder = getFolderLinkTarget(folder);
            }

            AcmContainer container = getOrCreateContainer(targetObjectType, targetObjectId);

            return copyFile(fileId, folder, container);
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Could not copy file {}", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, fileId,
                    "Could not copy file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyRecord(Long fileId, Long folderId, String targetObjectType, Long targetObjectId, Authentication authentication)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        AcmFolder folder = getFolderDao().find(folderId);
        EcmFile ecmFile = getEcmFileDao().find(fileId);

        if (ecmFile == null || folder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }

        try
        {
            MultipartFile fileToUpload = getMultipartFromEcmFile(ecmFile);
            AcmMultipartFile acmFileToUpload = new AcmMultipartFile(fileToUpload, false);

            return upload(ecmFile.getFileName(), ecmFile.getFileType(), null, acmFileToUpload, authentication,
                    folder.getCmisFolderId(), targetObjectType, targetObjectId);
        }
        catch (Exception e)
        {
            log.error("Could not copy record {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    ecmFile.getId(),
                    "Could not copy record", e);
        }
    }

    private MultipartFile getMultipartFromEcmFile(EcmFile ecmFile) throws Exception
    {
        String activeVersionSeriesId = ecmFile.getVersionSeriesId() + ";" + ecmFile.getActiveVersionTag();
        Document cmisDoc = (Document) findObjectById(ecmFile.getCmisRepositoryId(), activeVersionSeriesId);

        InputStream inputStream = cmisDoc.getContentStream().getStream();
        byte[] content = IOUtils.toByteArray(inputStream);

        File file = new File(ecmFile.getFileName());
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, content);

        FileItem fileItem = new DiskFileItem("", null, false, file.getName(), (int) file.length(),
                file.getParentFile());

        try (InputStream input = new FileInputStream(file))
        {
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
        }

        return new CommonsMultipartFile(fileItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFile(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);

        if (file == null || targetFolder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }

        if (file.getStatus().equals(EcmFileConstants.RECORD)){
            return copyRecord(file.getId(), targetFolder.getId(), targetContainer.getContainerObjectType(),
                    targetContainer.getContainerObjectId(), SecurityContextHolder.getContext().getAuthentication());
        }

        if (targetFolder.isLink())
        {
            targetFolder = getFolderLinkTarget(targetFolder);
        }
        String internalFileName = getFolderAndFilesUtils().createUniqueIdentificator(file.getFileName());
        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, getFolderAndFilesUtils().getActiveVersionCmisId(file));
        props.put(ArkCaseCMISConstants.DESTINATION_FOLDER_ID, targetFolder.getCmisFolderId());
        props.put(PropertyIds.NAME, internalFileName);
        props.put(EcmFileConstants.FILE_MIME_TYPE, file.getFileActiveVersionMimeType());
        String cmisRepositoryId = targetFolder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(ArkCaseCMISConstants.VERSIONING_STATE,
                getCmisConfigUtils().getVersioningState(ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID));
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {
            Document cmisdocument = (Document) getCamelContextManager().send(ArkCaseCMISActions.COPY_DOCUMENT, props);

            EcmFileVersion fileCopyVersion = new EcmFileVersion();
            fileCopyVersion.setCmisObjectId(
                    cmisdocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID) + ";" + cmisdocument.getVersionLabel());
            fileCopyVersion.setVersionTag(cmisdocument.getVersionLabel());
            copyFileVersionMetadata(file, fileCopyVersion);

            EcmFile fileCopy = copyEcmFile(file, targetFolder, targetContainer, fileCopyVersion,
                    cmisdocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID),
                    cmisdocument.getVersionLabel());

            EcmFile result = getEcmFileDao().save(fileCopy);

            getFileEventPublisher().publishFileCopiedEvent(result, file, SecurityContextHolder.getContext().getAuthentication(), null,
                    true);

            return getFileParticipantService().setFileParticipantsFromParentFolder(result);
        }
        catch (PersistenceException | ArkCaseFileRepositoryException e)
        {
            log.error("Could not copy file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not copy file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmUserActionFailedException.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 2, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFileInArkcase(EcmFile originalFile, String copiedFileNodeId, AcmFolder targetFolder)
            throws AcmUserActionFailedException
    {
        AcmContainer targetContainer;

        if (targetFolder.isLink())
        {
            targetFolder = getFolderLinkTarget(targetFolder);
        }

        try
        {
            targetContainer = getOrCreateContainer(targetFolder.getObjectType(), targetFolder.getId());
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Could not create container for {} with id {} ", targetFolder.getObjectType(), targetFolder.getId(), e.getMessage());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    originalFile.getId(),
                    "Could not copy file", e);
        }

        EcmFile result;

        try
        {
            EcmFileVersion fileCopyVersion = new EcmFileVersion();
            fileCopyVersion.setCmisObjectId(copiedFileNodeId + ";1.0");
            fileCopyVersion.setVersionTag("1.0");
            copyFileVersionMetadata(originalFile, fileCopyVersion);

            EcmFile fileCopy = copyEcmFile(originalFile, targetFolder, targetContainer, fileCopyVersion, copiedFileNodeId, "1.0");

            result = getEcmFileDao().save(fileCopy);

            result = getFileParticipantService().setFileParticipantsFromParentFolder(result);

            return result;
        }
        catch (PersistenceException e)
        {
            log.error("Could not copy file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    originalFile.getId(),
                    "Could not copy file", e);
        }
    }

    protected EcmFile copyEcmFile(EcmFile originalFile, AcmFolder targetFolder, AcmContainer targetContainer,
            EcmFileVersion fileVersion, String versionSeriesId, String activeVersionTag)
    {
        EcmFile fileCopy = new EcmFile();

        if (targetFolder.isLink())
        {
            targetFolder = getFolderLinkTarget(targetFolder);
        }

        fileCopy.setVersionSeriesId(versionSeriesId);
        fileCopy.setFileType(originalFile.getFileType());
        fileCopy.setActiveVersionTag(activeVersionTag);
        fileCopy.setFileName(originalFile.getFileName());
        fileCopy.setFolder(targetFolder);
        fileCopy.setContainer(targetContainer);
        fileCopy.setStatus(originalFile.getStatus());
        fileCopy.setCategory(originalFile.getCategory());
        fileCopy.setFileActiveVersionMimeType(originalFile.getFileActiveVersionMimeType());
        fileCopy.setClassName(originalFile.getClassName());
        fileCopy.setFileActiveVersionNameExtension(originalFile.getFileActiveVersionNameExtension());
        fileCopy.setFileSource(originalFile.getFileSource());
        fileCopy.setLegacySystemId(originalFile.getLegacySystemId());
        fileCopy.setPageCount(originalFile.getPageCount());
        fileCopy.setSecurityField(originalFile.getSecurityField());

        ObjectAssociation personCopy = copyObjectAssociation(originalFile.getPersonAssociation());
        fileCopy.setPersonAssociation(personCopy);

        ObjectAssociation organizationCopy = copyObjectAssociation(originalFile.getOrganizationAssociation());
        fileCopy.setOrganizationAssociation(organizationCopy);

        fileCopy.getVersions().add(fileVersion);
        return fileCopy;
    }

    protected ObjectAssociation copyObjectAssociation(ObjectAssociation original)
    {
        if (original == null)
        {
            return null;
        }

        ObjectAssociation copy = new ObjectAssociation();

        try
        {
            ArkCaseBeanUtils beanUtils = new ArkCaseBeanUtils();
            beanUtils.copyProperties(copy, original);
            copy.setAssociationId(null);
            copy.setParentName(null);
            copy.setParentId(null);
            return copy;
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            log.error("Could not copy object association - should never happen! [{}]", e.getMessage(), e);
            return null;
        }
    }

    protected void copyFileVersionMetadata(EcmFile file, EcmFileVersion fileCopyVersion)
    {
        fileCopyVersion.setVersionMimeType(file.getFileActiveVersionMimeType());
        fileCopyVersion.setVersionFileNameExtension(file.getFileActiveVersionNameExtension());
        fileCopyVersion.setFile(file);

        List<EcmFileVersion> versions = file.getVersions();

        // take the most recent version by default
        EcmFileVersion activeVersion = versions == null || versions.isEmpty() ? null : versions.get(versions.size() - 1);

        // but use the active version if it is there
        if (versions != null)
        {
            for (EcmFileVersion version : versions)
            {
                if (version.getVersionTag().equals(file.getActiveVersionTag()))
                {
                    activeVersion = version;
                }
            }
        }

        if (activeVersion != null)
        {
            fileCopyVersion.setFileSizeBytes(activeVersion.getFileSizeBytes());
            fileCopyVersion.setMediaCreated(activeVersion.getMediaCreated());
            fileCopyVersion.setWidthPixels(activeVersion.getWidthPixels());
            fileCopyVersion.setHeightPixels(activeVersion.getHeightPixels());
            fileCopyVersion.setGpsReadable(activeVersion.getGpsReadable());
            fileCopyVersion.setGpsLongitudeDegrees(activeVersion.getGpsLongitudeDegrees());
            fileCopyVersion.setGpsLatitudeDegrees(activeVersion.getGpsLatitudeDegrees());
            fileCopyVersion.setGpsIso6709(activeVersion.getGpsIso6709());
            fileCopyVersion.setDeviceMake(activeVersion.getDeviceMake());
            fileCopyVersion.setDeviceModel(activeVersion.getDeviceModel());
            fileCopyVersion.setDurationSeconds(activeVersion.getDurationSeconds());
            fileCopyVersion.setSearchablePDF(activeVersion.isSearchablePDF());
            fileCopyVersion.setFileHash(activeVersion.getFileHash());
        }
    }

    @Override
    @Transactional(rollbackFor = AcmObjectNotFoundException.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile updateFileType(Long fileId, String fileType) throws AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File  not found", null);
        }

        file.setFileType(fileType);

        EcmFile saved = getEcmFileDao().save(file);

        return saved;
    }

    @Override
    @Transactional(rollbackFor = AcmObjectNotFoundException.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile updateFile(EcmFile ecmFile) throws AcmObjectNotFoundException
    {

        EcmFile oldFile = findById(ecmFile.getId());
        if (oldFile == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, ecmFile.getId(), "File not found", null);
        }
        getEcmFileDao().getEm().detach(oldFile);
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("oldEcmFile", oldFile);

        // Explicitly set modified to force a save to trigger transformer to reindex data when child objects are changed
        // (e.g participants)
        ecmFile.setModified(new Date());

        ecmFile = getEcmFileDao().save(ecmFile);

        publishFileUpdatedEvent(ecmFile, SecurityContextHolder.getContext().getAuthentication(), true, eventProperties);
        log.info("File update successful [{}]", ecmFile);
        return ecmFile;
    }

    private void publishFileUpdatedEvent(EcmFile file, Authentication authentication, boolean success, Map<String, Object> eventProperties)
    {
        EcmFileUpdatedEvent event = new EcmFileUpdatedEvent(file, authentication);
        event.setEventProperties(eventProperties);
        event.setSucceeded(success);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public int getTotalPageCount(String parentObjectType, Long parentObjectId, List<String> totalPageCountFileTypes,
            List<String> totalPageCountMimeTypes, Authentication auth)
    {
        int totalCount = 0;
        try
        {
            int startRow = 0;
            int maxRows = 50;

            String typeQuery = createQueryFormListAndOperator(totalPageCountFileTypes, SearchConstants.OPERATOR_OR);
            String mimeQuery = createQueryFormListAndOperator(totalPageCountMimeTypes, SearchConstants.OPERATOR_OR);

            String query = SearchConstants.PROPERTY_OBJECT_TYPE + ":FILE AND "
                    + (typeQuery != null && !typeQuery.isEmpty() ? SearchConstants.PROPERTY_FILE_TYPE + ":" + typeQuery + " AND " : "")
                    + (mimeQuery != null && !mimeQuery.isEmpty() ? SearchConstants.PROPERTY_MIME_TYPE + ":" + mimeQuery + " AND " : "")
                    + SearchConstants.PROPERTY_PARENT_OBJECT_TYPE_S + ":" + parentObjectType + " AND "
                    + SearchConstants.PROPERTY_PARENT_OBJECT_ID_I + ":" + parentObjectId;

            JSONArray docs;

            do
            {
                String results = getSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.QUICK_SEARCH, query, startRow, maxRows,
                        SearchConstants.PROPERTY_OBJECT_ID_S + " DESC");
                docs = getSearchResults().getDocuments(results);

                if (docs != null)
                {
                    for (int i = 0; i < docs.length(); i++)
                    {
                        JSONObject doc = docs.getJSONObject(i);

                        if (doc != null && doc.has(SearchConstants.PROPERTY_PAGE_COUNT_I))
                        {
                            int pageCount = doc.getInt(SearchConstants.PROPERTY_PAGE_COUNT_I);
                            totalCount += pageCount;
                        }
                    }
                }

                startRow += maxRows;
            } while (docs != null && docs.length() > 0);
        }
        catch (SolrException e)
        {
            log.error("Cannot take total count. 'Parent Object Type': {}, 'Parent Object ID': {}", parentObjectType, parentObjectId);
        }

        return totalCount;
    }

    @Override
    @Transactional(rollbackFor = AcmObjectNotFoundException.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
    public EcmFile updateSecurityField(Long fileId, String securityFieldValue) throws AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File  not found", null);
        }

        file.setSecurityField(securityFieldValue);

        EcmFile saved = getEcmFileDao().save(file);

        return saved;
    }

    @Override
    public List<EcmFile> saveFilesToTempDirectory(MultiValueMap<String, MultipartFile> files)
    {
        log.debug("Saving files to temp directory...");
        List<EcmFile> uploadList = new ArrayList<>();

        if (files != null)
        {
            String tempUploadFolderPath = FileUtils.getTempDirectoryPath();
            for (Map.Entry<String, List<MultipartFile>> entry : files.entrySet())
            {
                List<MultipartFile> multipartFiles = entry.getValue();
                for (MultipartFile file : multipartFiles)
                {

                    try
                    {
                        // This unique filename will allow the temp file to be tracked before it is saved to Alfresco
                        UUID randomFileId = UUID.randomUUID();
                        String uniqueTempFileName = randomFileId + "_" + file.getOriginalFilename();

                        // Saves the file content to a temporary location
                        File tempFileDestination = new File(tempUploadFolderPath + File.separator + uniqueTempFileName);
                        log.debug("Saving file [{}] as [{}] to [{}]", file.getOriginalFilename(), uniqueTempFileName,
                                tempFileDestination.getCanonicalPath());
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFileDestination);

                        // The available file metadata will be returned as JSON to the caller
                        EcmFile uploadedFile = new EcmFile();
                        uploadedFile.setFileName(file.getOriginalFilename());
                        uploadedFile.setStatus(uniqueTempFileName);
                        uploadedFile.setFileActiveVersionMimeType(file.getContentType());
                        uploadedFile.setCreated(new Date());
                        uploadedFile.setModified(new Date());
                        uploadList.add(uploadedFile);
                    }
                    catch (IOException e)
                    {
                        log.error("Failed to write temp file [{}]", e.getMessage(), e);
                    }
                }
            }
        }

        log.debug("Saved [{}] to temp directory", uploadList);
        return uploadList;
    }

    @Override
    public boolean deleteTempFile(String uniqueFileName)
    {
        log.debug("Deleting temp file [{}]", uniqueFileName);
        boolean fileDeleted = false;
        String tmpDirectory = FileUtils.getTempDirectoryPath();
        File file = new File(tmpDirectory + File.separator + uniqueFileName);
        try
        {
            String absolutePath = file.getCanonicalPath();
            if (!absolutePath.startsWith(tmpDirectory))
            {
                log.error("The unique file name {} does not validate in current directory.", uniqueFileName);
                throw new ValidationException("Invalid path constructed!");
            }
        }
        catch (IOException e)
        {
            log.error("Error while reading contents of {} email template.", uniqueFileName, e);
            throw new ValidationException("Invalid path constructed!");
        }
        if (file.exists())
        {
            fileDeleted = FileUtils.deleteQuietly(file);
            log.trace("File [{}] deleted? : [{}]", file.getAbsolutePath(), fileDeleted);
        }
        return fileDeleted;
    }

    private String createQueryFormListAndOperator(List<String> elements, String operator)
    {
        String query = "";

        if (elements != null)
        {
            Optional<String> reduced = elements.stream().reduce((x, y) -> x + " " + operator + " " + y);
            if (reduced.isPresent())
            {
                query = reduced.get();

                if (query.contains(" " + operator + " "))
                {
                    query = "(" + query + ")";
                }
            }
        }

        return query;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 3, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException
    {
        AcmFolder folder = getFolderDao().find(dstFolderId);
        EcmFile file = getEcmFileDao().find(fileId);
        if (folder == null)
        {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, dstFolderId, "Folder  not found", null);
        }

        if (folder.isLink())
        {
            folder = getFolderLinkTarget(folder);
        }
        if (!file.isLink())
        {
            return moveFile(fileId, targetObjectId, targetObjectType, folder);
        }
        else
        {
            return moveFileLink(fileId, targetObjectId, targetObjectType, folder);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 3, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile moveFileLink(Long fileId, Long targetObjectId, String targetObjectType, AcmFolder folder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException
    {

        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File  not found", null);
        }

        // Check if link already exists in same directory
        if (getEcmFileDao().getFileLinkInCurrentDirectory(file.getVersionSeriesId(), folder.getId()) != null)
        {
            log.error("File with version series id {} already exist in current directory", file.getVersionSeriesId());
            throw new LinkAlreadyExistException("Link for file " + file.getFileName() + " already exist " +
                    "in current directory");
        }

        AcmContainer container = getOrCreateContainer(targetObjectType, targetObjectId);

        EcmFile movedFile;

        try
        {

            file.setContainer(container);

            file.setFolder(folder);

            movedFile = getEcmFileDao().save(file);

            movedFile = getFileParticipantService().setFileParticipantsFromParentFolder(movedFile);

            return movedFile;
        }
        catch (PersistenceException e)
        {
            log.error("Could not move file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not move file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 3, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, AcmFolder folder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {

        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File  not found", null);
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_OBJECT_ID, file.getVersionSeriesId());
        props.put(ArkCaseCMISConstants.DESTINATION_FOLDER_ID, folder.getCmisFolderId());
        props.put(ArkCaseCMISConstants.SOURCE_FOLDER_ID, file.getFolder().getCmisFolderId());
        String cmisRepositoryId = folder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(ArkCaseCMISConstants.VERSIONING_STATE,
                cmisConfigUtils.getVersioningState(ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID));
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        AcmContainer container = getOrCreateContainer(targetObjectType, targetObjectId, cmisRepositoryId);

        EcmFile movedFile;

        try
        {
            Object cmisObject = getCamelContextManager().send(ArkCaseCMISActions.MOVE_DOCUMENT, props);

            if (cmisObject instanceof Document)
            {
                Document cmisDocument = (Document) cmisObject;
                file.setVersionSeriesId(cmisDocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID));
            }
            file.setContainer(container);

            file.setFolder(folder);

            movedFile = getEcmFileDao().save(file);

            movedFile = getFileParticipantService().setFileParticipantsFromParentFolder(movedFile);

            return movedFile;
        }
        catch (PersistenceException | ArkCaseFileRepositoryException e)
        {
            log.error("Could not move file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not move file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile moveFileInArkcase(EcmFile file, AcmFolder targetParentFolder, String targetObjectType)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        String cmisRepositoryId = targetParentFolder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }

        if (targetParentFolder.isLink())
        {
            targetParentFolder = getFolderLinkTarget(targetParentFolder);
        }

        AcmContainer container = getOrCreateContainer(targetObjectType, targetParentFolder.getId(), cmisRepositoryId);
        EcmFile movedFile;

        try
        {
            file.setContainer(container);
            file.setFolder(targetParentFolder);

            movedFile = getEcmFileDao().save(file);
            movedFile = getFileParticipantService().setFileParticipantsFromParentFolder(movedFile);

            return movedFile;
        }
        catch (PersistenceException e)
        {
            log.error("Could not move file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not move file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmUserActionFailedException.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFileFromArkcase(Long fileId)
    {
        getEcmFileDao().deleteFile(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFile(Long objectId) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        deleteFile(objectId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFile(Long objectId, Boolean allVersions) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        EcmFile file = getEcmFileDao().find(objectId);

        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, objectId, "File not found", null);
        }
        else if (file.getStatus().equals(EcmFileConstants.RECORD))
        {
            log.error("Could not delete file with ID {} ", file.getFileId());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "File records cannot be deleted", null);
        }

        boolean removeFileFromDatabase = allVersions || file.getVersions().size() < 2;
        String versionToRemoveFromEcm = removeFileFromDatabase ? file.getVersionSeriesId()
                : file.getVersions().get(file.getVersions().size() - 1).getVersionTag();

        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, versionToRemoveFromEcm);
        String cmisRepositoryId = file.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        // TODO: This is hardcoded because we need camel configuration only for delete file. Should be replaced after
        // completion of all routes. After that mule-config-alfresco-cmis.properties should be deleted from
        // .arkcase/acm/cmis
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(ArkCaseCMISConstants.ALL_VERSIONS, allVersions);

        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {
            if (removeFileFromDatabase)
            {
                getEcmFileDao().deleteFile(objectId);
            }
            else
            {
                file.getVersions().remove(file.getVersions().size() - 1);
                EcmFileVersion current = file.getVersions().get(file.getVersions().size() - 1);
                file.setActiveVersionTag(current.getVersionTag());
                file.setFileActiveVersionNameExtension(current.getVersionFileNameExtension());
                file.setFileActiveVersionMimeType(current.getVersionMimeType());
                getEcmFileDao().save(file);
            }
            getCamelContextManager().send(ArkCaseCMISActions.DELETE_DOCUMENT, props);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not delete file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not delete file", e);
        }
    }

    @Override
    public void deleteCmisObject(CmisObject cmisObject, String cmisRepositoryId) throws Exception
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, cmisObject.getProperty("cmis:versionSeriesId").getFirstValue());
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        // TODO: This is hardcoded because we need camel configuration only for delete file. Should be replaced after
        // completion of all routes. After that mule-config-alfresco-cmis.properties should be deleted from
        // .arkcase/acm/cmis
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(ArkCaseCMISConstants.ALL_VERSIONS, false);
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        List<EcmFile> listFiles = getEcmFileDao()
                .findByCmisFileId(cmisObject.getProperty("cmis:versionSeriesId").getFirstValue().toString());

        if (listFiles != null && !listFiles.isEmpty())
        {
            throw new Exception("File already exists in Arkcase, use another method for deleting Arkcase file!");
        }
        getCamelContextManager().send(ArkCaseCMISActions.DELETE_DOCUMENT, props);
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public RecycleBinItem putFileIntoRecycleBin(Long objectId, Authentication authentication, HttpSession session)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        EcmFile file = getEcmFileDao().find(objectId);
        RecycleBinItem recycleBinItem = null;
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, objectId, "File not found", null);
        }

        try
        {
            if (!file.isLink())
            {
                if (file.getStatus().equals(EcmFileConstants.RECORD))
                {
                    log.error("Could not move file with ID {} to Recycle Bin", file.getFileId());
                    throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                            file.getId(), "File records cannot be deleted", null);
                }

                recycleBinItem = getRecycleBinItemService().putFileIntoRecycleBin(file, authentication, session);
                log.info("File {} successfully moved into recycle bin by user: {}", objectId, file.getModifier());

                deleteFileLinks(file);
            }
            else
            {
                getEcmFileDao().deleteFile(objectId);
            }

        }
        catch (PersistenceException | LinkAlreadyExistException e)
        {
            log.error("Could not put file {} into recycle bin, reason {} ", objectId, e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not put file into recycle bin", e);
        }
        return recycleBinItem;
    }

    @Override
    public void deleteFileLinks(EcmFile file)
    {
        List<EcmFile> fileLinks = getEcmFileDao().getFileLinks(file.getVersionSeriesId());
        for (EcmFile link : fileLinks)
        {
            getEcmFileDao().deleteFile(link.getFileId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFilePermanently(Long fileId, Long recycleBinId) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);

        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
        }

        if (file.getStatus().equals(EcmFileConstants.RECORD))
        {
            log.error("Could not move file with ID {} to Recycle Bin", file.getFileId());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "File records cannot be deleted", null);
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, file.getVersionSeriesId());
        String cmisRepositoryId = file.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, cmisRepositoryId);
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        props.put(ArkCaseCMISConstants.ALL_VERSIONS, true);
        try
        {
            getEcmFileDao().deleteFile(file.getId());
            getRecycleBinItemService().removeItemFromRecycleBin(recycleBinId);
            getCamelContextManager().send(ArkCaseCMISActions.DELETE_DOCUMENT, props);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not delete file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not delete file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFile(Long objectId, Long parentId, String parentType) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(objectId);

        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, objectId, "File not found", null);
        }
        else if (file.getStatus().equals(EcmFileConstants.RECORD))
        {
            log.error("Could not delete file with ID {} ", file.getFileId());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "File records cannot be deleted", null);
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, file.getVersionSeriesId());
        String cmisRepositoryId = file.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }

        // TODO: This is hardcoded because we need camel configuration only for delete file. Should be replaced after
        // completion of all routes. After that mule-config-alfresco-cmis.properties should be deleted from
        // .arkcase/acm/cmis
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        props.put(ArkCaseCMISConstants.ALL_VERSIONS, true);

        try
        {
            getEcmFileDao().deleteFile(objectId);
            getCamelContextManager().send(ArkCaseCMISActions.DELETE_DOCUMENT, props);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not delete file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not delete file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public void deleteFileInArkcase(EcmFile file)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, file.getId(), "File not found", null);
        }
        else if (file.getStatus().equals(EcmFileConstants.RECORD))
        {
            log.error("Could not delete file with ID {} ", file.getFileId());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "File records cannot be deleted", null);
        }

        try
        {
            getEcmFileDao().deleteFile(file.getId());
        }
        catch (PersistenceException e)
        {
            log.error("Could not delete file with id [{}], {} ", file.getId(), e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not delete file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile renameFile(Long fileId, String newFileName) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        String uniqueIdentificator = getFolderAndFilesUtils().createUniqueIdentificator(newFileName);
        EcmFile file = getEcmFileDao().find(fileId);

        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
        }
        Map<String, Object> props = new HashMap<>();
        props.put(ArkCaseCMISConstants.CMIS_DOCUMENT_ID, file.getVersionSeriesId());
        props.put(ArkCaseCMISConstants.NEW_FILE_NAME, uniqueIdentificator);
        String cmisRepositoryId = file.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = ecmFileConfig.getDefaultCmisId();
        }
        props.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        EcmFile renamedFile;
        try
        {
            getCamelContextManager().send(ArkCaseCMISActions.RENAME_DOCUMENT, props);
            file.setFileName(newFileName);
            renamedFile = getEcmFileDao().save(file);
            return renamedFile;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not rename file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE, EcmFileConstants.OBJECT_FILE_TYPE,
                    file.getId(), "Could not rename file", e);
        }
    }

    @Override
    @Transactional(rollbackFor = AcmUserActionFailedException.class)
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 0, objectType = "FILE", lockType = "DELETE")
    public EcmFile renameFileInArkcase(EcmFile file, String newFileName)
    {
        newFileName = getFolderAndFilesUtils().getBaseFileName(newFileName);
        file.setFileName(newFileName);
        return getEcmFileDao().save(file);
    }

    @Override
    @Transactional
    public void removeLockAndSendMessage(Long objectId, String message)
    {
        AcmObjectLock lock = getObjectLockService().findLock(objectId, EcmFileConstants.OBJECT_FILE_TYPE);
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
    public EcmFile findById(Long fileId)
    {
        return getEcmFileDao().find(fileId);
    }

    @Override
    public List<EcmFile> findByIds(List<Long> fileIds)
    {
        return getEcmFileDao().findByIds(fileIds);
    }

    @Override
    public List<EcmFile> findFileByContainerAndFileType(Long containerId, String fileType)
    {
        return getEcmFileDao().findForContainerAndFileType(containerId, fileType);
    }

    @Override
    public EcmFile findOldestFileByContainerAndFileType(Long containerId, String fileType)
    {
        List<EcmFile> files = findFileByContainerAndFileType(containerId, fileType);
        if (files.isEmpty())
        {
            return null;
        }
        else
        {
            return files.get(0);
        }
    }

    @Override
    public File convertFile(String fileKey, String version, String fileExtension, String fileName, String mimeType, EcmFile ecmFile)
            throws IOException
    {
        InputStream fileIs;
        InputStream pdfConvertedIs;
        File tmpPdfConvertedFile = null;

        String timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
        String tmpPdfConvertedFileName = timestamp.concat(fileKey).concat(".pdf");
        String tmpPdfConvertedFullFileName = FileUtils.getTempDirectoryPath().concat(File.separator).concat(tmpPdfConvertedFileName);

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("ecmFileVersion", version);
        eventProperties.put("tmpPdfConvertedFullFileName", tmpPdfConvertedFullFileName);

        // An Event listener is performing the conversion
        EcmFileConvertEvent ecmFileConvertEvent = new EcmFileConvertEvent(ecmFile, eventProperties);
        getApplicationEventPublisher().publishEvent(ecmFileConvertEvent);
        //

        tmpPdfConvertedFile = new File(tmpPdfConvertedFullFileName);

        return tmpPdfConvertedFile;
    }

    @Override
    public String uploadFileChunk(MultipartHttpServletRequest request, String fileName, String uniqueArkCaseHashFileIdentifier)
    {
        String dirPath = System.getProperty("java.io.tmpdir");
        try
        {
            MultipartHttpServletRequest multipartHttpServletRequest = request;
            MultiValueMap<String, MultipartFile> attachments = multipartHttpServletRequest.getMultiFileMap();
            if (attachments != null)
            {
                for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
                {

                    final List<MultipartFile> attachmentsList = entry.getValue();

                    if (attachmentsList != null && !attachmentsList.isEmpty())
                    {

                        for (final MultipartFile attachment : attachmentsList)
                        {
                            fileName = attachment.getOriginalFilename();
                            File dir = new File(dirPath);
                            if (dir.exists())
                            {
                                File file = new File(dirPath + File.separator + uniqueArkCaseHashFileIdentifier + "-" + fileName);
                                attachment.transferTo(file);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("File upload was unsuccessful.", e);
        }
        return fileName;
    }

    @Override
    @Transactional
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 3, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFileAsLink(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException
    {

        try
        {
            AcmFolder folder = folderDao.find(dstFolderId);

            if (folder.isLink())
            {
                folder = getFolderLinkTarget(folder);
            }

            AcmContainer container = getOrCreateContainer(targetObjectType, targetObjectId);

            return copyFileAsLink(fileId, folder, container);
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Could not copy file {}", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, fileId,
                    "Could not copy file", e);
        }
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFileAsLink(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException
    {
        EcmFile file = getEcmFileDao().find(fileId);

        if (file == null || targetFolder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }

        // Check if link already exists in same directory
        if (getEcmFileDao().getFileLinkInCurrentDirectory(file.getVersionSeriesId(), targetFolder.getId()) != null)
        {
            log.error("File with version series id {} already exist in current directory", file.getVersionSeriesId());
            throw new LinkAlreadyExistException("Link for file " + file.getFileName() + " already exist " +
                    "in current directory");
        }

        EcmFile savedFile;

        try
        {
            EcmFileVersion fileCopyVersion = new EcmFileVersion();
            fileCopyVersion.setCmisObjectId(file.getVersions().get(file.getVersions().size() - 1).getCmisObjectId());
            fileCopyVersion.setVersionTag(file.getActiveVersionTag());
            copyFileVersionMetadata(file, fileCopyVersion);

            EcmFile fileCopy = copyEcmFile(file, targetFolder, targetContainer, fileCopyVersion, file.getVersionSeriesId(),
                    file.getActiveVersionTag());
            fileCopy.setLink(true);

            savedFile = getEcmFileDao().save(fileCopy);

            savedFile = getFileParticipantService().setFileParticipantsFromParentFolder(savedFile);

            return savedFile;
        }
        catch (PersistenceException e)
        {
            log.error("Could not create link to file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not create link to file", e);
        }
    }

    @Override
    @Transactional
    public List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {

        return uploadFiles(authentication, parentObjectType, parentObjectId, fileType, null, folderCmisId, request, session);
    }

    @Override
    @Transactional
    public List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String fileLang, String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {

        // for multiple files
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();

        List<EcmFile> uploadedFiles = new ArrayList<>();

        if (attachments != null)
        {
            for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
            {
                final List<MultipartFile> attachmentsList = entry.getValue();

                if (attachmentsList != null && !attachmentsList.isEmpty())
                {
                    for (final MultipartFile attachment : attachmentsList)
                    {
                        if (getEmailAttachmentExtractorComponent().isEmail(attachment))
                        {
                            try
                            {
                                uploadFilesFromEmail(attachment, authentication, parentObjectType, parentObjectId, fileType, folderCmisId,
                                        uploadedFiles, fileLang, request.getParameter("uuid"));
                            }
                            catch (Exception e)
                            {
                                log.error("Upload of email file and attachments failed", e);
                                throw new AcmCreateObjectFailedException(parentObjectType, "Email file and attachments upload failed", e);
                            }
                        }
                        else
                        {
                            AcmMultipartFile acmMultipartFile = new AcmMultipartFile(attachment, false);

                            EcmFile metadata = new EcmFile();
                            metadata.setFileType(fileType);
                            metadata.setFileLang(fileLang);
                            metadata.setFileName(attachment.getOriginalFilename());
                            metadata.setFileActiveVersionMimeType(acmMultipartFile.getContentType());
                            metadata.setUuid(request.getParameter("uuid"));
                            EcmFile temp = upload(authentication, acmMultipartFile, folderCmisId, parentObjectType,
                                    parentObjectId,
                                    metadata);
                            uploadedFiles.add(temp);

                            applicationEventPublisher.publishEvent(new EcmFilePostUploadEvent(temp, authentication.getName()));
                        }
                    }
                }
            }
        }

        return uploadedFiles;
    }

    private void uploadFilesFromEmail(MultipartFile attachment, Authentication authentication, String parentObjectType, Long parentObjectId,
            String fileType, String folderCmisId, List<EcmFile> uploadedFiles, String fileLang, String uuid) throws Exception
    {

        String folderName;

        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = formatter.format(date);

        // extract email attachments
        EmailAttachmentExtractorComponent.EmailContent emailContent = getEmailAttachmentExtractorComponent().extract(attachment);

        // make folder name and create new folder with that name for email file and it's attachment
        folderName = emailContent.getSubject().replaceAll("\\W+", "") + "-" + currentDate + "-" + emailContent.getSender();
        AcmFolder folder = getFolderDao().findByCmisFolderId(folderCmisId);
        AcmFolder emailFolder = null;

        emailFolder = getAcmFolderService().addNewFolder(folder, folderName);

        // upload emails attachment
        uploadAttachment(
                authentication,
                parentObjectType,
                parentObjectId,
                emailFolder.getCmisFolderId(),
                uploadedFiles,
                Objects.requireNonNull(emailContent).getEmailAttachments());

        // create and upload email file and publish event
        AcmMultipartFile acmMultipartFile = new AcmMultipartFile(attachment, false);

        EcmFile metadata = new EcmFile();
        metadata.setFileType(fileType);
        metadata.setFileLang(fileLang);
        metadata.setFileName(attachment.getOriginalFilename());
        metadata.setFileActiveVersionMimeType(acmMultipartFile.getContentType());
        metadata.setUuid(uuid);
        EcmFile temp = upload(
                authentication,
                acmMultipartFile,
                emailFolder.getCmisFolderId(),
                parentObjectType,
                parentObjectId,
                metadata);

        applicationEventPublisher.publishEvent(new EcmFilePostUploadEvent(temp, authentication.getName()));

        uploadedFiles.add(temp);
    }

    private void uploadAttachment(Authentication authentication, String parentObjectType, Long parentObjectId, String folderCmisId,
            List<EcmFile> uploadedFiles, List<EmailAttachmentExtractorComponent.EmailAttachment> emailAttachments)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        for (EmailAttachmentExtractorComponent.EmailAttachment emailAttachment : emailAttachments)
        {
            try
            {
                EcmFile temp = upload(
                        emailAttachment.getName(),
                        "Attachment",
                        "Document",
                        emailAttachment.getInputStream(),
                        emailAttachment.getContentType(),
                        emailAttachment.getName(),
                        authentication,
                        folderCmisId,
                        parentObjectType,
                        parentObjectId);
                uploadedFiles.add(temp);

                applicationEventPublisher.publishEvent(new EcmFilePostUploadEvent(temp, authentication.getName()));
            }
            catch (AcmCreateObjectFailedException e)
            {
                if (e.getMessage().contains("MIME type"))
                {
                    log.warn("Failed to upload email attachment {}. Attachment MIME type not acceptable. Skip this file",
                            emailAttachment.getName());
                }
                else
                {
                    throw e;
                }
            }

        }
    }

    @Override
    public List<EcmFile> getFileLinks(Long fileId) throws AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }
        return getEcmFileDao().getFileLinks(file.getVersionSeriesId());
    }

    @Override
    public List<EcmFile> getFileDuplicates(Long fileId) throws AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);
        if (file == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }
        else
        {
            EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(file, file.getActiveVersionTag());

            List<EcmFile> efList = getEcmFileDao().getEcmFilesWithSameHash(ecmFileVersion.getFileHash());

            return efList;
        }
    }

    @Override
    public void updateFileLinks(EcmFile file) throws AcmObjectNotFoundException
    {
        List<EcmFile> links = getFileLinks(file.getFileId());
        EcmFileVersion activeFileVersion = file.getVersions()
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .findFirst().orElse(null);

        links.forEach(f -> {
            f.setFileType(file.getFileType());
            f.setActiveVersionTag(file.getActiveVersionTag());
            f.setFileName(file.getFileName());
            f.setContainer(file.getContainer());
            f.setStatus(file.getStatus());
            f.setCategory(file.getCategory());
            f.setFileActiveVersionMimeType(file.getFileActiveVersionMimeType());
            f.setClassName(file.getClassName());
            f.setFileActiveVersionNameExtension(file.getFileActiveVersionNameExtension());
            f.setFileSource(file.getFileSource());
            f.setLegacySystemId(file.getLegacySystemId());
            f.setPageCount(file.getPageCount());
            f.setSecurityField(file.getSecurityField());
            f.getVersions().get(0).setVersionTag(file.getActiveVersionTag());
            if (Objects.nonNull(activeFileVersion))
            {
                f.getVersions().get(0).setCmisObjectId(activeFileVersion.getCmisObjectId());
            }

            getEcmFileDao().save(f);
        });
    }

    @Override
    public void updateLinkTargetFile(EcmFile file) throws EcmFileLinkException
    {
        EcmFile f = getEcmFileDao().getLinkTargetFile(file);
        EcmFileVersion activeFileVersion = file.getVersions()
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .findFirst().orElse(null);

        f.setFileType(file.getFileType());
        f.setActiveVersionTag(file.getActiveVersionTag());
        f.setFileName(file.getFileName());
        f.setContainer(file.getContainer());
        f.setStatus(file.getStatus());
        f.setCategory(file.getCategory());
        f.setFileActiveVersionMimeType(file.getFileActiveVersionMimeType());
        f.setClassName(file.getClassName());
        f.setFileActiveVersionNameExtension(file.getFileActiveVersionNameExtension());
        f.setFileSource(file.getFileSource());
        f.setLegacySystemId(file.getLegacySystemId());
        f.setPageCount(file.getPageCount());
        f.setSecurityField(file.getSecurityField());
        f.getVersions().get(0).setVersionTag(file.getActiveVersionTag());
        if (Objects.nonNull(activeFileVersion))
        {
            f.getVersions().get(0).setCmisObjectId(activeFileVersion.getCmisObjectId());
        }

        getEcmFileDao().save(f);
    }

    @Override
    public LinkTargetFileDTO getLinkTargetFileInfo(EcmFile ecmFile) throws EcmFileLinkException
    {
        log.info("Get target file info for the linked file: {}", ecmFile.getId());
        if (!ecmFile.isLink())
        {
            throw new EcmFileLinkException("Ecm file: " + ecmFile.getId() + " is not a link");
        }
        return getEcmFileDao().getLinkTargetFileInfo(ecmFile);

    }

    @Override
    public void checkAndSetDuplicatesByHash(EcmFile ecmFile)
    {
        EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, ecmFile.getActiveVersionTag());

        if (ecmFileVersion.getFileHash() != null)
        {
            List<EcmFile> efList = getEcmFileDao().getEcmFilesWithSameHash(ecmFileVersion.getFileHash());
            if (efList.size() > 1)
            {
                for (EcmFile ef : efList)
                {
                    if (!ef.isDuplicate())
                    {
                        ef.setDuplicate(true);
                        getEcmFileDao().save(ef);
                    }
                }
            }
            else if (efList.size() == 1)
            {
                EcmFile notDuplicate = efList.get(0);
                if (notDuplicate.isDuplicate())
                {
                    notDuplicate.setDuplicate(false);
                    getEcmFileDao().save(notDuplicate);
                }
            }
        }
    }

    @Override
    public List<EcmFile> findFilesByFolder(Long folderId)
    {

        List<EcmFile> files = getEcmFileDao().findByFolderId(folderId);

        return files;
    }

    @Override
    public void download(HttpServletResponse response, boolean isInline, EcmFile ecmFile, String version)
            throws IOException, ArkCaseFileRepositoryException, AcmObjectNotFoundException
    {

        String cmisFileId = getFolderAndFilesUtils().getVersionCmisId(ecmFile, version);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, cmisFileId);

        Object result = getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

        if (result instanceof ContentStream)
        {
            handleFilePayload((ContentStream) result, response, isInline, ecmFile, version);
        }
        else
        {
            fileNotFound(ecmFile.getId());
        }

    }

    // called for normal processing - file was found
    private void handleFilePayload(ContentStream filePayload, HttpServletResponse response, boolean isInline, EcmFile ecmFile,
                                   String version) throws IOException
    {
        String mimeType = filePayload.getMimeType();

        EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, version);
        // OpenCMIS thinks this is an application/octet-stream since the file has no extension
        // we will use what Tika detected in such cases
        if (ecmFileVersion != null)
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFileVersion.getVersionMimeType())))
            {
                mimeType = ecmFileVersion.getVersionMimeType();
            }
        }
        else
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFile.getFileActiveVersionMimeType())))
            {
                mimeType = ecmFile.getFileActiveVersionMimeType();
            }
        }

        String fileName = filePayload.getFileName();
        // endWith will throw a NullPointerException on a null argument. But a file is not required to have an
        // extension... so the extension can be null. So we have to guard against it.
        if (ecmFileVersion != null)
        {
            if (ecmFile != null && ecmFileVersion.getVersionFileNameExtension() != null
                    && !fileName.endsWith(ecmFileVersion.getVersionFileNameExtension()))
            {
                fileName = fileName + ecmFileVersion.getVersionFileNameExtension();
            }
        }
        else
        {
            if (ecmFile != null && ecmFile.getFileActiveVersionNameExtension() != null
                    && !fileName.endsWith(ecmFile.getFileActiveVersionNameExtension()))
            {
                fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
            }
        }

        InputStream fileIs = null;

        try
        {
            fileIs = filePayload.getStream();
            if (!isInline)
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                // add file metadata so it can be displayed in Snowbound
                JSONObject fileMetadata = new JSONObject();
                fileMetadata.put("fileName", ecmFile.getFileName());
                fileMetadata.put("fileType", ecmFile.getFileType());
                String versionTag = (version == null || version.equals("")) ? ecmFile.getActiveVersionTag() : version;
                fileMetadata.put("fileNameWithVersion", String.format("%s:%s", ecmFile.getFileName(), versionTag));
                fileMetadata.put("fileTypeCapitalized",
                        ecmFile.getFileType().substring(0, 1).toUpperCase() + ecmFile.getFileType().substring(1));
                response.setHeader("X-ArkCase-File-Metadata", fileMetadata.toString());
            }
            response.setContentType(mimeType);
            byte[] buffer = new byte[1024];
            int read;
            do
            {
                read = fileIs.read(buffer, 0, buffer.length);
                if (read > 0)
                {
                    response.getOutputStream().write(buffer, 0, read);
                }
            } while (read > 0);
            response.getOutputStream().flush();
        }
        finally
        {
            if (fileIs != null)
            {
                try
                {
                    fileIs.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: {}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void fileNotFound(Long fileId) throws AcmObjectNotFoundException
    {
        throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, String.format("File %d not found", fileId), null);
    }

    @Override
    public void publishEcmFileDownloadedEvent(String ipAddress, EcmFile ecmFile, Authentication auth)
    {
        EcmFileDownloadedEvent event = new EcmFileDownloadedEvent(ecmFile);

        String userId = auth != null ? auth.getName() : "EMAIL_USER";
        event.setUserId(userId);
        event.setSucceeded(true);

        getApplicationEventPublisher().publishEvent(event);
    }

    private AcmFolder getFolderLinkTarget(AcmFolder folderLink)
    {
        return getFolderDao().findByCmisFolderId(folderLink.getCmisFolderId());
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public Map<String, String> getSortParameterNameToCmisFieldName()
    {
        return sortParameterNameToCmisFieldName;
    }

    public void setSortParameterNameToCmisFieldName(Map<String, String> sortParameterNameToCmisFieldName)
    {
        this.sortParameterNameToCmisFieldName = sortParameterNameToCmisFieldName;
    }

    public Map<String, String> getSolrObjectTypeToAcmType()
    {
        return solrObjectTypeToAcmType;
    }

    public void setSolrObjectTypeToAcmType(Map<String, String> solrObjectTypeToAcmType)
    {
        this.solrObjectTypeToAcmType = solrObjectTypeToAcmType;
    }

    public AcmContainerDao getContainerFolderDao()
    {
        return containerFolderDao;
    }

    public void setContainerFolderDao(AcmContainerDao containerFolderDao)
    {
        this.containerFolderDao = containerFolderDao;
    }

    public ExecuteSolrQuery getSolrQuery()
    {
        return solrQuery;
    }

    public void setSolrQuery(ExecuteSolrQuery solrQuery)
    {
        this.solrQuery = solrQuery;
    }

    public Map<String, String> getCategoryMap()
    {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, String> categoryMap)
    {
        this.categoryMap = categoryMap;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
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

    public void setFileParticipantService(EcmFileParticipantService participantService)
    {
        this.fileParticipantService = participantService;
    }

    public AcmParticipantService getParticipantService()
    {
        return participantService;
    }

    public void setParticipantService(AcmParticipantService participantService)
    {
        this.participantService = participantService;
    }

    public ProgressIndicatorService getProgressIndicatorService()
    {
        return progressIndicatorService;
    }

    public void setProgressIndicatorService(ProgressIndicatorService progressIndicatorService)
    {
        this.progressIndicatorService = progressIndicatorService;
    }

    public ProgressbarDetails getProgressbarDetails()
    {
        return progressbarDetails;
    }

    public void setProgressbarDetails(ProgressbarDetails progressbarDetails)
    {
        this.progressbarDetails = progressbarDetails;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }

    public EmailSenderConfig getEmailSenderConfig()
    {
        return emailSenderConfig;
    }

    public void setEmailSenderConfig(EmailSenderConfig emailSenderConfig)
    {
        this.emailSenderConfig = emailSenderConfig;
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

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public RecycleBinItemService getRecycleBinItemService()
    {
        return recycleBinItemService;
    }

    public void setRecycleBinItemService(RecycleBinItemService recycleBinItemService)
    {
        this.recycleBinItemService = recycleBinItemService;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }

    public EmailAttachmentExtractorComponent getEmailAttachmentExtractorComponent()
    {
        return emailAttachmentExtractorComponent;
    }

    public void setEmailAttachmentExtractorComponent(EmailAttachmentExtractorComponent emailAttachmentExtractorComponent)
    {
        this.emailAttachmentExtractorComponent = emailAttachmentExtractorComponent;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }
}
