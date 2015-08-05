package com.armedia.acm.service.outlook.exception;

/**
 * Created by manoj.dhungana on 7/28/15.
 */
public class AcmOutlookSendEmailWithAttachmentsFailedException extends AcmOutlookException
{
    public AcmOutlookSendEmailWithAttachmentsFailedException()
    {
    }

    public AcmOutlookSendEmailWithAttachmentsFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookSendEmailWithAttachmentsFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookSendEmailWithAttachmentsFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookSendEmailWithAttachmentsFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
