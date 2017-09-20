package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.CalendarServiceException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 17, 2017
 *
 */
public class CalendarServiceBindToRemoteException extends CalendarServiceException
{

    private static final long serialVersionUID = 4570285965499329555L;

    /**
     * @param message
     */
    public CalendarServiceBindToRemoteException(String message)
    {
        super(message);
    }

    /**
     * @param e
     */
    public CalendarServiceBindToRemoteException(Throwable t)
    {
        super(t);
    }

}
