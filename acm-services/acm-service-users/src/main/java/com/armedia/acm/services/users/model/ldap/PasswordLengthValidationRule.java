package com.armedia.acm.services.users.model.ldap;

public class PasswordLengthValidationRule implements PasswordValidationRule
{
    private final String message;
    private final int minLength;

    public PasswordLengthValidationRule(int minLength, String message)
    {
        this.minLength = minLength;
        this.message = message;
    }

    @Override
    public String runValidationAndGetMessage(String username, String password)
    {

        return password.length() >= minLength ? null : message;
    }

    public int getMinLength()
    {
        return minLength;
    }
}
