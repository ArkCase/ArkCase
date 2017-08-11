package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public class OutlookCalendarAdminService implements OutlookCalendarAdminServiceExtension
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService extendedService;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#readConfiguration(boolean)
     */
    @Override
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        return extendedService.readConfiguration(includePassword);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.config.service.CalendarAdminService#writeConfiguration(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationsByObjectType)
     */
    @Override
    public void writeConfiguration(CalendarConfigurationsByObjectType configuration) throws CalendarConfigurationException
    {
        extendedService.writeConfiguration(configuration);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.config.service.CalendarAdminService#getExceptionMapper(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationException)
     */
    @Override
    public <CCE extends CalendarConfigurationException> CalendarConfigurationExceptionMapper<CCE> getExceptionMapper(CCE e)
    {
        return extendedService.getExceptionMapper(e);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#getOutlookUser(java.lang.String)
     */
    @Override
    public Optional<AcmOutlookUser> getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException
    {
        try
        {
            return Optional.ofNullable(getOutlookUser(null, objectType));
        } catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new AcmOutlookItemNotFoundException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#getOutlookUser(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Optional<AcmOutlookUser> getHandlerOutlookUser(String userName, String objectType) throws PipelineProcessException
    {
        try
        {
            return Optional.ofNullable(getOutlookUser(userName, objectType));
        } catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new PipelineProcessException(e);
        }
    }

    private AcmOutlookUser getOutlookUser(String userName, String objectType) throws CalendarConfigurationException
    {
        CalendarConfigurationsByObjectType configurations = extendedService.readConfiguration(true);
        CalendarConfiguration configuration = configurations.getConfiguration(objectType);
        if (configuration != null && configuration.isIntegrationEnabled())
        {
            return new AcmOutlookUser(userName, configuration.getSystemEmail(), configuration.getPassword());
        }
        return null;
    }

    /**
     * @param extendedService
     *            the extendedService to set
     */
    public void setExtendedService(CalendarAdminService extendedService)
    {
        this.extendedService = extendedService;
    }

}
