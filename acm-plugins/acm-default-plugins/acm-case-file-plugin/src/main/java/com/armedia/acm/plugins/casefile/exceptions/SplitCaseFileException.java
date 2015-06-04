package com.armedia.acm.plugins.casefile.exceptions;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseFileException extends Exception {
    public SplitCaseFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SplitCaseFileException(String s) {
        super(s);
    }
}
