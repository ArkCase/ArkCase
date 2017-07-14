package com.armedia.acm.services.users.service.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.PasswordValidation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, LdapUser>
{
    @Autowired
    PasswordValidationService passwordValidationService;

    @Override
    public void initialize(PasswordValidation passwordValidation)
    {

    }

    @Override
    public boolean isValid(LdapUser user, ConstraintValidatorContext context)
    {
        if (user == null || user.getAcmUser() == null) return false;
        String userId = user.getAcmUser().getUserId();
        String userPassword = user.getPassword();
        if (userId == null || userPassword == null) return false;

        context.disableDefaultConstraintViolation();

        List<String> errorMessages = passwordValidationService.validate(user.getAcmUser().getUserId(), user.getPassword());
        for (String message : errorMessages)
        {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return errorMessages.isEmpty();
    }

}
