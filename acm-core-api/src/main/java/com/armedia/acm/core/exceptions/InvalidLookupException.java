package com.armedia.acm.core.exceptions;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class InvalidLookupException extends Exception
{
    private static final long serialVersionUID = 1L;

    public InvalidLookupException(String message)
    {
        super(message);
    }

    public InvalidLookupException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
