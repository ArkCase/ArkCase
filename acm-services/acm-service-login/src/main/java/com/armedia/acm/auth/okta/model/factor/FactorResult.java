package com.armedia.acm.auth.okta.model.factor;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum FactorResult
{
    SUCCESS("SUCCESS"),
    CHALLENGE("CHALLENGE"),
    WAITING("WAITING"),
    FAILED("FAILED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    TIMEOUT("TIMEOUT"),
    TIME_WINDOW_EXCEEDED("TIME_WINDOW_EXCEEDED"),
    PASSCODE_REPLAYED("PASSCODE_REPLAYED"),
    ERROR("ERROR");


    private String result;

    FactorResult(String result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("result", result)
                .toString();
    }
}
