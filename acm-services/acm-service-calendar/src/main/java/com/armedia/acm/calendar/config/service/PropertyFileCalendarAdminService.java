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
import com.armedia.acm.calendar.config.model.CalendarConfig;
import com.armedia.acm.calendar.config.model.PurgeOptions;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 */
public class PropertyFileCalendarAdminService implements CalendarAdminService, ApplicationEventPublisherAware
{
    private static final String CALENDAR_CONFIG_SERVICE_USER_ID = "CALENDAR_CONFIG_SERVICE";
    private Logger log = LogManager.getLogger(getClass());
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;
    private ApplicationEventPublisher applicationEventPublisher;
    private CalendarConfig calendarConfig;
    private ConfigurationPropertyService configurationPropertyService;

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
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword)
    {
        CalendarConfigurationsByObjectType configurations = new CalendarConfigurationsByObjectType();

        Map<String, CalendarConfiguration> configurationsByType = calendarConfig.getConfigurationsByObjectType()
                .entrySet().stream()
                .map(entry -> {
                    CalendarConfiguration configuration = entry.getValue();

                    CalendarConfiguration configurationByType = new CalendarConfiguration();
                    configurationByType.setSystemEmail(configuration.getSystemEmail());
                    configurationByType.setIntegrationEnabled(configuration.isIntegrationEnabled());
                    configurationByType.setPurgeOptions(configuration.getPurgeOptions());
                    configurationByType.setDaysClosed(configuration.getDaysClosed());
                    if (!includePassword)
                    {
                        configurationByType.setPassword("");
                    }
                    else
                    {
                        String password = configuration.getPassword();
                        try
                        {
                            password = encryptablePropertyUtils.decryptPropertyValue(configuration.getPassword());
                        }
                        catch (AcmEncryptionException e)
                        {
                            log.warn("Could not decrypt outlook password.");
                        }
                        configurationByType.setPassword(password);
                    }
                    return new SimpleImmutableEntry<>(entry.getKey(), configurationByType);
                }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        for (String objectType : calendarConfig.getObjectTypes())
        {
            configurationsByType.computeIfAbsent(objectType, k -> new CalendarConfiguration());
        }
        configurations.setConfigurationsByType(configurationsByType);
        return configurations;
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
        Map<String, CalendarConfiguration> configurationsByType = configurations.getConfigurationsByType();
        configurationsByType.entrySet()
                .removeIf(entry -> !calendarConfig.getObjectTypes().contains(entry.getKey().toUpperCase()));

        CalendarConfigurationsByObjectType loadedConfigurations = readConfiguration(true);
        CalendarConfigurationException configurationException = null;
        for (Entry<String, CalendarConfiguration> entry : configurationsByType.entrySet())
        {
            CalendarConfiguration configuration = entry.getValue();
            CalendarConfiguration loadedConfiguration = loadedConfigurations.getConfiguration(entry.getKey());
            configurationException = writeEmailInput(configurationException, entry.getKey(), configuration,
                    loadedConfiguration);
            configurationException = writePurgeOptionsInput(configurationException, entry.getKey(), configuration);
        }
        if (configurationException != null)
        {
            throw configurationException;
        }

        calendarConfig.setConfigurationsByObjectType(configurationsByType);
        configurationPropertyService.updateProperties(calendarConfig);
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
            CalendarConfiguration configuration, CalendarConfiguration loadedConfiguration)
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
                try
                {
                    String encryptedPassword = encryptablePropertyUtils.encryptPropertyValue(
                            configuration.getPassword() != null ? configuration.getPassword() : loadedConfiguration.getPassword());
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
            CalendarConfiguration configuration)
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

    /**
     * @param encryptablePropertyUtils
     *            the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    public CalendarConfig getCalendarConfig()
    {
        return calendarConfig;
    }

    public void setCalendarConfig(CalendarConfig calendarConfig)
    {
        this.calendarConfig = calendarConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
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
