package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordViolation implements
            ConstraintValidator<PasswordValidation, Object> {
    private static final String RULE_PATTERN = "/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#%^+=]).*$/";

    @Override
    public void initialize(PasswordValidation arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        LdapUser user = (LdapUser) candidate;
        return user.getPassword().contains(user.getAcmUser().getUserId()) || user.getPassword().matches(RULE_PATTERN);
    }
}
