package com.armedia.acm.service.outlook.exception;


public class AcmOutlookListItemsFailedException extends AcmOutlookException {
    public AcmOutlookListItemsFailedException() {
    }

    public AcmOutlookListItemsFailedException(String message) {
        super(message);
    }

    public AcmOutlookListItemsFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmOutlookListItemsFailedException(Throwable cause) {
        super(cause);
    }

    public AcmOutlookListItemsFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
