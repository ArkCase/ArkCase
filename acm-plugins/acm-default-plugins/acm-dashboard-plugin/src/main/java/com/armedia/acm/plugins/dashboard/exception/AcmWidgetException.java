package com.armedia.acm.plugins.dashboard.exception;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class AcmWidgetException extends Exception {

    public AcmWidgetException() {
    }

    public AcmWidgetException(String message) {
        super(message);
    }

    public AcmWidgetException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmWidgetException(Throwable cause) {
        super(cause);
    }

    public AcmWidgetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}