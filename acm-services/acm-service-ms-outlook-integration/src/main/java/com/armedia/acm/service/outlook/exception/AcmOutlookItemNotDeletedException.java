package com.armedia.acm.service.outlook.exception;

/**
 * Created by nebojsha on 04.05.2015.
 */
public class AcmOutlookItemNotDeletedException extends AcmOutlookException {
    public AcmOutlookItemNotDeletedException() {
    }

    public AcmOutlookItemNotDeletedException(String message) {
        super(message);
    }

    public AcmOutlookItemNotDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmOutlookItemNotDeletedException(Throwable cause) {
        super(cause);
    }

    public AcmOutlookItemNotDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
