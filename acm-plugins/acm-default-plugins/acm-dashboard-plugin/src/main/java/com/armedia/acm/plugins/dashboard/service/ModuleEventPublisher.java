package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.module.ModuleConstants;
import com.armedia.acm.plugins.dashboard.model.module.ModuleCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class ModuleEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishModuleCreated(Module source, Authentication authentication, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a module name event. New Module Name Created Event.");

        String userId = authentication == null ? ModuleConstants.ON_BOOT_USER_NAME : authentication.getName();
        String ipAddr = ipAddress == null ? ModuleConstants.LOOPBACK_IP_ADDRESS : ipAddress;

        ModuleCreatedEvent moduleCreatedEvent = new ModuleCreatedEvent(source, userId);
        moduleCreatedEvent.setIpAddress(ipAddr);
        moduleCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(moduleCreatedEvent);
    }

    public ApplicationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
