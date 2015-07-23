package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileDeclareRequestListener implements ApplicationListener<EcmFileDeclareRequestEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private MuleContextManager muleContextManager;
    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;

    @Override
    public void onApplicationEvent(EcmFileDeclareRequestEvent ecmFileDeclareRequestEvent)
    {
        boolean proceed = getAlfrescoRecordsService().checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_DECLARE_REQUEST_INTEGRATION_KEY);

        if ( !proceed )
        {
            return;
        }

        if ( ! ecmFileDeclareRequestEvent.isSucceeded() )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Returning - file declaration request was not successful");
            }
            return;
        }

        AcmRecord record = new AcmRecord();
        record.setEcmFileId(ecmFileDeclareRequestEvent.getEcmFileId());

        String containerType = ecmFileDeclareRequestEvent.getSource().getContainer().getContainerObjectType();
        String categoryFolder = getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty(
                AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + containerType);
        if ( categoryFolder == null )
        {
            log.error("Cannot declare record for this file since the container object type {} is unknown", containerType);
            return;
        }

        record.setCategoryFolder(categoryFolder);
        record.setOriginatorOrg(getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty(
                AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG));

        record.setOriginator(ecmFileDeclareRequestEvent.getUserId());
        record.setPublishedDate(new Date());
        record.setReceivedDate(ecmFileDeclareRequestEvent.getEventDate());
        record.setRecordFolder(ecmFileDeclareRequestEvent.getParentObjectName());

        Map<String, Object> messageProperties = getAlfrescoRecordsService().getRmaMessageProperties();

        try
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("sending JMS message.");
            }
            getMuleContextManager().dispatch(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, messageProperties);
            ecmFileDeclareRequestEvent.getSource().setStatus(EcmFileConstants.RECORD);
            getEcmFileDao().save(ecmFileDeclareRequestEvent.getSource());
            log.info("File with ID: " + ecmFileDeclareRequestEvent.getSource().getId() + " declared as RECORD");
            if ( log.isTraceEnabled() )
            {
                log.trace("done");
            }
        }
        catch (MuleException e)
        {
            log.error("Could not create RMA folder: " + e.getMessage(), e);
        }
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }

}
