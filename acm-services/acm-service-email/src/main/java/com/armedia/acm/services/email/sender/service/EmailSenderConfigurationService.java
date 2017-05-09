package com.armedia.acm.services.email.sender.service;

import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author sasko.tanaskoski
 *
 */
public class EmailSenderConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource emailSenderPropertiesResource;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void writeConfiguration(EmailSenderConfiguration configuration, Authentication auth)
    {

        Properties emailSenderProperties = new Properties();

        emailSenderProperties.put(EmailSenderConfigurationConstants.HOST, configuration.getHost());
        emailSenderProperties.put(EmailSenderConfigurationConstants.PORT, configuration.getPort().toString());
        emailSenderProperties.put(EmailSenderConfigurationConstants.ENCRYPTION, configuration.getEncryption());
        emailSenderProperties.put(EmailSenderConfigurationConstants.TYPE, configuration.getType());
        emailSenderProperties.put(EmailSenderConfigurationConstants.USERNAME, configuration.getUsername());
        emailSenderProperties.put(EmailSenderConfigurationConstants.PASSWORD, configuration.getPassword());
        emailSenderProperties.put(EmailSenderConfigurationConstants.USER_FROM, configuration.getUserFrom());
        emailSenderProperties.put(EmailSenderConfigurationConstants.ALLOW_DOCUMENTS, Boolean.toString(configuration.isAllowDocuments()));
        emailSenderProperties.put(EmailSenderConfigurationConstants.ALLOW_ATTACHMENTS,
                Boolean.toString(configuration.isAllowAttachments()));
        emailSenderProperties.put(EmailSenderConfigurationConstants.ALLOW_HYPERLINKS, Boolean.toString(configuration.isAllowHyperlinks()));

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try
        {
            emailSenderProperties.store(new FileOutputStream(emailSenderPropertiesResource.getFile()),
                    String.format("Updated by %s", auth.getName()));
        } catch (IOException e)
        {
            log.error("Could not write properties to {} file.", emailSenderPropertiesResource.getFilename());
        } finally
        {
            writeLock.unlock();
        }
    }

    public EmailSenderConfiguration readConfiguration()
    {
        EmailSenderConfiguration emailSenderConfiguration = new EmailSenderConfiguration();

        Properties emailSenderProperties = loadProperties();

        Set<String> propertyNames = emailSenderProperties.stringPropertyNames();

        for (String propertyName : propertyNames)
        {

            String propertyValue = emailSenderProperties.getProperty(propertyName);
            switch (propertyName)
            {
            case EmailSenderConfigurationConstants.HOST:
                emailSenderConfiguration.setHost(propertyValue);
                break;
            case EmailSenderConfigurationConstants.PORT:
                emailSenderConfiguration.setPort(Integer.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationConstants.ENCRYPTION:
                emailSenderConfiguration.setEncryption(propertyValue);
                break;
            case EmailSenderConfigurationConstants.TYPE:
                emailSenderConfiguration.setType(propertyValue);
                break;
            case EmailSenderConfigurationConstants.USERNAME:
                emailSenderConfiguration.setUsername(propertyValue);
                break;
            case EmailSenderConfigurationConstants.PASSWORD:
                emailSenderConfiguration.setPassword(propertyValue);
                break;
            case EmailSenderConfigurationConstants.USER_FROM:
                emailSenderConfiguration.setUserFrom(propertyValue);
                break;
            case EmailSenderConfigurationConstants.ALLOW_DOCUMENTS:
                emailSenderConfiguration.setAllowDocuments(Boolean.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationConstants.ALLOW_ATTACHMENTS:
                emailSenderConfiguration.setAllowAttachments(Boolean.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationConstants.ALLOW_HYPERLINKS:
                emailSenderConfiguration.setAllowHyperlinks(Boolean.valueOf(propertyValue));
            }
        }

        return emailSenderConfiguration;
    }

    private Properties loadProperties()
    {
        Properties emailSenderProperties = new Properties();
        Lock readLock = lock.readLock();
        readLock.lock();
        try
        {
            emailSenderProperties.load(emailSenderPropertiesResource.getInputStream());
        } catch (IOException e)
        {
            log.error("Could not read properties from {} file.", emailSenderPropertiesResource.getFilename());
        } finally
        {
            readLock.unlock();

        }
        return emailSenderProperties;
    }

    public boolean validateSmtpConfiguration(EmailSenderConfiguration configuration)
    {
        boolean validation = false;
        AuthenticatingSMTPClient authenticatingSmtpClient = null;

        try
        {
            if (configuration.getEncryption().equals("starttls") || configuration.getEncryption().equals("off"))
            {
                authenticatingSmtpClient = new AuthenticatingSMTPClient();
            } else
            {
                authenticatingSmtpClient = new AuthenticatingSMTPClient("TLS", true);
            }

            authenticatingSmtpClient.setConnectTimeout(3 * 1000);
            authenticatingSmtpClient.connect(configuration.getHost(), configuration.getPort());
            if (!isPositiveReply(authenticatingSmtpClient))
            {
                throw new Exception("Rejected connection");
            }

            if (configuration.getEncryption().equals("starttls"))
            {
                authenticatingSmtpClient.execTLS();
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    throw new Exception("STARTTLS not supported");
                }

                validation = true;
            }

            if (configuration.getEncryption().equals("starttls") || configuration.getEncryption().equals("ssl-tls"))
            {
                authenticatingSmtpClient.ehlo("");
                authenticatingSmtpClient.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, configuration.getUsername(),
                        configuration.getPassword());
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    throw new Exception("Rejected login");
                }
            }

            validation = true;

        } catch (Exception e)
        {
            log.error("SMTP Error, {}", e.getMessage());
            if (authenticatingSmtpClient.isConnected())
            {
                try
                {
                    authenticatingSmtpClient.disconnect();
                } catch (IOException ioe)
                {
                }
            }
        }

        return validation;
    }

    private boolean isPositiveReply(SMTPClient smtpClient)
    {
        if (SMTPReply.isPositiveCompletion(smtpClient.getReplyCode()))
        {
            log.info("SMTP Positive Reply {} {}", smtpClient.getReplyCode(), smtpClient.getReplyString());
            return true;
        } else
        {
            log.error("SMTP Error Reply {} {}", smtpClient.getReplyCode(), smtpClient.getReplyString());
            return false;
        }
    }

    /**
     * @param emailSenderPropertiesResource
     *            the emailSenderPropertiesResource to set
     */
    public void setEmailSenderPropertiesResource(Resource emailSenderPropertiesResource)
    {
        this.emailSenderPropertiesResource = emailSenderPropertiesResource;
    }

}
