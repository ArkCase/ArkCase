package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmComplaintFolderListener implements ApplicationListener<ComplaintCreatedEvent>
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

    @Override
    public void onApplicationEvent(ComplaintCreatedEvent complaintCreatedEvent)
    {
        boolean proceed = getAlfrescoRecordsService().checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_FOLDER_INTEGRATION_KEY);

        if ( !proceed )
        {
            return;
        }

        if ( ! complaintCreatedEvent.isSucceeded() )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Returning - complaint creation was not successful");
            }

            return;
        }

        try
        {
            String ticket = getAlfrescoRecordsService().getTicketService().service(null);
            CmisObject categoryFolder = getAlfrescoRecordsService().findCategoryFolder(ComplaintConstants.OBJECT_TYPE);
            getAlfrescoRecordsService().createOrFindRecordFolder(complaintCreatedEvent.getComplaintNumber(), ticket, categoryFolder);
        } catch (AlfrescoServiceException e)
        {
            log.error("Could not create record folder for complaint: {}", e.getMessage(), e);
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
