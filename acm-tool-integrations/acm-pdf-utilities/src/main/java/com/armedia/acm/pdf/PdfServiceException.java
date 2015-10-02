package com.armedia.acm.pdf;

/**
 * PDF processing exception
 * Created by Petar Ilin <petar.ilin@armedia.com> on 02.10.2015.
 */
public class PdfServiceException extends Exception
{
    /**
     * Constructor.
     * Create new exception with custom message
     *
     * @param message error message
     */
    public PdfServiceException(String message)
    {
        super(message);
    }

    /**
     * Constructor.
     * Wrap thrown exception
     *
     * @param exception exception to be wrapped
     */
    public PdfServiceException(Exception exception)
    {
        super(exception);
    }
}
