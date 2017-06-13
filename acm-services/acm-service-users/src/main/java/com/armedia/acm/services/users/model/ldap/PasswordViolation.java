package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordViolation implements ConstraintValidator<IPasswordValidation, Object> {
    private List<IPasswordValidationRule> passwordRules;

    public PasswordViolation(List<IPasswordValidationRule> passwordRules){
        this.passwordRules = passwordRules;
    }

    @Override
    public void initialize(IPasswordValidation passwordValidation) {

    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext context) {
        LdapUser user = (LdapUser) candidate;
        if(user==null) return false;
        if(user.getAcmUser()==null)  return false;
        String user_Id = user.getAcmUser().getUserId();
        String user_password = user.getPassword();
        if (user_Id == null || user_password == null) return false;
        for (IPasswordValidationRule pattern : passwordRules) {
            String message = pattern.RunValidationAndGetMessage(user_Id, user_password);
            context.disableDefaultConstraintViolation();
            if (message != null) {
                context
                        .buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
