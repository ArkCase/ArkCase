package com.armedia.acm.plugins.onlyoffice.model;

public class CallbackResponse
{
    private int error;

    public CallbackResponse()
    {
        error = 0;
    }

    public int getError()
    {
        return error;
    }

    public void setError(int error)
    {
        this.error = error;
    }
}
