package com.armedia.acm.auth;

import org.springframework.security.core.AuthenticationException;

public class NoProviderFoundException extends AuthenticationException {

    public NoProviderFoundException(String msg) {
        super(msg);
    }
}
