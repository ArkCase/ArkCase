package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileDeclareRequestListener implements ApplicationListener<EcmFileDeclareRequestEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private MuleClient muleClient;
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
            getMuleClient().dispatch(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, messageProperties);
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

    public MuleClient getMuleClient()
    {
        // Method body is overridden by Spring via 'lookup-method', so this method body is never called
        // when this class is used as a Spring bean.  But, when used as a non-Spring POJO, i.e. in unit tests,
        // then this is how the test gets to inject a mock client.
        return muleClient;
    }

    // this method used for unit testing.
    protected void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
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
