package com.armedia.acm.audit;

import com.armedia.commons.audit.AuditorFactory;
import com.armedia.commons.audit.AuditorInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Launch the audit queues and sinks when the application starts; close the audit system
 * when the application shuts down.
 */
public class AuditConfigurer implements ApplicationListener<ContextStartedEvent>
{

    private String auditConfigFilename;
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Load the audit configuration after the application context has started.  Since the audit config may
     * depend on beans loaded in other files we can't use the init-method bean setting.
     *
     * @param contextStartedEvent not used, this method just tells us when the full context has started
     */
    @Override
    public void onApplicationEvent(ContextStartedEvent contextStartedEvent)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Initializing audit configuration from " + getAuditConfigFilename());
        }
        Resource config = new ClassPathResource(getAuditConfigFilename());

        try
        {
            AuditorFactory.initialize(config.getURL());
        }
        catch ( IOException | AuditorInitializationException ex)
        {
            throw new IllegalStateException("Could not initialize audit configuration: " + ex.getMessage(), ex);
        }
    }

    public void closeBean()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Shutting down audit configuration");
        }
        AuditorFactory.close();
    }

    public String getAuditConfigFilename()
    {
        return auditConfigFilename;
    }

    public void setAuditConfigFilename(String auditConfigFilename)
    {
        this.auditConfigFilename = auditConfigFilename;
    }
}
