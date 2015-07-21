package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecordFolder;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmComplaintFolderListener implements ApplicationListener<ComplaintCreatedEvent>
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private MuleContextManager muleContextManager;

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

        AcmRecordFolder folder = new AcmRecordFolder();
        folder.setFolderType("COMPLAINT");
        folder.setFolderName(complaintCreatedEvent.getComplaintNumber());

        Map<String, Object> messageProperties = getAlfrescoRecordsService().getRmaMessageProperties();

        try
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("sending JMS message.");
            }
            getMuleContextManager().dispatch(AlfrescoRmaPluginConstants.FOLDER_MULE_ENDPOINT, folder, messageProperties);
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
}
