package com.armedia.acm.auth.okta.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum ProviderType
{
    OKTA("OKTA"), RSA("RSA"), SYMANTEC("SYMANTEC"), GOOGLE("GOOGLE"), DUO("DUO"), YUBICO("YUBICO");

    private String provider;

    ProviderType(String provider)
    {
        this.provider = provider;
    }

    public String getProviderType()
    {
        return provider;
    }

    public void setProviderType(String provider)
    {
        this.provider = provider;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("provider", provider)
                .toString();
    }
}
