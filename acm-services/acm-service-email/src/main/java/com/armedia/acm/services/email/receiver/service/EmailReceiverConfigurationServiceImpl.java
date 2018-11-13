package com.armedia.acm.services.email.receiver.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;
import com.armedia.acm.services.email.receiver.modal.EmailReceiverConfiguration;
import com.armedia.acm.services.email.receiver.modal.EmailReceiverConfigurationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EmailReceiverConfigurationServiceImpl implements EmailReceiverConfigurationService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource emailReceiverPropertiesResource;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private AcmEncryptablePropertyUtilsImpl acmEncryptablePropertyUtils;

    @Override
    public void writeConfiguration(EmailReceiverConfiguration emailReceiverConfiguration, Authentication authentication)
            throws AcmEncryptionException
    {

        EmailReceiverConfiguration configuration = readConfiguration();
        Properties emailReceiverProperties = new Properties();

        emailReceiverProperties.put(EmailReceiverConfigurationConstants.SHOULD_DELETE_MESSAGE,
                Boolean.toString(configuration.isShouldDeleteMessage()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.SHOULD_MARK_MESSAGES_AS_READ,
                Boolean.toString(configuration.isShouldMarkMessagesAsRead()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.MAX_MESSAGES_PER_POLL,
                Integer.toString(configuration.getMaxMessagePerPoll()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.FIXED_RATE, Long.toString(configuration.getFixedRate()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.EMAIL, emailReceiverConfiguration.getUser());
        if (emailReceiverConfiguration.getPassword() == null)
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD,
                    acmEncryptablePropertyUtils.encryptPropertyValue(configuration.getPassword()));
        }
        else
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD,
                    acmEncryptablePropertyUtils.encryptPropertyValue(emailReceiverConfiguration.getPassword()));
        }
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.PROTOCOL, configuration.getProtocol());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.FETCH_FOLDER, configuration.getFetchFolder());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.HOST, configuration.getHost());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.PORT, Integer.toString(configuration.getPort()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.DEBUG, Boolean.toString(configuration.isDebug()));

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try (OutputStream propertyOutputStream = new FileOutputStream(emailReceiverPropertiesResource.getFile()))
        {
            emailReceiverProperties.store(propertyOutputStream, String.format("Updated by %s", authentication.getName()));
        }
        catch (IOException e)
        {
            log.error("Could not write properties to [{}] file.", emailReceiverPropertiesResource.getFilename());
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public EmailReceiverConfiguration readConfiguration()
    {
        EmailReceiverConfiguration emailReceiverConfiguration = new EmailReceiverConfiguration();

        Properties emailReceiverProperties = loadProperties();

        Set<String> propertyNames = emailReceiverProperties.stringPropertyNames();

        for (String propertyName : propertyNames)
        {

            String propertyValue = emailReceiverProperties.getProperty(propertyName);

            switch (propertyName)
            {

            case EmailReceiverConfigurationConstants.SHOULD_DELETE_MESSAGE:
                emailReceiverConfiguration.setShouldDeleteMessage(Boolean.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.SHOULD_MARK_MESSAGES_AS_READ:
                emailReceiverConfiguration.setShouldMarkMessagesAsRead(Boolean.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.MAX_MESSAGES_PER_POLL:
                emailReceiverConfiguration.setMaxMessagePerPoll(Integer.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.FIXED_RATE:
                emailReceiverConfiguration.setFixedRate(Long.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.EMAIL:
                emailReceiverConfiguration.setUser(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.PASSWORD:
                emailReceiverConfiguration.setPassword(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.PROTOCOL:
                emailReceiverConfiguration.setProtocol(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.FETCH_FOLDER:
                emailReceiverConfiguration.setFetchFolder(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.HOST:
                emailReceiverConfiguration.setHost(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.PORT:
                emailReceiverConfiguration.setPort(Integer.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.DEBUG:
                emailReceiverConfiguration.setDebug(Boolean.valueOf(propertyValue));
            }
        }

        return emailReceiverConfiguration;
    }

    private Properties loadProperties()
    {
        Properties emailReceiverProperties = new Properties();

        Lock readLock = lock.readLock();
        readLock.lock();

        try (InputStream propertyInputStream = emailReceiverPropertiesResource.getInputStream())
        {
            emailReceiverProperties.load(propertyInputStream);
        }
        catch (IOException e)
        {
            log.error("Could not read properties from [{}] file.", emailReceiverPropertiesResource.getFilename());
        }
        finally
        {
            readLock.unlock();

        }

        return emailReceiverProperties;
    }

    public void setEmailReceiverPropertiesResource(Resource emailReceiverPropertiesResource)
    {
        this.emailReceiverPropertiesResource = emailReceiverPropertiesResource;
    }

    public AcmEncryptablePropertyUtilsImpl getAcmEncryptablePropertyUtils()
    {
        return acmEncryptablePropertyUtils;
    }

    public void setAcmEncryptablePropertyUtils(AcmEncryptablePropertyUtilsImpl acmEncryptablePropertyUtils)
    {
        this.acmEncryptablePropertyUtils = acmEncryptablePropertyUtils;
    }
}
