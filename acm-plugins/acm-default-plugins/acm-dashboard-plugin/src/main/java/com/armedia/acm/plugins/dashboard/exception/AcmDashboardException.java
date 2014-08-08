package com.armedia.acm.plugins.dashboard.exception;

/**
 * Created by marst on 7/29/14.
 */

public class AcmDashboardException extends Exception {

        public AcmDashboardException()
        {
        }

        public AcmDashboardException(String message)
        {
            super(message);
        }

        public AcmDashboardException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public AcmDashboardException(Throwable cause)
        {
            super(cause);
        }

        public AcmDashboardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }


