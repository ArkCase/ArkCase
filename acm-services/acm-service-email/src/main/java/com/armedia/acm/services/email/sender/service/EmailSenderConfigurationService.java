package com.armedia.acm.services.email.sender.service;

import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationProperties;

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

        emailSenderProperties.put(EmailSenderConfigurationProperties.HOST, configuration.getHost());
        emailSenderProperties.put(EmailSenderConfigurationProperties.PORT, configuration.getPort().toString());
        emailSenderProperties.put(EmailSenderConfigurationProperties.ENCRYPTION, configuration.getEncryption());
        emailSenderProperties.put(EmailSenderConfigurationProperties.TYPE, configuration.getType());
        emailSenderProperties.put(EmailSenderConfigurationProperties.USERNAME, configuration.getUsername());
        emailSenderProperties.put(EmailSenderConfigurationProperties.PASSWORD, configuration.getPassword());
        emailSenderProperties.put(EmailSenderConfigurationProperties.USER_FROM, configuration.getUserFrom());
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_DOCUMENTS, Boolean.toString(configuration.isAllowDocuments()));
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_ATTACHMENTS,
                Boolean.toString(configuration.isAllowAttachments()));
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_HYPERLINKS, Boolean.toString(configuration.isAllowHyperlinks()));

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
            case EmailSenderConfigurationProperties.HOST:
                emailSenderConfiguration.setHost(propertyValue);
                break;
            case EmailSenderConfigurationProperties.PORT:
                emailSenderConfiguration.setPort(Integer.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationProperties.ENCRYPTION:
                emailSenderConfiguration.setEncryption(propertyValue);
                break;
            case EmailSenderConfigurationProperties.TYPE:
                emailSenderConfiguration.setType(propertyValue);
                break;
            case EmailSenderConfigurationProperties.USERNAME:
                emailSenderConfiguration.setUsername(propertyValue);
                break;
            case EmailSenderConfigurationProperties.PASSWORD:
                emailSenderConfiguration.setPassword(propertyValue);
                break;
            case EmailSenderConfigurationProperties.USER_FROM:
                emailSenderConfiguration.setUserFrom(propertyValue);
                break;
            case EmailSenderConfigurationProperties.ALLOW_DOCUMENTS:
                emailSenderConfiguration.setAllowDocuments(Boolean.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationProperties.ALLOW_ATTACHMENTS:
                emailSenderConfiguration.setAllowAttachments(Boolean.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationProperties.ALLOW_HYPERLINKS:
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

    /**
     * @param emailSenderPropertiesResource
     *            the emailSenderPropertiesResource to set
     */
    public void setEmailSenderPropertiesResource(Resource emailSenderPropertiesResource)
    {
        this.emailSenderPropertiesResource = emailSenderPropertiesResource;
    }

}
