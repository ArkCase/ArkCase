package com.armedia.acm.activiti.exceptions;

/**
 * Created by nebojsha on 16.04.2015.
 */
public class NotValidBpmnFileExceptuon extends AcmBpmnException {
    public NotValidBpmnFileExceptuon(String s) {
        super(s);
    }

    public NotValidBpmnFileExceptuon(String s, Throwable e) {
        super(s, e);
    }
}
