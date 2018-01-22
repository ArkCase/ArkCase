package com.armedia.acm.service.outlook.exception;

import com.armedia.acm.core.exceptions.AcmOutlookException;

public class AcmOutlookSendEmailWithEmbeddedLinksFailedException extends AcmOutlookException
{
    public AcmOutlookSendEmailWithEmbeddedLinksFailedException()
    {
    }

    public AcmOutlookSendEmailWithEmbeddedLinksFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookSendEmailWithEmbeddedLinksFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookSendEmailWithEmbeddedLinksFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookSendEmailWithEmbeddedLinksFailedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}