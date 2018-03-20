package com.armedia.acm.service.stateofarkcase.exceptions;

/**
 * Custom exception for State Of Arkcase module
 */
public class StateOfArkcaseReportException extends RuntimeException
{
    public StateOfArkcaseReportException(Exception e)
    {
        super(e);
    }

    public StateOfArkcaseReportException(String message, Exception e)
    {
        super(message, e);
    }
}
