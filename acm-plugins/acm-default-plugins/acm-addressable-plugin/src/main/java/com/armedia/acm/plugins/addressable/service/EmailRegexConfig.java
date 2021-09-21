package com.armedia.acm.plugins.addressable.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class EmailRegexConfig {
    @JsonProperty("email.email_regex")
    @Value("${email.email_regex}")
    private String emailRegex;

    //.substring is put here because Yaml is parsing the string and can't accept ^ sign at the beginning
    public String getEmailRegex()
    {
        return emailRegex.substring(1, emailRegex.length() - 1);
    }
}