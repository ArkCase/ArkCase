package com.armedia.acm.service.objectlock.exception;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockException extends RuntimeException {
    public AcmObjectLockException(String message) {
        super(message);
    }

    public AcmObjectLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
