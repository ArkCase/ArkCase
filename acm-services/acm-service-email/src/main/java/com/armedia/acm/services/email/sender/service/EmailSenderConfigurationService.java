package com.armedia.acm.services.email.sender.service;

import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;

import org.springframework.security.core.Authentication;

/**
 * @author sasko.tanaskoski
 *
 */
public interface EmailSenderConfigurationService
{
    void writeConfiguration(EmailSenderConfiguration configuration, Authentication auth);

    EmailSenderConfiguration readConfiguration();
}
