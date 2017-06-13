package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/12/17.
 */
public class PasswordShouldMatchPattern implements IPasswordValidationRule {
    private String pattern;
    private String message;

    public PasswordShouldMatchPattern(String pattern, String message) {
        this.pattern=pattern;
        this.message=message;
    }

    @Override
    public String RunValidationAndGetMessage(String username, String password) {
        if(!password.matches(pattern))
            return message;
        return null;
    }
}
