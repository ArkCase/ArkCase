package com.armedia.acm.crypto.exceptions;

public class AcmEncryptionException extends Exception {

    public AcmEncryptionException() {
    }

    public AcmEncryptionException(String message) {
        super(message);
    }

    public AcmEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmEncryptionException(Throwable cause) {
        super(cause);
    }

    public AcmEncryptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


