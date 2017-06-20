package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/12/17.
 */
public class PasswordShouldNotContainUserId implements PasswordValidationRule {
    @Override
    public String RunValidationAndGetMessage(String username, String password) {
        if(username!=null && !username.isEmpty())
            if(password.contains(username))
                return "Password cannot contain username.";
        return null;
    }
}
