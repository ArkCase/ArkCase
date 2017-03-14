package com.armedia.acm.plugins.outlook.service.impl;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;
import com.armedia.acm.plugins.outlook.service.CalendarAdminService;
import com.armedia.acm.plugins.outlook.service.CalendarConfigurationException;
import com.armedia.acm.plugins.outlook.web.api.CalendarConfiguration;
import com.armedia.acm.plugins.outlook.web.api.CalendarConfiguration.CalendarPropertyKeys;
import com.armedia.acm.plugins.outlook.web.api.CalendarConfiguration.CalendarType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public class PropertyFileCalendarAdminService implements CalendarAdminService, InitializingBean
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource calendarPropertiesResource;

    private Properties calendarProperties;

    private AcmEncryptablePropertyUtilsImpl encryptablePropertyUtils;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.outlook.service.CalendarAdminService#readConfiguration()
     */
    @Override
    public CalendarConfiguration readConfiguration()
    {
        CalendarConfiguration configuration = new CalendarConfiguration();
        String calendarType = calendarProperties.getProperty(CalendarPropertyKeys.CALENDAR_TYPE.name());
        if (calendarType != null)
        {
            configuration.setCalendarType(CalendarType.valueOf(calendarType));
        }
        String systemEmail = calendarProperties.getProperty(CalendarPropertyKeys.SYSTEM_EMAIL.name());
        configuration.setSystemEmail(systemEmail);
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

        try
        {
            calendarProperties.store(new FileOutputStream(calendarPropertiesResource.getFile()),
                    String.format("Updated from ", getClass().getName()));
        } catch (IOException e)
        {
            log.error("Could not write properties to {} file.", calendarPropertiesResource.getFilename());
            throw new CalendarConfigurationException(
                    String.format("Could not write properties to %s file.", calendarPropertiesResource.getFilename()), e);
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

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        calendarProperties = new Properties();
        calendarProperties.load(calendarPropertiesResource.getInputStream());
    }

}
