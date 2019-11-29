package com.armedia.acm.plugins.addressable.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class PhoneRegexConfig
{
    @JsonProperty("phone.phone_regex")
    @Value("${phone.phone_regex}")
    private String phoneRegex;

    //.substring is put here because Yaml is parsing the string and can't accept ^ sign at the beginning
    public String getPhoneRegex()
    {
        return phoneRegex.substring(1, phoneRegex.length() - 1);
    }

}
