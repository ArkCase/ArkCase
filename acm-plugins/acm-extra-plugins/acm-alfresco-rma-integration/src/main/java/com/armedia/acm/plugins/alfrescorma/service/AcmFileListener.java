package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileListener implements ApplicationListener<EcmFileAddedEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private MuleClient muleClient;

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

        AcmRecord record = new AcmRecord();
        record.setEcmFileId(ecmFileAddedEvent.getEcmFileId());

        String containerType = ecmFileAddedEvent.getSource().getContainer().getContainerObjectType();
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

        record.setOriginator(ecmFileAddedEvent.getUserId());
        record.setPublishedDate(new Date());
        record.setReceivedDate(ecmFileAddedEvent.getEventDate());
        record.setRecordFolder(ecmFileAddedEvent.getParentObjectName());

        Map<String, Object> messageProperties = getAlfrescoRecordsService().getRmaMessageProperties();


        try
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("sending JMS message.");
            }
            getMuleClient().dispatch(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT, record, messageProperties);
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
