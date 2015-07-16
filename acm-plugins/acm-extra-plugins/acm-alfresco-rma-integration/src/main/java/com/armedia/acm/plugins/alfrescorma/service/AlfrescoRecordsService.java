package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by armdev on 3/27/15.
 */
public class AlfrescoRecordsService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private Properties alfrescoRmaProperties;
    private EcmFileDao ecmFileDao;

    public void declareAllContainerFilesAsRecords(Authentication auth, AcmContainer container, Date receiveDate,
                                                  String recordFolderName)
    {
        try
        {
            AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);
            declareAsRecords(files,container,receiveDate,recordFolderName);
        }
        catch (AcmListObjectsFailedException e)
        {
            log.error("Cannot finish Record Management Strategy for container " + container.getContainerObjectType() +
                    " " + container.getContainerObjectId(), e);
        }
    }

    public void declareAllFilesInFolderAsRecords(AcmCmisObjectList folder,AcmContainer container, Date receiveDate,
                                                  String recordFolderName)
    {
        declareAsRecords(folder,container,receiveDate,recordFolderName);
    }

    public void declareAsRecords(AcmCmisObjectList files,AcmContainer container, Date receiveDate,
                                 String recordFolderName){

        Map<String, Object> messageProperties = getRmaMessageProperties();

        for ( AcmCmisObject file : files.getChildren() )
        {
            AcmRecord record = new AcmRecord();

            String objectType = container.getContainerObjectType();
            String propertyKey = AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + objectType;

            String categoryFolder = getAlfrescoRmaProperties().getProperty(propertyKey);

            if ( categoryFolder == null || categoryFolder.trim().isEmpty() )
            {
                log.error("Unknown category folder for object type: " + objectType + "; will not declare any records");
                return;
            }

            String originatorOrg = getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG);
            if ( originatorOrg == null || originatorOrg.trim().isEmpty() )
            {
                originatorOrg = AlfrescoRmaPluginConstants.DEFAULT_ORIGINATOR_ORG;
            }

            record.setEcmFileId(file.getCmisObjectId());
            record.setCategoryFolder(categoryFolder);
            record.setOriginatorOrg(originatorOrg);
            record.setOriginator(file.getModifier());
            record.setPublishedDate(new Date());
            record.setReceivedDate(receiveDate);
            record.setRecordFolder(recordFolderName);

            try
            {
                if ( log.isTraceEnabled() )
                {
                    log.trace("Sending JMS message.");
                }

                getMuleClient().dispatch(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, messageProperties);
                setFileStatusAsRecord(file.getObjectId());
                if ( log.isTraceEnabled() )
                {
                    log.trace("Done");
                }

            }
            catch (MuleException e)
            {
                log.error("Could not create RMA folder: " + e.getMessage(), e);
            }
        }
    }

    public void setFileStatusAsRecord(Long fileId){
        try{
            EcmFile ecmFile = getEcmFileService().findById(fileId);
            if(null == ecmFile){
                throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
            }
            else{
                ecmFile.setStatus(EcmFileConstants.RECORD);
                getEcmFileDao().save(ecmFile);
                if ( log.isDebugEnabled() ) {
                    log.debug("File with ID : " + ecmFile.getFileId() + " Status is changed to "+ EcmFileConstants.RECORD);
                }
            }
        }
        catch(AcmObjectNotFoundException e){
            if (log.isErrorEnabled()) {
                log.error("File with id: " + fileId + " does not exists - " + e.getMessage());
            }
        }
    }

    public Map<String, Object> getRmaMessageProperties()
    {
        String rmaModuleVersion = getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.RMA_MODULE_VERSION_KEY);
        return Collections.singletonMap("alfresco_rma_module_version", rmaModuleVersion);
    }

    public boolean checkIntegrationEnabled(String integrationPointKey)
    {
        String integrationEnabledKey = "alfresco.rma.integration.enabled";

        Properties rmaProps = getAlfrescoRmaProperties();

        return "true".equals(rmaProps.getProperty(integrationEnabledKey, "true")) &&
                "true".equals(rmaProps.getProperty(integrationPointKey, "true"));
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public MuleClient getMuleClient()
    {
        // Method body is overridden by Spring via 'lookup-method', so this method body is never called
        // when this class is used as a Spring bean
        return null;
    }

    public void setAlfrescoRmaProperties(Properties alfrescoRmaProperties)
    {
        this.alfrescoRmaProperties = alfrescoRmaProperties;
    }

    public Properties getAlfrescoRmaProperties()
    {
        return alfrescoRmaProperties;
    }


    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }

}
