package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.mule.api.MuleException;
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
    private MuleContextManager muleContextManager;

    public void declareAllContainerFilesAsRecords(Authentication auth, AcmContainer container, Date receiveDate,
                                                  String recordFolderName)
    {
        Map<String, Object> messageProperties = getRmaMessageProperties();

        try
        {
            AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);

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

                    getMuleContextManager().dispatch(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, messageProperties);

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
        catch (AcmListObjectsFailedException e)
        {
            log.error("Cannot finish Record Management Strategy for container " + container.getContainerObjectType() +
                    " " + container.getContainerObjectId(), e);
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
    }

    public Properties getAlfrescoRmaProperties()
    {
        return alfrescoRmaProperties;
    }
}
