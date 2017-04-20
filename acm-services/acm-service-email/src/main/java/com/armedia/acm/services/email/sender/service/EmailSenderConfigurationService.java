package com.armedia.acm.services.email.sender.service;

import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

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

    public void writeConfiguration(EmailSenderConfiguration configuration)
    {

        Properties emailSenderProperties = new Properties();

        emailSenderProperties.put(EmailSenderConfigurationProperties.SENDER_HOST, configuration.getHost());
        emailSenderProperties.put(EmailSenderConfigurationProperties.SENDER_PORT, configuration.getPort().toString());
        emailSenderProperties.put(EmailSenderConfigurationProperties.SENDER_ENCRYPTION, configuration.getEncryption());
        emailSenderProperties.put(EmailSenderConfigurationProperties.SENDER_TYPE, configuration.getType());
        emailSenderProperties.put(EmailSenderConfigurationProperties.USERNAME, configuration.getUsername());
        emailSenderProperties.put(EmailSenderConfigurationProperties.PASSWORD, configuration.getPassword());
        emailSenderProperties.put(EmailSenderConfigurationProperties.USER_FROM, configuration.getUserFrom());
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_SENDING, Boolean.toString(configuration.isAllowSending()));
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_ATTACHMENTS,
                Boolean.toString(configuration.isAllowAttachments()));
        emailSenderProperties.put(EmailSenderConfigurationProperties.ALLOW_HYPERLINKS,
                Boolean.toString(configuration.isAllowHyperlinks()));

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try
        {
            emailSenderProperties.store(new FileOutputStream(emailSenderPropertiesResource.getFile()),
                    String.format("Updated from ", getClass().getName()));
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
            case EmailSenderConfigurationProperties.SENDER_HOST:
                emailSenderConfiguration.setHost(propertyValue);
                break;
            case EmailSenderConfigurationProperties.SENDER_PORT:
                emailSenderConfiguration.setPort(Integer.valueOf(propertyValue));
                break;
            case EmailSenderConfigurationProperties.SENDER_ENCRYPTION:
                emailSenderConfiguration.setEncryption(propertyValue);
                break;
            case EmailSenderConfigurationProperties.SENDER_TYPE:
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
            case EmailSenderConfigurationProperties.ALLOW_SENDING:
                emailSenderConfiguration.setAllowSending(Boolean.valueOf(propertyValue));
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
