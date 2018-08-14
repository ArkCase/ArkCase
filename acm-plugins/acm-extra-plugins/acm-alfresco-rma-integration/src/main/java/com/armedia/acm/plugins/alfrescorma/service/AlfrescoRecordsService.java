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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.pluginmanager.service.AcmConfigurablePlugin;
import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by armdev on 3/27/15.
 */
public class AlfrescoRecordsService implements InitializingBean, AcmConfigurablePlugin
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private Properties alfrescoRmaProperties;
    private Map<String, Object> alfrescoRmaPropertiesMap;
    private EcmFileDao ecmFileDao;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;
    private DeclareRecordService declareRecordService;
    private SetRecordMetadataService setRecordMetadataService;
    private FindFolderService findFolderService;
    private CreateOrFindRecordFolderService createOrFindRecordFolderService;
    private MoveToRecordFolderService moveToRecordFolderService;
    private CompleteRecordService completeRecordService;

    @Override
    public void afterPropertiesSet()
    {
        getEncryptablePropertyUtils().decryptProperties(alfrescoRmaProperties);

        getEncryptablePropertyUtils().decryptProperties(alfrescoRmaPropertiesMap);
    }

    public void declareAllContainerFilesAsRecords(Authentication auth, AcmContainer container, Date receiveDate, String recordFolderName)
    {
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

    public void declareAllFilesInFolderAsRecords(AcmCmisObjectList folder, AcmContainer container, Date receiveDate,
            String recordFolderName)
    {
        declareAsRecords(folder, container, receiveDate, recordFolderName);
    }

    public void declareAsRecords(AcmCmisObjectList files, AcmContainer container, Date receiveDate, String recordFolderName)
    {
        String originatorOrg = getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG,
                AlfrescoRmaPluginConstants.DEFAULT_ORIGINATOR_ORG);

        try
        {
            for (AcmCmisObject file : files.getChildren())
            {
                declareFileAsRecord(container, receiveDate, recordFolderName, originatorOrg, file.getModifier(), file.getCmisObjectId(),
                        file.getStatus(), file.getObjectId());
            }

        }
        catch (AlfrescoServiceException e)
        {
            log.error("Could not declare records: {}", e.getMessage(), e);
        }
    }

    protected void declareFileAsRecord(AcmContainer container, Date receiveDate, String recordFolderName, String originatorOrg,
            String originator, String cmisObjectId, String objectStatus, Long ecmFileId) throws AlfrescoServiceException
    {
        if (!((EcmFileConstants.RECORD).equals(objectStatus)))
        {
            declareRecord(cmisObjectId);

            writeRecordMetadata(receiveDate, originatorOrg, cmisObjectId, originator);

            Folder categoryFolder = findFolder(container.getContainerObjectType());

            String recordFolderId = createOrFindRecordFolder(recordFolderName, categoryFolder);

            log.debug("recordFolderId: {}", recordFolderId);

            moveToRecordFolder(recordFolderId, cmisObjectId);

            completeRecord(cmisObjectId);

            setFileStatusAsRecord(ecmFileId);
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

    protected String createOrFindRecordFolder(String recordFolderName, Folder folder) throws AlfrescoServiceException
    {
        Map<String, Object> findRecordFolderContext = new HashMap<>();
        findRecordFolderContext.put("parentFolder", folder);
        findRecordFolderContext.put("recordFolderName", recordFolderName);
        return getCreateOrFindRecordFolderService().service(findRecordFolderContext);
    }

    protected Folder findFolder(String containerObjectType) throws AlfrescoServiceException
    {
        // find the category folder
        Map<String, Object> findCategoryFolderContext = new HashMap<>();
        findCategoryFolderContext.put("objectType", containerObjectType);
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
                if (log.isDebugEnabled())
                {
                    log.debug("File with ID : " + ecmFile.getFileId() + " Status is changed to " + EcmFileConstants.RECORD);
                }
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("File with id: " + fileId + " does not exists - " + e.getMessage());
            }
        }
    }

    public boolean checkIntegrationEnabled(String integrationPointKey)
    {
        String integrationEnabledKey = "alfresco.rma.integration.enabled";

        Properties rmaProps = getAlfrescoRmaProperties();

        return "true".equals(rmaProps.getProperty(integrationEnabledKey, "true"))
                && "true".equals(rmaProps.getProperty(integrationPointKey, "true"));
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public Properties getAlfrescoRmaProperties()
    {
        return alfrescoRmaProperties;
    }

    public void setAlfrescoRmaProperties(Properties alfrescoRmaProperties)
    {
        this.alfrescoRmaProperties = alfrescoRmaProperties;

        if (alfrescoRmaProperties != null)
        {
            Map<String, Object> stringObjectMap = new HashMap<>();
            alfrescoRmaProperties.entrySet().stream().filter((entry) -> entry.getKey() != null)
                    .forEach((entry) -> stringObjectMap.put((String) entry.getKey(), entry.getValue()));
            setAlfrescoRmaPropertiesMap(stringObjectMap);
        }
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
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

    public CreateOrFindRecordFolderService getCreateOrFindRecordFolderService()
    {
        return createOrFindRecordFolderService;
    }

    public void setCreateOrFindRecordFolderService(CreateOrFindRecordFolderService createOrFindRecordFolderService)
    {
        this.createOrFindRecordFolderService = createOrFindRecordFolderService;
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

    public Map<String, Object> getAlfrescoRmaPropertiesMap()
    {
        return alfrescoRmaPropertiesMap;
    }

    public void setAlfrescoRmaPropertiesMap(Map<String, Object> alfrescoRmaPropertiesMap)
    {
        this.alfrescoRmaPropertiesMap = alfrescoRmaPropertiesMap;
    }

    @Override
    public boolean isEnabled()
    {
        return Boolean.parseBoolean(alfrescoRmaProperties.getProperty("alfresco_rma_integration_enabled", "false"));
    }

    @Override
    public String getName()
    {
        return AlfrescoRmaPluginConstants.RMA_PLUGIN;
    }
}
