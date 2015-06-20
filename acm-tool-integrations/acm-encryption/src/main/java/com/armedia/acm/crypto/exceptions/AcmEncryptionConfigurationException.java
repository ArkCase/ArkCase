package com.armedia.acm.crypto.exceptions;

public class AcmEncryptionConfigurationException extends AcmEncryptionException {

    public AcmEncryptionConfigurationException() {
    }

    public AcmEncryptionConfigurationException(String message) {
        super(message);
    }

    public AcmEncryptionConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmEncryptionConfigurationException(Throwable cause) {
        super(cause);
    }

    public AcmEncryptionConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


