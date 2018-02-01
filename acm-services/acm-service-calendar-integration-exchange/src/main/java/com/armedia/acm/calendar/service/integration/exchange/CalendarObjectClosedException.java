package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.CalendarServiceException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 22, 2018
 *
 */
public class CalendarObjectClosedException extends CalendarServiceException
{

    private static final long serialVersionUID = -1778865468809272335L;

    /**
     * @param message
     */
    public CalendarObjectClosedException(String message)
    {
        super(message);
    }

}
