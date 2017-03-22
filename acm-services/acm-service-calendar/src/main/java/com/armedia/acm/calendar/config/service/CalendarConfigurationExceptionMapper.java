package com.armedia.acm.calendar.config.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 22, 2017
 *
 */
public interface CalendarConfigurationExceptionMapper
{

    /**
     * @param ce
     * @return
     */
    Object mapException(CalendarConfigurationException ce);

}
