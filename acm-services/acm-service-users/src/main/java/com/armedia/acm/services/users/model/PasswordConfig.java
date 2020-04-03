package com.armedia.acm.services.users.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class PasswordConfig {

    @JsonProperty("password.length")
    @Value("${password.length}")
    private Integer passwordLength;

    @JsonProperty("password.lengthMessage")
    @Value("${password.lengthMessage}")
    private String passwordLengthMessage;

    public Integer getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(Integer passwordLength) {
        this.passwordLength = passwordLength;
    }

    public String getPasswordLengthMessage() {
        return passwordLengthMessage;
    }

    public void setPasswordLengthMessage(String passwordLengthMessage) {
        this.passwordLengthMessage = passwordLengthMessage;
    }
}