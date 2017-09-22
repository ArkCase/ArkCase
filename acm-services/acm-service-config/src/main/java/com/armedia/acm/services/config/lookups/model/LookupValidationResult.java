package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class LookupValidationResult
{
    private boolean isValid;
    private String errorMessage;

    public LookupValidationResult(boolean isValid, String errorMessage)
    {
        super();
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid()
    {
        return isValid;
    }

    public void setValid(boolean isValid)
    {
        this.isValid = isValid;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
