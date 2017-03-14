package com.armedia.acm.plugins.outlook.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 13, 2017
 *
 */
public class CalendarConfigurationException extends Exception
{

    private static final long serialVersionUID = -3718074504554001030L;

    public CalendarConfigurationException(String message)
    {
        super(message);
    }

    public CalendarConfigurationException(Throwable t)
    {
        super(t);
    }

    public CalendarConfigurationException(String message, Throwable t)
    {
        super(message, t);
    }

}
