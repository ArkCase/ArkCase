package com.armedia.acm.services.users.service.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import com.armedia.acm.services.users.model.ldap.PasswordValidation;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, UserDTO>
{
    @Autowired
    PasswordValidationService passwordValidationService;

    @Override
    public void initialize(PasswordValidation passwordValidation)
    {

    }

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context)
    {
        String userId = user.getUserId();
        String userPassword = user.getPassword();
        if (userId == null || userPassword == null) return false;

        context.disableDefaultConstraintViolation();

        List<String> errorMessages = passwordValidationService.validate(user.getUserId(), user.getPassword());
        for (String message : errorMessages)
        {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return errorMessages.isEmpty();
    }

}
