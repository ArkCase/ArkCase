package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.CalendarServiceException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 12, 2017
 *
 */
public class CalendarServiceConfigurationException extends CalendarServiceException
{

    /**
     *
     */
    private static final long serialVersionUID = 628773797764354601L;

    public CalendarServiceConfigurationException(String message)
    {
        super(message);
    }

    /**
     * @param e
     */
    public CalendarServiceConfigurationException(Throwable t)
    {
        super(t);
    }

}
