package com.armedia.acm.services.email.receiver.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.services.email.receiver.modal.EmailReceiverConfiguration;

import org.springframework.security.core.Authentication;

public interface EmailReceiverConfigurationService
{

    void writeConfiguration(EmailReceiverConfiguration emailReceiverConfiguration, Authentication authentication)
            throws AcmEncryptionException;

    EmailReceiverConfiguration readConfiguration() throws AcmEncryptionException;
}
