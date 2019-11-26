package com.armedia.acm.plugins.addressable.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class PhoneRegexConfig
{
    @JsonProperty("phone.phone_regex")
    @Value("${phone.phone_regex}")
    private String phoneRegex;

    public String getPhoneRegex()
    {
        return phoneRegex.substring(1, phoneRegex.length() - 1);
    }

}
