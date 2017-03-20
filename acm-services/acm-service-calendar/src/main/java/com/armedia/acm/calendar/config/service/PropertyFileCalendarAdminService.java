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

        for (String propertyName : propertyNames)
        {
            String[] objectTypePropertyName = propertyName.split("\\.");

            if (!objectTypes.contains(objectTypePropertyName[0].toUpperCase()))
            {
                continue;
            }

            CalendarConfiguration configuration = configurationsByType.computeIfAbsent(objectTypePropertyName[0],
                    k -> new CalendarConfiguration());
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
                        log.error("Could not decrypt password for calendar configuration.");
                        throw new CalendarConfigurationException("Could not decrypt password for calendar configuration.", e);
                    }
                }
                break;
            case PURGE_OPTION:
                configuration.setPurgeOptions(PurgeOptions.valueOf(propertyValue));
                break;
            }
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

        for (Entry<String, CalendarConfiguration> entry : configurationsByType.entrySet())
        {
            if (!objectTypes.contains(entry.getKey().toUpperCase()))
            {
                continue;
            }

            CalendarConfiguration configuration = entry.getValue();
            CalendarConfiguration loadedConfiguration = loadedConfigurations.getConfiguration(entry.getKey());

            if (checkInput(configuration, loadedConfiguration))
            {
                log.error("System email and password must be provided.");
                throw new CalendarConfigurationException("System email and password must be provided.");
            }
            calendarProperties.setProperty(String.format("%s.%s", entry.getKey(), CalendarPropertyKeys.SYSTEM_EMAIL.name()),
                    configuration.getSystemEmail());
            try
            {
                calendarProperties.setProperty(String.format("%s.%s", entry.getKey(), CalendarPropertyKeys.PASSWORD.name()),
                        encryptablePropertyUtils.encryptPropertyValue(configuration.getSystemEmail()));
            } catch (AcmEncryptionException e)
            {
                log.error("Could not encrypt password for calendar configuration.");
                throw new CalendarConfigurationException("Could not encrypt password for calendar configuration.", e);
            }
            calendarProperties.setProperty(String.format("%s.%s", entry.getKey(), CalendarPropertyKeys.INTEGRATION_ENABLED.name()),
                    Boolean.toString(configuration.isIntegrationEnabled()));
            calendarProperties.setProperty(String.format("%s.%s", entry.getKey(), CalendarPropertyKeys.PURGE_OPTION),
                    configuration.getPurgeOptions().name());

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

    /**
     * @param configuration
     * @param loadedConfiguration
     * @return
     */
    private boolean checkInput(CalendarConfiguration configuration, CalendarConfiguration loadedConfiguration)
    {
        return (loadedConfiguration == null && StringUtils.isEmpty(configuration.getSystemEmail())
                || StringUtils.isEmpty(configuration.getPassword()))
                || (loadedConfiguration != null && (StringUtils.isEmpty(configuration.getSystemEmail())
                        || !configuration.getSystemEmail().equals(loadedConfiguration.getSystemEmail())
                                && StringUtils.isEmpty(configuration.getPassword())));
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
