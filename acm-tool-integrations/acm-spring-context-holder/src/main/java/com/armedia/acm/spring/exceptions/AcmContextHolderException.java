package com.armedia.acm.spring.exceptions;

/**
 * Created by nebojsha on 24.04.2015.
 */
public class AcmContextHolderException extends RuntimeException {
    public AcmContextHolderException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AcmContextHolderException(String message) {
        super(message);
    }
}
