package com.armedia.acm.calendar.config.service;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.CalendarPropertyKeys;
import com.armedia.acm.calendar.config.service.CalendarConfiguration.CalendarType;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public class PropertyFileCalendarAdminService implements CalendarAdminService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource calendarPropertiesResource;

    private AcmEncryptablePropertyUtilsImpl encryptablePropertyUtils;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.outlook.service.CalendarAdminService#readConfiguration()
     */
    @Override
    public CalendarConfiguration readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        Properties calendarProperties = loadProperties();

        CalendarConfiguration configuration = new CalendarConfiguration();
        String calendarType = calendarProperties.getProperty(CalendarPropertyKeys.CALENDAR_TYPE.name());
        if (calendarType != null)
        {
            configuration.setCalendarType(CalendarType.valueOf(calendarType));
        }
        String systemEmail = calendarProperties.getProperty(CalendarPropertyKeys.SYSTEM_EMAIL.name());
        configuration.setSystemEmail(systemEmail);
        if (includePassword)
        {
            try
            {
                configuration.setPassword(encryptablePropertyUtils
                        .decryptPropertyValue(calendarProperties.getProperty(CalendarPropertyKeys.PASSWORD.name())));
            } catch (AcmEncryptionException e)
            {
                log.error("Could not decrypt password for calendar configuration.");
                throw new CalendarConfigurationException("Could not decrypt password for calendar configuration.", e);
            }
        }
        return configuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.plugins.outlook.service.CalendarAdminService#writeConfiguration(com.armedia.acm.plugins.outlook.
     * web.api.CalendarConfiguration)
     */
    @Override
    public void writeConfiguration(CalendarConfiguration configuration) throws CalendarConfigurationException
    {
        Properties calendarProperties = loadProperties();

        if (configuration.getCalendarType().equals(CalendarType.SYSTEM_BASED))
        {
            if (StringUtils.isEmpty(configuration.getSystemEmail()) || StringUtils.isEmpty(configuration.getPassword()))
            {
                log.error("System email and password must be provided for 'SYSTEM_BASED' calendar.");
                throw new CalendarConfigurationException("System email and password must be provided for 'SYSTEM_BASED' calendar.");
            }
            calendarProperties.setProperty(CalendarPropertyKeys.SYSTEM_EMAIL.name(), configuration.getSystemEmail());
            try
            {
                calendarProperties.setProperty(CalendarPropertyKeys.PASSWORD.name(),
                        encryptablePropertyUtils.encryptPropertyValue(configuration.getSystemEmail()));
            } catch (AcmEncryptionException e)
            {
                log.error("Could not encrypt password for calendar configuration.");
                throw new CalendarConfigurationException("Could not encrypt password for calendar configuration.", e);
            }
        } else
        {
            calendarProperties.remove(CalendarPropertyKeys.SYSTEM_EMAIL.name());
            calendarProperties.remove(CalendarPropertyKeys.PASSWORD.name());
        }
        calendarProperties.setProperty(CalendarPropertyKeys.CALENDAR_TYPE.name(), configuration.getCalendarType().name());

        Lock writeLock = lock.writeLock();

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

    private Properties loadProperties() throws CalendarConfigurationException
    {
        Properties calendarProperties = new Properties();
        Lock readLock = lock.readLock();
        try
        {
            calendarProperties.load(calendarPropertiesResource.getInputStream());
            return calendarProperties;
        } catch (IOException e)
        {
            log.error("Could not read properties from {} file.", calendarPropertiesResource.getFilename());
            throw new CalendarConfigurationException(
                    String.format("Could not read properties fromo %s file.", calendarPropertiesResource.getFilename()), e);
        } finally
        {
            readLock.unlock();
        }
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

}
