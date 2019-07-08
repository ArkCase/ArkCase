package com.armedia.acm.services.email.smtp;

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

    private static final Logger log = LogManager.getLogger(AcmMailSender.class);

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
                log.warn("Failed to add attachment [{}]. Cause: {}.", attachment.getName(), e.getMessage());
            }
        });
        mailSender.send(helper.getMimeMessage());
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
     * Returns preconfigured mail sender instance depending on weather we use encryption or not.
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
}
