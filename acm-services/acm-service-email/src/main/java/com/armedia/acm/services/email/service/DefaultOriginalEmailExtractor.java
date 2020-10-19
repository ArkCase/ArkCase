package com.armedia.acm.services.email.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;

import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class DefaultOriginalEmailExtractor implements OriginalEmailExtractorStrategy
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     *
     * Default fallback strategy, returns the original message
     *
     * @param message
     *            Email message
     * @return Original or forwarded email
     */
    @Override
    public Optional<Message> getForwardedMessage(Message message)
    {
        log.info("No forwarded email found, the sent mail will be used.");
        return Optional.of(message);
    }
}
