package com.armedia.acm.auth.okta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse
{
    private String errorCode;
    private String errorSummary;
    private String errorLink;
    private String errorId;

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorSummary()
    {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary)
    {
        this.errorSummary = errorSummary;
    }

    public String getErrorLink()
    {
        return errorLink;
    }

    public void setErrorLink(String errorLink)
    {
        this.errorLink = errorLink;
    }

    public String getErrorId()
    {
        return errorId;
    }

    public void setErrorId(String errorId)
    {
        this.errorId = errorId;
    }

    public boolean hasError()
    {
        return errorCode != null;
    }
}
