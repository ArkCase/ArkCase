package com.armedia.acm.activiti.exceptions;

/**
 * Created by nebojsha on 15.04.2015.
 */
public class AcmBpmnException extends RuntimeException {
    public AcmBpmnException(String s) {
        super(s);
    }

    public AcmBpmnException(String message, Throwable cause) {
        super(message, cause);
    }
}
