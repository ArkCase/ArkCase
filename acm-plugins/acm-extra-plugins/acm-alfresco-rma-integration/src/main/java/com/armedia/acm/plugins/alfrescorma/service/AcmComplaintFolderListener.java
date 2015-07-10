package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecordFolder;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
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
    private MuleClient muleClient;

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
            getMuleClient().dispatch(AlfrescoRmaPluginConstants.FOLDER_MULE_ENDPOINT, folder, messageProperties);
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
}
