package com.armedia.acm.service.stateofarkcase.exceptions;

import java.io.FileNotFoundException;

public class ErrorLogFileException extends Exception
{
    /**
     * Constructs a new exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message
     *            the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public ErrorLogFileException(String message)
    {
        super(message);
    }

    public ErrorLogFileException(FileNotFoundException e)
    {
        super(e);
    }
}
