package com.armedia.acm.services.email.smtp;

/*-
 * #%L
 * ACM Service: Email SMTP
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.email.model.EmailSenderConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.List;

public class AcmMailSender
{
    private JavaMailSenderImpl smtpMailSender;
    private JavaMailSenderImpl smtpsMailSender;
    private EmailSenderConfig emailConfig;
    private TrackOutgoingEmailService trackOutgoingEmailService;

    private static final Logger log = LogManager.getLogger(AcmMailSender.class);
    
    @Deprecated
    public void sendEmail(String recipient, String subject, String body) throws Exception
    {
        JavaMailSender mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(emailConfig.getUserFrom());
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(helper.getMimeMessage());
    }

    public void sendEmail(String recipient, String subject, String body, String parentType, String parentId) throws Exception
    {
        JavaMailSender mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(emailConfig.getUserFrom());
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(helper.getMimeMessage());
        trackOutgoingEmailService.trackEmail(mimeMessage, recipient, subject, parentType, parentId, null);
    }
    
    @Deprecated
    public void sendMultipartEmail(String recipient, String subject, String body, List<InputStreamDataSource> attachments)
            throws Exception
    {
        JavaMailSender mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(emailConfig.getUserFrom());
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        attachments.forEach(attachment -> {
            try
            {
                helper.addAttachment(attachment.getName(), attachment);
            }
            catch (MessagingException e)
            {
                log.warn("Failed to add attachment [{}]. Cause: {}.", attachment.getName(), e.getMessage(), e);
            }
        });
        mailSender.send(helper.getMimeMessage());
    }

    public void sendMultipartEmail(String recipient, String subject, String body, List<InputStreamDataSource> attachments, String parentType, String parentId)
            throws Exception
    {
        JavaMailSender mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(emailConfig.getUserFrom());
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        attachments.forEach(attachment -> {
            try
            {
                helper.addAttachment(attachment.getName(), attachment);
            }
            catch (MessagingException e)
            {
                log.warn("Failed to add attachment [{}]. Cause: {}.", attachment.getName(), e.getMessage(), e);
            }
        });
        mailSender.send(helper.getMimeMessage());
        trackOutgoingEmailService.trackEmail(mimeMessage, recipient, subject, parentType, parentId, attachments);
    }

    public EmailSenderConfig getEmailConfig()
    {
        return emailConfig;
    }

    public void setEmailConfig(EmailSenderConfig emailConfig)
    {
        this.emailConfig = emailConfig;
    }

    public void setSmtpMailSender(JavaMailSenderImpl smtpMailSender)
    {
        this.smtpMailSender = smtpMailSender;
    }

    public void setSmtpsMailSender(JavaMailSenderImpl smtpsMailSender)
    {
        this.smtpsMailSender = smtpsMailSender;
    }

    /**
     * Returns preconfigured mail sender instance depending on whether we use encryption or not.
     * The host, port, username and password values are set each time the sender is required since
     * the mail configuration can be changed on runtime.
     *
     * NOTE: The method is not thread safe but it is correct since EmailSenderConfig is the same for all threads.
     *
     * @return JavaMailSenderImpl
     */
    private JavaMailSenderImpl getMailSender()
    {
        log.debug("Mail sender configured with: encryption: [{}], host: [{}], port: [{}], username: [{}]",
                emailConfig.getEncryption(), emailConfig.getHost(), emailConfig.getPort(), emailConfig.getUsername());

        if (emailConfig.getEncryption().equals("starttls"))
        {
            smtpsMailSender.setHost(emailConfig.getHost());
            smtpsMailSender.setPort(emailConfig.getPort());
            smtpsMailSender.setUsername(emailConfig.getUsername());
            smtpsMailSender.setPassword(emailConfig.getPassword());
            return smtpsMailSender;
        }
        else
        {
            smtpMailSender.setHost(emailConfig.getHost());
            smtpMailSender.setPort(emailConfig.getPort());
            smtpMailSender.setUsername(emailConfig.getUsername());
            smtpMailSender.setPassword(emailConfig.getPassword());
            return smtpMailSender;
        }
    }

    public TrackOutgoingEmailService getTrackOutgoingEmailService() 
    {
        return trackOutgoingEmailService;
    }

    public void setTrackOutgoingEmailService(TrackOutgoingEmailService trackOutgoingEmailService) 
    {
        this.trackOutgoingEmailService = trackOutgoingEmailService;
    }
}
