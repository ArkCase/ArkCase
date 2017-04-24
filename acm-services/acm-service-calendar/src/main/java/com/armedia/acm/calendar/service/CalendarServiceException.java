package com.armedia.acm.calendar.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 4, 2017
 *
 */
public class CalendarServiceException extends Exception
{

    private static final long serialVersionUID = 1L;

    public CalendarServiceException(String message)
    {
        super(message);
    }

    /**
     * @param e
     */
    public CalendarServiceException(Throwable t)
    {
        super(t);
    }

}
