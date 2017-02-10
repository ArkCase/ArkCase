package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileDeclareRequestEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileDeclareRequestListener implements ApplicationListener<EcmFileDeclareRequestEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

    @Override
    public void onApplicationEvent(EcmFileDeclareRequestEvent ecmFileDeclareRequestEvent)
    {
        boolean proceed = getAlfrescoRecordsService()
                .checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_DECLARE_REQUEST_INTEGRATION_KEY);

        if (!proceed)
        {
            return;
        }

        if (!ecmFileDeclareRequestEvent.isSucceeded())
        {
            log.trace("Returning - file declaration request was not successful");
        }

        try
        {
            getAlfrescoRecordsService().declareFileAsRecord(ecmFileDeclareRequestEvent.getSource().getContainer(),
                    ecmFileDeclareRequestEvent.getEventDate(), ecmFileDeclareRequestEvent.getParentObjectName(),
                    getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG),
                    ecmFileDeclareRequestEvent.getUserId(), ecmFileDeclareRequestEvent.getEcmFileId(),
                    ecmFileDeclareRequestEvent.getSource().getStatus(), ecmFileDeclareRequestEvent.getObjectId());

        }
        catch (AlfrescoServiceException e)
        {
            log.error("Could not declare file as record: {}", e.getMessage(), e);
        }
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

}
