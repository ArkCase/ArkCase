package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.pluginmanager.service.AcmConfigurablePlugin;
import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.web.api.MDCConstants;

/**
 * Created by armdev on 3/27/15.
 */
public class AlfrescoRecordsService implements AcmConfigurablePlugin
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private DeclareRecordService declareRecordService;
    private SetRecordMetadataService setRecordMetadataService;
    private FindFolderService findFolderService;
    private CreateOrFindRecordFolderOrRecordCategoryService createOrFindRecordFolderOrRecordCategoryService;
    private MoveToRecordFolderService moveToRecordFolderService;
    private CompleteRecordService completeRecordService;
    private AlfrescoRmaConfig rmaConfig;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Async
    public void declareAllContainerFilesAsRecords(Authentication auth, AcmContainer container, Date receiveDate, String recordFolderName)
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        getAuditPropertyEntityAdapter().setUserId("RECORDS_SERVICE_USER");
        try
        {
            AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);
            declareAsRecords(files, container, receiveDate, recordFolderName);
        }
        catch (AcmListObjectsFailedException e)
        {
            log.error("Cannot finish Record Management Strategy for container [{}]", container.getContainerObjectType() + " "
                    + container.getContainerObjectId(), e);
        }
    }

    @Async
    public void declareAllFilesInFolderAsRecords(AcmCmisObjectList folder, AcmContainer container, Date receiveDate,
            String recordFolderName)
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        getAuditPropertyEntityAdapter().setUserId("RECORDS_SERVICE_USER");

        declareAsRecords(folder, container, receiveDate, recordFolderName);
    }

    @Async
    public void declareAsRecords(AcmCmisObjectList files, AcmContainer container, Date receiveDate, String recordFolderName)
    {
        String originatorOrg = rmaConfig.getDefaultOriginatorOrg();
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        getAuditPropertyEntityAdapter().setUserId("RECORDS_SERVICE_USER");

        try
        {
            for (AcmCmisObject file : files.getChildren())
            {
                String originator = file.getCustodian() != null ? file.getCustodian() : file.getCreator();

                declareFileAsRecord(container, receiveDate, recordFolderName, originatorOrg, originator, file.getCmisObjectId(),
                        file.getStatus(), file.getObjectId(), file.isLink());
            }
        }
        catch (AlfrescoServiceException e)
        {
            log.error("Could not declare records: {}", e.getMessage(), e);
        }
    }

    @Async
    protected void declareFileAsRecord(AcmContainer container, Date receiveDate, String recordFolderName, String originatorOrg,
            String originator, String cmisObjectId, String objectStatus, Long ecmFileId) throws AlfrescoServiceException
    {
        declareFileAsRecord(container, receiveDate, recordFolderName, originatorOrg,  originator,cmisObjectId,
                objectStatus, ecmFileId, false);
    }

    @Async
    protected void declareFileAsRecord(AcmContainer container, Date receiveDate, String recordFolderName, String originatorOrg,
            String originator, String cmisObjectId, String objectStatus, Long ecmFileId, boolean isFileLink) throws AlfrescoServiceException
    {
        if (!isFileLink)
        {
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
            getAuditPropertyEntityAdapter().setUserId("RECORDS_SERVICE_USER");

            if (!((EcmFileConstants.RECORD).equals(objectStatus)))
            {
                declareRecord(cmisObjectId);

                writeRecordMetadata(receiveDate, originatorOrg, cmisObjectId, originator);
                Folder categoryFolder;
                if (getRmaConfig().getRmaUseSameCategoryFolderPathForCaseFiles() && container.getContainerObjectType().equals("CASE_FILE"))
                {
                    categoryFolder = findCategoryFolderByPath(container);
                }
                else
                {
                    categoryFolder = findFolder(container.getContainerObjectType());
                }

                Folder parentFolder = addDateInAlfrescoStructure(categoryFolder, container.getCreated());

                String recordFolderId = createOrFindRecordFolderOrRecordCategory(recordFolderName, parentFolder, "Record Folder");
                log.debug("recordFolderId: {}", recordFolderId);
                moveToRecordFolder(recordFolderId, cmisObjectId);

                completeRecord(cmisObjectId);

                setFileStatusAsRecord(ecmFileId);

                setFileLinksStatusAsRecord(ecmFileId);
            }
        }
    }

    private Folder findCategoryFolderByPath(AcmContainer container) throws AlfrescoServiceException
    {
        String parentFolderCmisId = container.getFolder().getCmisFolderId();

        try
        {
            Folder activeParentFolder = (Folder) ecmFileService.findObjectById(container.getCmisRepositoryId(), parentFolderCmisId);
            String path = activeParentFolder.getPath();
            String categoryFolderName = StringUtils.substringBetween(path,"documentLibrary/", "/");
            return findRecordFolder(categoryFolderName);
        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException("Record Category folder not found for " + container.getFolder().getCmisFolderId(), e);
        }
    }

    public Folder addDateInAlfrescoStructure(Folder categoryFolder) throws AlfrescoServiceException
    {
        return addDateInAlfrescoStructure(categoryFolder, new Date());
    }

    public Folder addDateInAlfrescoStructure(Folder categoryFolder, Date date) throws AlfrescoServiceException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = parseMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String day = parseMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));

        String yearFolderId = createOrFindRecordFolderOrRecordCategory(year, categoryFolder, "Record Category");
        log.debug("yearFolderId: {}", yearFolderId);
        String yearPath = categoryFolder.getName() + "/" + year;
        Folder yearFolder = findRecordFolder(yearPath);

        String monthFolderId = createOrFindRecordFolderOrRecordCategory(month, yearFolder, "Record Category");
        log.debug("monthFolderId: {}", monthFolderId);
        String monthPath = yearPath + "/" + month;
        Folder monthFolder = findRecordFolder(monthPath);

        String dayFolderId = createOrFindRecordFolderOrRecordCategory(day, monthFolder, "Record Category");
        log.debug("dayFolderId: {}", monthFolderId);
        String dayPath = monthPath + "/" + day;
        return findRecordFolder(dayPath);

    }

    private String parseMonthOrDay(int value)
    {
        if (value < 10)
        {
            return "0" + value;
        }
        else
        {
            return String.valueOf(value);
        }
    }

    protected void completeRecord(String cmisFileId) throws AlfrescoServiceException
    {
        Map<String, Object> completeRecordContext = new HashMap<>();
        completeRecordContext.put("ecmFileId", cmisFileId);
        getCompleteRecordService().service(completeRecordContext);
    }

    protected void moveToRecordFolder(String recordFolderId, String cmisFileId) throws AlfrescoServiceException
    {
        Map<String, Object> moveToRecordFolderContext = new HashMap<>();
        moveToRecordFolderContext.put("ecmFileId", cmisFileId);
        moveToRecordFolderContext.put("recordFolderId", recordFolderId);
        getMoveToRecordFolderService().service(moveToRecordFolderContext);
    }

    public String createNewRecordCategoryInBaseFolder(String recordCategoryName) throws AlfrescoServiceException
    {
        Folder baseFolder = findRecordFolder("");
        String categoryFolderCmisId = createOrFindRecordFolderOrRecordCategory(recordCategoryName, baseFolder, "Record Category");

        return categoryFolderCmisId;
    }

    protected String createOrFindRecordFolderOrRecordCategory(String recordFolderName, Folder folder, String type)
            throws AlfrescoServiceException
    {
        Map<String, Object> findRecordFolderContext = new HashMap<>();
        findRecordFolderContext.put("parentFolder", folder);
        findRecordFolderContext.put("recordFolderName", recordFolderName);
        findRecordFolderContext.put("type", type);
        return getCreateOrFindRecordFolderOrRecordCategoryService().service(findRecordFolderContext);
    }

    protected Folder findFolder(String containerObjectType) throws AlfrescoServiceException
    {
        // find the category folder
        Map<String, Object> findCategoryFolderContext = new HashMap<>();
        findCategoryFolderContext.put("objectType", containerObjectType);
        Folder folder = getFindFolderService().service(findCategoryFolderContext);
        return folder;
    }

    private Folder findRecordFolder(String recordFolderId) throws AlfrescoServiceException
    {
        // find the record folder
        Map<String, Object> findCategoryFolderContext = new HashMap<>();
        findCategoryFolderContext.put("folderPath", recordFolderId);
        Folder folder = getFindFolderService().service(findCategoryFolderContext);
        return folder;
    }

    protected void writeRecordMetadata(Date receiveDate, String originatorOrg, String cmisFileId, String originator)
            throws AlfrescoServiceException
    {
        Map<String, Object> metadataContext = new HashMap<>();
        metadataContext.put("ecmFileId", cmisFileId);
        metadataContext.put("publicationDate", new Date());
        metadataContext.put("originator", originator);
        metadataContext.put("originatingOrganization", originatorOrg);
        metadataContext.put("dateReceived", receiveDate);
        getSetRecordMetadataService().service(metadataContext);
    }

    protected void declareRecord(String cmisFileId) throws AlfrescoServiceException
    {
        Map<String, Object> declareContext = new HashMap<>();
        declareContext.put("ecmFileId", cmisFileId);
        getDeclareRecordService().service(declareContext);
    }

    public void setFileStatusAsRecord(Long fileId)
    {
        try
        {
            EcmFile ecmFile = getEcmFileService().findById(fileId);
            if (null == ecmFile)
            {
                throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
            }
            else
            {
                ecmFile.setStatus(EcmFileConstants.RECORD);
                getEcmFileDao().save(ecmFile);
                log.debug("For file with ID: [{}] status is changed to [{}]", ecmFile.getFileId(), EcmFileConstants.RECORD);
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("File with id: [{}] does not exists - ", fileId, e.getMessage());
        }
    }

    public void setFileLinksStatusAsRecord(Long fileId)
    {
        try
        {
            List<EcmFile> ecmFileLinks = getEcmFileService().getFileLinks(fileId);
            for (EcmFile ecmFileLink : ecmFileLinks)
            {
                ecmFileLink.setStatus(EcmFileConstants.RECORD);
                getEcmFileDao().save(ecmFileLink);
                log.debug("For file link with ID: [{}] status is changed to [{}]", ecmFileLink.getFileId(), EcmFileConstants.RECORD);
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("File link with id: [{}] does not exists - ", fileId, e.getMessage());
        }
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

    public AlfrescoRmaConfig getRmaConfig()
    {
        return rmaConfig;
    }

    public void setRmaConfig(AlfrescoRmaConfig rmaConfig)
    {
        this.rmaConfig = rmaConfig;
    }

    public DeclareRecordService getDeclareRecordService()
    {
        return declareRecordService;
    }

    public void setDeclareRecordService(DeclareRecordService declareRecordService)
    {
        this.declareRecordService = declareRecordService;
    }

    public SetRecordMetadataService getSetRecordMetadataService()
    {
        return setRecordMetadataService;
    }

    public void setSetRecordMetadataService(SetRecordMetadataService setRecordMetadataService)
    {
        this.setRecordMetadataService = setRecordMetadataService;
    }

    public FindFolderService getFindFolderService()
    {
        return findFolderService;
    }

    public void setFindFolderService(FindFolderService findFolderService)
    {
        this.findFolderService = findFolderService;
    }

    public CreateOrFindRecordFolderOrRecordCategoryService getCreateOrFindRecordFolderOrRecordCategoryService()
    {
        return createOrFindRecordFolderOrRecordCategoryService;
    }

    public void setCreateOrFindRecordFolderOrRecordCategoryService(
            CreateOrFindRecordFolderOrRecordCategoryService createOrFindRecordFolderOrRecordCategoryService)
    {
        this.createOrFindRecordFolderOrRecordCategoryService = createOrFindRecordFolderOrRecordCategoryService;
    }

    public MoveToRecordFolderService getMoveToRecordFolderService()
    {
        return moveToRecordFolderService;
    }

    public void setMoveToRecordFolderService(MoveToRecordFolderService moveToRecordFolderService)
    {
        this.moveToRecordFolderService = moveToRecordFolderService;
    }

    public CompleteRecordService getCompleteRecordService()
    {
        return completeRecordService;
    }

    public void setCompleteRecordService(CompleteRecordService completeRecordService)
    {
        this.completeRecordService = completeRecordService;
    }

    @Override
    public boolean isEnabled()
    {
        return rmaConfig.getIntegrationEnabled();
    }

    @Override
    public String getName()
    {
        return AlfrescoRmaPluginConstants.RMA_PLUGIN;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
