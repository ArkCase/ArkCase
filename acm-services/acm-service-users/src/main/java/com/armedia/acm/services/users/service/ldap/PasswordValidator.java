package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
        if (userId == null || userPassword == null)
            return false;

        context.disableDefaultConstraintViolation();

        List<String> errorMessages = passwordValidationService.validate(user.getUserId(), user.getPassword());
        for (String message : errorMessages)
        {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return errorMessages.isEmpty();
    }

}
