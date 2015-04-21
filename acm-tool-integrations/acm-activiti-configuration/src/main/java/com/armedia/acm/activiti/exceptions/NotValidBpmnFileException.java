package com.armedia.acm.activiti.exceptions;

/**
 * Created by nebojsha on 16.04.2015.
 */
public class NotValidBpmnFileException extends AcmBpmnException {
    public NotValidBpmnFileException(String s) {
        super(s);
    }

    public NotValidBpmnFileException(String s, Throwable e) {
        super(s, e);
    }
}
