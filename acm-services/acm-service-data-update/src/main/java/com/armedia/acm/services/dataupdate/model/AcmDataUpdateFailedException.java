package com.armedia.acm.services.dataupdate.model;

/**
 * Exception to be thrown in case of failed update operations in
 * any of {@link com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor}
 * implementations and ensure transaction rollback.
 */
public class AcmDataUpdateFailedException extends RuntimeException
{
    public AcmDataUpdateFailedException(String message)
    {
        super(message);
    }

    public AcmDataUpdateFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
