package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.mule.api.MuleException;
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
public class AlfrescoRecordsService implements InitializingBean
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private Properties alfrescoRmaProperties;
    private Map<String, Object> alfrescoRmaPropertiesMap;
    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        getEncryptablePropertyUtils().decryptProperties(alfrescoRmaProperties);

        getEncryptablePropertyUtils().decryptProperties(alfrescoRmaPropertiesMap);
    }

    public void declareAllContainerFilesAsRecords(Authentication auth, AcmContainer container, Date receiveDate,
                                                  String recordFolderName)
    {
        try
        {
            AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);
            declareAsRecords(files, container, receiveDate, recordFolderName);
        } catch (AcmListObjectsFailedException e)
        {
            log.error("Cannot finish Record Management Strategy for container " + container.getContainerObjectType() +
                    " " + container.getContainerObjectId(), e);
        }
    }

    public void declareAllFilesInFolderAsRecords(AcmCmisObjectList folder, AcmContainer container, Date receiveDate,
                                                 String recordFolderName)
    {
        declareAsRecords(folder, container, receiveDate, recordFolderName);
    }

    public void declareAsRecords(AcmCmisObjectList files, AcmContainer container, Date receiveDate,
                                 String recordFolderName)
    {

        for (AcmCmisObject file : files.getChildren())
        {
            if (!((EcmFileConstants.RECORD).equals(file.getStatus())))
            {
                AcmRecord record = new AcmRecord();

                String objectType = container.getContainerObjectType();
                log.info("Found object type : " + objectType);

                String propertyKey = AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + objectType;

                String categoryFolder = getAlfrescoRmaProperties().getProperty(propertyKey);
                log.info("Found category folder is : " + categoryFolder);

                if (categoryFolder == null || categoryFolder.trim().isEmpty())
                {
                    log.error("Unknown category folder for object type: " + objectType + "; will not declare any records");
                    return;
                }

                String originatorOrg = getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG);
                if (originatorOrg == null || originatorOrg.trim().isEmpty())
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
                    if (log.isTraceEnabled())
                    {
                        log.trace("Sending JMS message.");
                    }

                    getMuleContextManager().send(
                            AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, getAlfrescoRmaPropertiesMap());
                    setFileStatusAsRecord(file.getObjectId());
                    if (log.isTraceEnabled())
                    {
                        log.trace("Done");
                    }

                } catch (MuleException e)
                {
                    log.error("Could not create RMA folder: " + e.getMessage(), e);
                }
            }
        }
    }

    public void setFileStatusAsRecord(Long fileId)
    {
        try
        {
            EcmFile ecmFile = getEcmFileService().findById(fileId);
            if (null == ecmFile)
            {
                throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
            } else
            {
                ecmFile.setStatus(EcmFileConstants.RECORD);
                getEcmFileDao().save(ecmFile);
                if (log.isDebugEnabled())
                {
                    log.debug("File with ID : " + ecmFile.getFileId() + " Status is changed to " + EcmFileConstants.RECORD);
                }
            }
        } catch (AcmObjectNotFoundException e)
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

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public void setAlfrescoRmaProperties(Properties alfrescoRmaProperties)
    {
        this.alfrescoRmaProperties = alfrescoRmaProperties;

        if (alfrescoRmaProperties != null)
        {
            Map<String, Object> stringObjectMap = new HashMap<>();
            alfrescoRmaProperties.
                    entrySet().
                    stream().
                    filter((entry) -> entry.getKey() != null).
                    forEach((entry) -> stringObjectMap.put((String) entry.getKey(), entry.getValue()));
            setAlfrescoRmaPropertiesMap(stringObjectMap);
        }
    }

    public Properties getAlfrescoRmaProperties()
    {
        return alfrescoRmaProperties;
    }


    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public Map<String, Object> getAlfrescoRmaPropertiesMap()
    {
        return alfrescoRmaPropertiesMap;
    }

    protected void setAlfrescoRmaPropertiesMap(Map<String, Object> alfrescoRmaPropertiesMap)
    {
        this.alfrescoRmaPropertiesMap = alfrescoRmaPropertiesMap;
    }


    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}
