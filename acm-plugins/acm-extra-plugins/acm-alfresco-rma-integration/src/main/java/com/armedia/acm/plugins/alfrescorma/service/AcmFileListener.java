package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileListener implements ApplicationListener<EcmFileAddedEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(EcmFileAddedEvent ecmFileAddedEvent)
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a file created event; file id: '" + ecmFileAddedEvent.getObjectId() + "'");
        }

        if ( ! ecmFileAddedEvent.isSucceeded() )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Returning - file creation was not successful");
            }

            return;
        }

        AcmRecord record = new AcmRecord();
        record.setEcmFileId(ecmFileAddedEvent.getEcmFileId());

        //TODO: parameterize these two
        record.setCategoryFolder("Complaints");
        record.setOriginatorOrg("Armedia LLC");

        record.setOriginator(ecmFileAddedEvent.getUserId());
        record.setPublishedDate(new Date());
        record.setReceivedDate(ecmFileAddedEvent.getEventDate());
        record.setRecordFolder(ecmFileAddedEvent.getParentObjectName());


        try
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("sending JMS message.");
            }
            getMuleClient().dispatch("jms://rmaRecord.in", record, null);
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
