package com.armedia.acm.plugins.onlyoffice.model;

public class CallbackResponseError extends CallbackResponse
{
    private String message;

    public CallbackResponseError(String message)
    {
        super(1);
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
