package com.armedia.acm.crypto.exceptions;

public class AcmEncryptionBadKeyOrDataException extends AcmEncryptionException {

    public AcmEncryptionBadKeyOrDataException() {
    }

    public AcmEncryptionBadKeyOrDataException(String message) {
        super(message);
    }

    public AcmEncryptionBadKeyOrDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcmEncryptionBadKeyOrDataException(Throwable cause) {
        super(cause);
    }

    public AcmEncryptionBadKeyOrDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


