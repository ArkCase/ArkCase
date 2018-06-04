package com.armedia.acm.plugins.onlyoffice.model;

public class CallbackResponseSuccess extends CallbackResponse
{
    public CallbackResponseSuccess()
    {
        // onlyoffice expects to get {"error":0} if operation succeeded
        super(0);
    }
}
