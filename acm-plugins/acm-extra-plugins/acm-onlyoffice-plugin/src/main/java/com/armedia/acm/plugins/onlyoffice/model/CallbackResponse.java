package com.armedia.acm.plugins.onlyoffice.model;

public abstract class CallbackResponse
{
    private int error;

    public CallbackResponse(int error)
    {
        this.error = error;
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
