package com.armedia.acm.services.email.service;

import javax.mail.Message;

import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public interface OriginalEmailExtractorStrategy
{

    /**
     * 
     * Get the forwarded message from the contents, headers or attachments of an email
     * 
     * @param message
     *            Email message
     * @return The original email if the extraction was successful
     */
    Optional<Message> getForwardedMessage(Message message);

}
