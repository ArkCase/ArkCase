package com.armedia.acm.calendar.config.service;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.CalendarPropertyKeys;
import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public class PropertyFileCalendarAdminService implements CalendarAdminService, InitializingBean
{

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 22, 2017
     *
     */
    private class PropertyFileCalendarConfigurationExceptionMapper implements CalendarConfigurationExceptionMapper
    {

        /*
         * (non-Javadoc)
         *
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
                    errorDetails.put("error_cause", "UPDATE_CONFIGURATION_EXCEPTION.");
                }
                errorDetails.put("error_message", ce.getMessage());
            }

            Throwable[] suppressed = ce.getSuppressed();
            Map<String, Map<String, String>> validationFailures = new HashMap<>();

            for (Throwable t : suppressed)
            {
                if (!CalendarConfigurationValidationExcpetion.class.equals(t.getClass()))
                {
                    continue;
                }

                CalendarConfigurationValidationExcpetion cce = CalendarConfigurationValidationExcpetion.class.cast(t);
                String objectType = cce.getObjectType();
                Map<String, String> validationfaiulureByType = validationFailures.computeIfAbsent(objectType, k -> new HashMap<>());

                if (t.getCause() != null && t.getCause().equals(AcmEncryptionException.class))
                {
                    validationfaiulureByType.put("error_cause", "ENCRYPT_EXCEPTION");
                } else
                {
                    validationfaiulureByType.put("error_cause", "INPUT_DATA_EXCEPTION");
                }
                validationfaiulureByType.put("error_message", ce.getMessage());
            }

            errorDetails.put("validationFailures", validationFailures);

            return errorDetails;
        }

    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource configurableObjectTypes;

    private Resource calendarPropertiesResource;

    private AcmEncryptablePropertyUtilsImpl encryptablePropertyUtils;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private List<String> objectTypes;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#readConfiguration()
     */
    @Override
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        Properties calendarProperties = loadProperties();

        CalendarConfigurationsByObjectType configurations = new CalendarConfigurationsByObjectType();

        Set<String> propertyNames = calendarProperties.stringPropertyNames();

        Map<String, CalendarConfiguration> configurationsByType = new HashMap<>();

        CalendarConfigurationException configurationException = null;

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
                if (includePassword)
                {
                    try
                    {
                        configuration.setPassword(encryptablePropertyUtils.decryptPropertyValue(propertyValue));
                    } catch (AcmEncryptionException e)
                    {
                        log.error("Could not decrypt password for calendar configuration for object type {}.", objectType);
                        if (configurationException == null)
                        {
                            configurationException = new CalendarConfigurationException(
                                    "Exception during reading calendar configuration properties.");
                        }
                        configurationException.addSuppressed(new CalendarConfigurationException(
                                String.format("Could not decrypt password for calendar configuration for object type %s.", objectType), e,
                                objectType));
                    }
                }
                break;
            case PURGE_OPTION:
                configuration.setPurgeOptions(PurgeOptions.valueOf(propertyValue));
                break;
            case DAYS_CLOSED:
                configuration.setDaysClosed(Integer.parseInt(propertyValue));
                break;
            }
        }

        if (configurationException != null)
        {
            throw configurationException;
        }

        for (String objectType : objectTypes)
        {
            configurationsByType.computeIfAbsent(objectType, k -> new CalendarConfiguration());
        }

        configurations.setConfigurationsByType(configurationsByType);

        return configurations;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.plugins.outlook.service.CalendarAdminService#writeConfiguration(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationsByObjectType)
     */
    @Override
    public void writeConfiguration(CalendarConfigurationsByObjectType configurations) throws CalendarConfigurationException
    {

        Properties calendarProperties = new Properties();

        Map<String, CalendarConfiguration> configurationsByType = configurations.getConfigurationsByType();

        CalendarConfigurationsByObjectType loadedConfigurations = readConfiguration(true);

        CalendarConfigurationException configurationException = null;

        for (Entry<String, CalendarConfiguration> entry : configurationsByType.entrySet())
        {
            if (!objectTypes.contains(entry.getKey().toUpperCase()))
            {
                continue;
            }

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

        try
        {
            calendarProperties.store(new FileOutputStream(calendarPropertiesResource.getFile()),
                    String.format("Updated from ", getClass().getName()));
        } catch (IOException e)
        {
            log.error("Could not write properties to {} file.", calendarPropertiesResource.getFilename());
            throw new CalendarConfigurationException(
                    String.format("Could not write properties to %s file.", calendarPropertiesResource.getFilename()), e);
        } finally
        {
            writeLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#getExceptionMapper()
     */
    @Override
    public CalendarConfigurationExceptionMapper getExceptionMapper()
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
                log.error("System email and password must be provided for object type {}.", objectType);
                if (cce == null)
                {
                    cce = new CalendarConfigurationException("Exception during writing calendar configuration properties.");
                }
                cce.addSuppressed(new CalendarConfigurationValidationExcpetion(
                        String.format("System email and password must be provided for object type %s.", objectType), objectType));
            } else
            {
                calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.SYSTEM_EMAIL.name()),
                        configuration.getSystemEmail());
                if (configuration.getSystemEmail() != null)
                {
                    try
                    {
                        calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.PASSWORD.name()),
                                encryptablePropertyUtils.encryptPropertyValue(configuration.getPassword()));
                    } catch (AcmEncryptionException e)
                    {
                        log.error("Could not encrypt password for calendar configuration for object type {}.", objectType);
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
        return (loadedConfiguration == null && StringUtils.isEmpty(configuration.getSystemEmail())
                || StringUtils.isEmpty(configuration.getPassword()))
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
                log.error("Number of days has to be provided for purge option for object type {}.", objectType);
                if (cce == null)
                {
                    cce = new CalendarConfigurationException("Exception during writing calendar configuration properties.");
                }
                cce.addSuppressed(new CalendarConfigurationValidationExcpetion(
                        String.format("Number of days has to be provided for purge option for object type %s.", objectType), objectType));
            } else
            {
                calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.PURGE_OPTION),
                        configuration.getPurgeOptions().name());
                if (configuration.getPurgeOptions().equals(PurgeOptions.CLOSED_X_DAYS) && configuration.getDaysClosed() != null)
                {
                    calendarProperties.setProperty(String.format("%s.%s", objectType, CalendarPropertyKeys.DAYS_CLOSED),
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
        try
        {
            calendarProperties.load(calendarPropertiesResource.getInputStream());
            return calendarProperties;
        } catch (IOException e)
        {
            log.error("Could not read properties from {} file.", calendarPropertiesResource.getFilename());
            throw new CalendarConfigurationException(
                    String.format("Could not read properties from %s file.", calendarPropertiesResource.getFilename()), e);
        } finally
        {
            readLock.unlock();
        }
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
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtilsImpl encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Properties objectTypeProperties = new Properties();
        objectTypeProperties.load(configurableObjectTypes.getInputStream());
        String keys = objectTypeProperties.getProperty("configured_object_types").toUpperCase();
        if (keys != null)
        {
            objectTypes = Arrays.asList(keys.split(",\\s*"));
        } else
        {
            objectTypes = new ArrayList<>();
        }
    }

}
