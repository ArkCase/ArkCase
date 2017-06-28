package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, Object> {
    @Resource(name="passwordValidation")
    private List<PasswordValidationRule> passwordRules;

    @Override
    public void initialize(PasswordValidation passwordValidation) {

    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext context) {
        LdapUser user = (LdapUser) candidate;
        if(user==null) return false;
        if(user.getAcmUser()==null)  return false;
        boolean hasPassedValidationRules=true;
        String userId = user.getAcmUser().getUserId();
        String userPassword = user.getPassword();
        if (userId == null || userPassword == null) return false;
        for (PasswordValidationRule pattern : passwordRules) {
            String message = pattern.RunValidationAndGetMessage(userId, userPassword);
            context.disableDefaultConstraintViolation();
            if (message != null) {
                context
                        .buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
                hasPassedValidationRules=false;
            }
        }
        return hasPassedValidationRules;
    }
}
