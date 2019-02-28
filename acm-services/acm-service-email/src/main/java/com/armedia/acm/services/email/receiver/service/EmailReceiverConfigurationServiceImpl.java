package com.armedia.acm.services.email.receiver.service;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.EMAIL_CASE, emailReceiverConfiguration.getUser());
        if (emailReceiverConfiguration.getPassword() == null)
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD_CASE,
                    acmEncryptablePropertyUtils.encryptPropertyValue(configuration.getPassword()));
        }
        else
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD_CASE,
                    acmEncryptablePropertyUtils.encryptPropertyValue(emailReceiverConfiguration.getPassword()));
        }
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.PROTOCOL, configuration.getProtocol());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.FETCH_FOLDER, configuration.getFetchFolder());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.HOST, configuration.getHost());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.PORT, Integer.toString(configuration.getPort()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.DEBUG, Boolean.toString(configuration.isDebug()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.USER_ID, configuration.getUserId());
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.EMAIL_COMPLAINT, emailReceiverConfiguration.getUser_complaint());
        if (emailReceiverConfiguration.getPassword_complaint() == null)
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD_COMPLAINT,
                    acmEncryptablePropertyUtils.encryptPropertyValue(configuration.getPassword_complaint()));
        }
        else
        {
            emailReceiverProperties.put(EmailReceiverConfigurationConstants.PASSWORD_COMPLAINT,
                    acmEncryptablePropertyUtils.encryptPropertyValue(emailReceiverConfiguration.getPassword_complaint()));
        }
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.ENABLE_CREATING_CASE, Boolean.toString(emailReceiverConfiguration.getEnableCase()));
        emailReceiverProperties.put(EmailReceiverConfigurationConstants.ENABLE_CREATING_COMPLAINT, Boolean.toString(emailReceiverConfiguration.getEnableComplaint()));

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
            case EmailReceiverConfigurationConstants.EMAIL_CASE:
                emailReceiverConfiguration.setUser(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.PASSWORD_CASE:
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
                break;
            case EmailReceiverConfigurationConstants.USER_ID:
                emailReceiverConfiguration.setUserId(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.EMAIL_COMPLAINT:
                emailReceiverConfiguration.setUser_complaint(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.PASSWORD_COMPLAINT:
                emailReceiverConfiguration.setPassword_complaint(propertyValue);
                break;
            case EmailReceiverConfigurationConstants.ENABLE_CREATING_CASE:
                emailReceiverConfiguration.setEnableCase(Boolean.valueOf(propertyValue));
                break;
            case EmailReceiverConfigurationConstants.ENABLE_CREATING_COMPLAINT:
                emailReceiverConfiguration.setEnableComplaint(Boolean.valueOf(propertyValue));
                break;
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
