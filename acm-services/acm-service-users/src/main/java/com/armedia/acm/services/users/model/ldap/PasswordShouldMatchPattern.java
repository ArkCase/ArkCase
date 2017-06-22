package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/12/17.
 */
public class PasswordShouldMatchPattern implements PasswordValidationRule {
    private final String pattern;
    private final String message;

    public PasswordShouldMatchPattern(String pattern, String message) {
        this.pattern=pattern;
        this.message=message;
    }

    @Override
    public String RunValidationAndGetMessage(String username, String password) {
        return !password.matches(pattern)?message:null;
    }
}
