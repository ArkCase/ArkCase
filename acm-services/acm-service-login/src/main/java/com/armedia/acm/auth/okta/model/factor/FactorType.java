package com.armedia.acm.auth.okta.model.factor;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum FactorType
{
    @JsonProperty("sms")
    SMS("sms"),
    @JsonProperty("push")
    PUSH("push"),
    @JsonProperty("call")
    CALL("call"),
    @JsonProperty("token")
    TOKEN("token"),
    @JsonProperty("token:software:totp")
    SOFTWARE_TOKEN("token:software:totp"),
    @JsonProperty("token:hardware")
    HARDWARE_TOKEN("token:hardware"),
    @JsonProperty("question")
    QUESTION("question"),
    @JsonProperty("web")
    WEB("web"),
    @JsonProperty("email")
    EMAIL("email");

    private String factorType;

    FactorType(String factorType)
    {
        this.factorType = factorType;
    }

    public String getFactorType()
    {
        return factorType;
    }

    public void setFactorType(String factorType)
    {
        this.factorType = factorType;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("factorType", factorType)
                .toString();
    }
}
