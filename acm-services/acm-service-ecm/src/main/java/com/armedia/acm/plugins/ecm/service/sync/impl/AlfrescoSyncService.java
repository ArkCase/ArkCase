package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;
import java.util.Map;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncService implements ApplicationEventPublisherAware
{
    private PropertyFileManager propertyFileManager;
    private AlfrescoAuditApplicationRestClient auditApplicationRestClient;
    private Map<String, EcmAuditResponseReader> auditApplications;
    private String auditApplicationLastAuditIdsFilename;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void queryAlfrescoAuditApplications()
    {
        for (Map.Entry<String, EcmAuditResponseReader> auditApplication : getAuditApplications().entrySet())
        {

            String lastAuditIdFetched = null;
            String applicationName = auditApplication.getKey();
            EcmAuditResponseReader reader = auditApplication.getValue();

            log.info("Starting Alfresco sync for audit application {}", applicationName);

            try
            {
                String lastAuditIdKey = applicationName + ".lastAuditId";

                lastAuditIdFetched = getPropertyFileManager().load(getAuditApplicationLastAuditIdsFilename(),
                        lastAuditIdKey, "0");
                long lastAuditId = Long.valueOf(lastAuditIdFetched);
                JSONObject auditEntries = getAuditApplicationRestClient().service(applicationName, lastAuditId);

                List<EcmEvent> events = reader.read(auditEntries);

                if (events != null && !events.isEmpty())
                {
                    log.info("Fetched {} audit records for audit application {}", applicationName);

                    getPropertyFileManager().store(lastAuditIdKey,
                            String.valueOf(events.get(events.size() - 1).getAuditId()), getAuditApplicationLastAuditIdsFilename());

                    for (EcmEvent e : events)
                    {
                        applicationEventPublisher.publishEvent(e);
                    }
                }


            } catch (AcmEncryptionException e)
            {
                log.error("Could not decrypt property {}.lastAuditId", applicationName, e);
            } catch (NumberFormatException e)
            {
                log.error("The last audit id {} should be a number, but it is not!", lastAuditIdFetched);
            } catch (Exception e)
            {
                log.error("Could not query Alfresco audit records for application {}", applicationName, e);
            }

        }

    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setAuditApplicationRestClient(AlfrescoAuditApplicationRestClient auditApplicationRestClient)
    {
        this.auditApplicationRestClient = auditApplicationRestClient;
    }

    public AlfrescoAuditApplicationRestClient getAuditApplicationRestClient()
    {
        return auditApplicationRestClient;
    }

    public void setAuditApplicationLastAuditIdsFilename(String auditApplicationLastAuditIdsFilename)
    {
        this.auditApplicationLastAuditIdsFilename = auditApplicationLastAuditIdsFilename;
    }

    public String getAuditApplicationLastAuditIdsFilename()
    {
        return auditApplicationLastAuditIdsFilename;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Map<String, EcmAuditResponseReader> getAuditApplications()
    {
        return auditApplications;
    }

    public void setAuditApplications(Map<String, EcmAuditResponseReader> auditApplications)
    {
        this.auditApplications = auditApplications;
    }
}
