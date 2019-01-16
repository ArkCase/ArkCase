package com.armedia.acm.calendar.config.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.calendar.config.service.CalendarConfiguration.CalendarPropertyKeys;
import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 */
public class PropertyFileCalendarAdminService implements CalendarAdminService, InitializingBean, ApplicationEventPublisherAware
{

    private static final String CALENDAR_CONFIG_SERVICE_USER_ID = "CALENDAR_CONFIG_SERVICE";
    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource configurableObjectTypes;
    private Resource calendarPropertiesResource;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<String> objectTypes;
    private ApplicationEventPublisher applicationEventPublisher;
    private CalendarConfigurationsByObjectType configurations;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#readConfiguration()
     */
    @Override
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        CalendarConfigurationException configurationException = new CalendarConfigurationException(
                "Exception during reading calendar configuration properties.");
        CalendarConfigurationsByObjectType configurationsCopy = new CalendarConfigurationsByObjectType();

        Lock readLock = lock.readLock();
        readLock.lock();

        Map<String, CalendarConfiguration> configurationsByType;

        try
        {

            configurationsByType = configurations.getConfigurationsByType().entrySet().stream().map(entry -> {

                try
                {
                    CalendarConfiguration configuration = entry.getValue();

                    CalendarConfiguration configurationCopy = new CalendarConfiguration();
                    configurationCopy.setIntegrationEnabled(configuration.isIntegrationEnabled());
                    configurationCopy.setPurgeOptions(configuration.getPurgeOptions());
                    configurationCopy.setDaysClosed(configuration.getDaysClosed());
                    configurationCopy.setSystemEmail(configuration.getSystemEmail());
                    if (includePassword)
                    {
                        configurationCopy.setPassword(encryptablePropertyUtils.decryptPropertyValue(configuration.getPassword()));
                    }

                    return new SimpleImmutableEntry<>(entry.getKey(), configurationCopy);
                }
                catch (AcmEncryptionException e)
                {
                    log.error("Could not decrypt password for calendar configuration for object type [{}].", entry.getKey());
                    configurationException.addSuppressed(new CalendarConfigurationException(
                            String.format("Could not decrypt password for calendar configuration for object type [%s].", entry.getKey()), e,
                            entry.getKey()));
                    return null;
                }

            }).filter(entry -> entry != null).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        }
        finally
        {
            readLock.unlock();
        }

        if (configurationException.getSuppressed().length > 0)
        {
            throw configurationException;
        }

        for (String objectType : objectTypes)
        {
            configurationsByType.computeIfAbsent(objectType, k -> new CalendarConfiguration());
        }

