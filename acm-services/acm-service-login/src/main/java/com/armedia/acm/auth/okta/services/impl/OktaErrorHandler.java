package com.armedia.acm.auth.okta.services.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class OktaErrorHandler extends DefaultResponseErrorHandler
{
    @Override
    protected boolean hasError(HttpStatus statusCode)
    {
        if (statusCode.is4xxClientError())
        {
            return false;
        }
        else
        {
            return super.hasError(statusCode);
        }
    }
}
