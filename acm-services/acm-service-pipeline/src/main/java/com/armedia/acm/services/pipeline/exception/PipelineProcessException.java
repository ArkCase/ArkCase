package com.armedia.acm.services.pipeline.exception;

/**
 * Pipeline processing exception
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2015.
 */
public class PipelineProcessException extends Exception
{
    /**
     * Constructor.
     * Create new exception with custom message
     *
     * @param message error message
     */
    public PipelineProcessException(String message)
    {
        super(message);
    }

    /**
     * Constructor.
     * Wrap thrown exception
     *
     * @param exception exception to be wrapped
     */
    public PipelineProcessException(Exception exception)
    {
        super(exception);
    }
}
