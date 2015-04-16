package com.armedia.acm.activiti.exceptions;

/**
 * Created by nebojsha on 15.04.2015.
 */
public class AcmProcessDefinitionException extends RuntimeException {
    public AcmProcessDefinitionException(String s) {
        super(s);
    }

    public AcmProcessDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