        configurationsCopy.setConfigurationsByType(configurationsByType);
        return configurationsCopy;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.plugins.outlook.service.CalendarAdminService#writeConfiguration(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationsByObjectType)
     */
    @Override
    public void writeConfiguration(CalendarConfigurationsByObjectType configurations) throws CalendarConfigurationException
    {

        Properties calendarProperties = new Properties();

        Map<String, CalendarConfiguration> configurationsByType = configurations.getConfigurationsByType();
        configurationsByType.entrySet().removeIf(entry -> !objectTypes.contains(entry.getKey().toUpperCase()));

        CalendarConfigurationsByObjectType loadedConfigurations = readConfiguration(true);

        CalendarConfigurationException configurationException = null;

        for (Entry<String, CalendarConfiguration> entry : configurationsByType.entrySet())
        {

            CalendarConfiguration configuration = entry.getValue();
            CalendarConfiguration loadedConfiguration = loadedConfigurations.getConfiguration(entry.getKey());

            configurationException = writeEmailInput(configurationException, entry.getKey(), configuration, loadedConfiguration,
                    calendarProperties);
            configurationException = writePurgeOptionsInput(configurationException, entry.getKey(), configuration, calendarProperties);
            calendarProperties.setProperty(String.format("%s.%s", entry.getKey(), CalendarPropertyKeys.INTEGRATION_ENABLED.name()),
                    Boolean.toString(configuration.isIntegrationEnabled()));

        }

        if (configurationException != null)
        {
            throw configurationException;
        }

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try (OutputStream propertyOutputStream = new FileOutputStream(calendarPropertiesResource.getFile()))
        {
            calendarProperties.store(propertyOutputStream, String.format("Updated from ", getClass().getName()));
            this.configurations = configurations;
        }
        catch (IOException e)
        {
            log.error("Could not write properties to [{}] file.", calendarPropertiesResource.getFilename());
            throw new CalendarConfigurationException(
                    String.format("Could not write properties to %s file.", calendarPropertiesResource.getFilename()), e);
        }
        finally
        {
            writeLock.unlock();
        }
        applicationEventPublisher.publishEvent(
                new CalendarConfigurationEvent(configurations, CALENDAR_CONFIG_SERVICE_USER_ID, AuthenticationUtils.getUserIpAddress()));
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#getExceptionMapper()
     */
    @Override
    public CalendarConfigurationExceptionMapper<CalendarConfigurationException> getExceptionMapper(CalendarConfigurationException cce)
    {
        return new PropertyFileCalendarConfigurationExceptionMapper();
    }

    private CalendarConfigurationException writeEmailInput(CalendarConfigurationException cce, String objectType,
            CalendarConfiguration configuration, CalendarConfiguration loadedConfiguration, Properties calendarProperties)
    {

        if (configuration.isIntegrationEnabled())
        {
            if (checkEmailInput(configuration, loadedConfiguration))
            {
                log.error("System email and password must be provided for object type [{}].", objectType);
                if (cce == null)
                {
                    cce = new CalendarConfigurationException("Exception during writing calendar configuration properties.");
                }
                cce.addSuppressed(new CalendarConfigurationValidationException(
                        String.format("System email and password must be provided for object type %s.", objectType), objectType));
            }
            else
            {
                calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.SYSTEM_EMAIL.name()),
                        configuration.getSystemEmail());
                try
                {
                    String encryptedPassword = encryptablePropertyUtils.encryptPropertyValue(
                            configuration.getPassword() != null ? configuration.getPassword() : loadedConfiguration.getPassword());
                    calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.PASSWORD.name()),
                            encryptedPassword);
                    configuration.setPassword(encryptedPassword);
                }
                catch (AcmEncryptionException e)
                {
                    log.error("Could not encrypt password for calendar configuration for object type [{}].", objectType);
                    if (cce == null)
                    {
                        cce = new CalendarConfigurationException("Exception during writing calendar configuration properties.");
                    }
                    cce.addSuppressed(new CalendarConfigurationException(
                            String.format("Could not encrypt password for calendar configuration for object type %s.", objectType), e,
                            objectType));
                }
            }
        }

        return cce;
    }

    /**
     * @param configuration
     * @param loadedConfiguration
     * @return <code>true</code> if the email input is not valid, <code>false</code> otherwise.
     */
    private boolean checkEmailInput(CalendarConfiguration configuration, CalendarConfiguration loadedConfiguration)
    {
        // for new configurations both system email and password have to be provided
        return (loadedConfiguration == null
                && (StringUtils.isEmpty(configuration.getSystemEmail()) || StringUtils.isEmpty(configuration.getPassword())))
                // for existing configurations email has to be provided, and in case email has changed, an accompanying
                // password must be provided
                || (loadedConfiguration != null && (StringUtils.isEmpty(configuration.getSystemEmail())
                        || !configuration.getSystemEmail().equals(loadedConfiguration.getSystemEmail())
                                && StringUtils.isEmpty(configuration.getPassword())));
    }

    private CalendarConfigurationException writePurgeOptionsInput(CalendarConfigurationException cce, String objectType,
            CalendarConfiguration configuration, Properties calendarProperties)
    {

        if (configuration.isIntegrationEnabled())
        {
            if (checkPurgeOptionsInput(configuration))
            {
                log.error("Number of days has to be provided for purge option for object type [{}].", objectType);
                if (cce == null)
                {
                    cce = new CalendarConfigurationException("Exception during writing calendar configuration properties.");
                }
                cce.addSuppressed(new CalendarConfigurationValidationException(
                        String.format("Number of days has to be provided for purge option for object type %s.", objectType), objectType));
            }
            else
            {
                calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.PURGE_OPTION.name()),
                        configuration.getPurgeOptions().name());
                if (configuration.getPurgeOptions().equals(PurgeOptions.CLOSED_X_DAYS) && configuration.getDaysClosed() != null)
                {
                    calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.DAYS_CLOSED.name()),
                            configuration.getDaysClosed().toString());
                }
            }
        }

        return cce;
    }

    /**
     * @param configuration
     * @return <code>true</code> if the purge options input is not valid, <code>false</code> otherwise.
     */
    private boolean checkPurgeOptionsInput(CalendarConfiguration configuration)
    {
        return configuration.getPurgeOptions().equals(PurgeOptions.CLOSED_X_DAYS) && configuration.getDaysClosed() == null;
    }

    private Properties loadProperties() throws CalendarConfigurationException
    {
        Properties calendarProperties = new Properties();
        Lock readLock = lock.readLock();
        readLock.lock();
        try (InputStream propertyInputStream = calendarPropertiesResource.getInputStream())
        {
            calendarProperties.load(propertyInputStream);
            return calendarProperties;
        }
        catch (IOException e)
        {
            log.error("Could not read properties from [{}] file.", calendarPropertiesResource.getFilename(), e);
            throw new CalendarConfigurationException(
                    String.format("Could not read properties from %s file.", calendarPropertiesResource.getFilename()), e);
        }
        finally
        {
            readLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Properties objectTypeProperties = new Properties();
        try (InputStream objectTypesInputStream = configurableObjectTypes.getInputStream())
        {

            objectTypeProperties.load(objectTypesInputStream);
            String keys = objectTypeProperties.getProperty("configured_object_types").toUpperCase();
            if (keys != null)
            {
                objectTypes = Arrays.asList(keys.split(",\\s*"));
            }
            else
            {
                objectTypes = new ArrayList<>();
            }
        }
        catch (IOException ioe)
        {
            log.error("Could not read properties from [{}] file.", configurableObjectTypes.getFilename(), ioe);
            objectTypes = new ArrayList<>();
        }
        // load configurations in memory on startup in order to speed up subsequent reads.
        loadConfigurations();
    }

    private void loadConfigurations() throws CalendarConfigurationException
    {
        Properties calendarProperties = loadProperties();

        configurations = new CalendarConfigurationsByObjectType();

        Set<String> propertyNames = calendarProperties.stringPropertyNames();

        Map<String, CalendarConfiguration> configurationsByType = new HashMap<>();

        for (String propertyName : propertyNames)
        {
            String[] objectTypePropertyName = propertyName.split("\\.");
            String objectType = objectTypePropertyName[0];

            if (!objectTypes.contains(objectType.toUpperCase()))
            {
                continue;
            }

            CalendarConfiguration configuration = configurationsByType.computeIfAbsent(objectType, k -> new CalendarConfiguration());
            String propertyValue = calendarProperties.getProperty(propertyName);
            CalendarPropertyKeys propertyType = CalendarPropertyKeys.valueOf(objectTypePropertyName[1]);
            switch (propertyType)
            {
            case INTEGRATION_ENABLED:
                configuration.setIntegrationEnabled(Boolean.valueOf(propertyValue));
                break;
            case SYSTEM_EMAIL:
                configuration.setSystemEmail(propertyValue);
                break;
            case PASSWORD:
                configuration.setPassword(propertyValue);
                break;
            case PURGE_OPTION:
                configuration.setPurgeOptions(PurgeOptions.valueOf(propertyValue));
                break;
            case DAYS_CLOSED:
                configuration.setDaysClosed(Integer.parseInt(propertyValue));
                break;
            }
        }

        for (String objectType : objectTypes)
        {
            configurationsByType.computeIfAbsent(objectType, k -> new CalendarConfiguration());
        }

        configurations.setConfigurationsByType(configurationsByType);
    }

    /**
     * @param configurableObjectTypes
     *            the configurableObjectTypes to set
     */
    public void setConfigurableObjectTypes(Resource configurableObjectTypes)
    {
        this.configurableObjectTypes = configurableObjectTypes;
    }

    /**
     * @param calendarPropertiesResource
     *            the calendarPropertiesResource to set
     */
    public void setCalendarPropertiesResource(Resource calendarPropertiesResource)
    {
        this.calendarPropertiesResource = calendarPropertiesResource;
    }

    /**
     * @param encryptablePropertyUtils
     *            the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 22, 2017
     */
    private class PropertyFileCalendarConfigurationExceptionMapper
            implements CalendarConfigurationExceptionMapper<CalendarConfigurationException>
    {

        /*
         * (non-Javadoc)
         * @see
         * com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper#mapException(com.armedia.acm.
         * calendar.config.service.CalendarConfigurationException)
         */
        @Override
        public Object mapException(CalendarConfigurationException ce)
        {
            Map<String, Object> errorDetails = new HashMap<>();

            Throwable cause = ce.getCause();
            if (cause != null)
            {
                Class<? extends Throwable> causeClass = cause.getClass();
                if (causeClass.equals(IOException.class))
                {
                    errorDetails.put(ERROR_CAUSE, UPDATE_CONFIGURATION_EXCEPTION);
                }
                errorDetails.put(ERROR_MESSAGE, ce.getMessage());
            }

            Throwable[] suppressed = ce.getSuppressed();
            Map<String, Map<String, String>> validationFailures = new HashMap<>();

            for (Throwable t : suppressed)
            {
                if (!CalendarConfigurationValidationException.class.equals(t.getClass()))
                {
                    continue;
                }

                CalendarConfigurationValidationException cce = CalendarConfigurationValidationException.class.cast(t);
                String objectType = cce.getObjectType();
                Map<String, String> validationfaiulureByType = validationFailures.computeIfAbsent(objectType, k -> new HashMap<>());

                if (t.getCause() != null && t.getCause().getClass().equals(AcmEncryptionException.class))
                {
                    validationfaiulureByType.put(ERROR_CAUSE, ENCRYPT_EXCEPTION);
                }
                else
                {
                    validationfaiulureByType.put(ERROR_CAUSE, INPUT_DATA_EXCEPTION);
                }
                validationfaiulureByType.put(ERROR_MESSAGE, ce.getMessage());
            }

            errorDetails.put("validationFailures", validationFailures);

            return errorDetails;
        }

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            return HttpStatus.BAD_REQUEST;
        }

    }

}
