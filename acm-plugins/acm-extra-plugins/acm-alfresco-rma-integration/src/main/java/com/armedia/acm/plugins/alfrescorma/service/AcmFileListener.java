package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileListener implements ApplicationListener<EcmFileAddedEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent ecmFileAddedEvent)
    {
        boolean proceed = getAlfrescoRecordsService().checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_INTEGRATION_KEY);

        if ( !proceed )
        {
            return;
        }

        if ( ! ecmFileAddedEvent.isSucceeded() )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Returning - file creation was not successful");
            }

            return;
        }

        try
        {
            String ticket = getAlfrescoRecordsService().getTicketService().service(null);
            getAlfrescoRecordsService().declareFileAsRecord(ecmFileAddedEvent.getSource().getContainer(),
                    ecmFileAddedEvent.getEventDate(),
                    ecmFileAddedEvent.getParentObjectName(),
                    getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG),
                    ecmFileAddedEvent.getUserId(),
                    ticket,
                    ecmFileAddedEvent.getEcmFileId(),
                    ecmFileAddedEvent.getSource().getStatus(),
                    ecmFileAddedEvent.getObjectId());

        } catch (AlfrescoServiceException e)
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
