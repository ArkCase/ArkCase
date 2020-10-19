package com.armedia.acm.services.email.service;

import com.armedia.acm.service.MimeMessageParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.util.List;
import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class ForwardedAsAttachmentEmailExtractor implements OriginalEmailExtractorStrategy
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     *
     * Finds and returns the original email if one is sent as an attachment
     *
     * @param message
     *            Email message
     * @return Original forwarded email or empty optional
     */
    @Override
    public Optional<Message> getForwardedMessage(Message message)
    {
        try
        {
            List<Part> attachments = MimeMessageParser.getAttachments(message);

            if (MimeMessageParser.hasForwardedEmailAsAttachment(attachments))
            {
                Part attachment = attachments.get(0);

                MimeMessage originalMessage = new MimeMessage(
                        Session.getDefaultInstance(System.getProperties()),
                        attachment.getInputStream());

                return Optional.of(originalMessage);
            }
        }
        catch (Exception e)
        {
            log.error("Couldn't read the email body and contents while extracting forwarded as attachment email!", e);
        }

        return Optional.empty();
    }
}
