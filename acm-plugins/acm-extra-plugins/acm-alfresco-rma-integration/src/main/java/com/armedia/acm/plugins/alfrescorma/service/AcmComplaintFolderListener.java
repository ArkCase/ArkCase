package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecordFolder;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmComplaintFolderListener implements ApplicationListener<ComplaintCreatedEvent>
{
    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ComplaintCreatedEvent complaintCreatedEvent)
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a complaint created event; complaint id: '" + complaintCreatedEvent.getObjectId() + "'");
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

        try
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("sending JMS message.");
            }
            getMuleClient().dispatch("jms://rmaFolder.in", folder, null);
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
        return null;  // this method should be overridden by Spring method injection
    }


}
