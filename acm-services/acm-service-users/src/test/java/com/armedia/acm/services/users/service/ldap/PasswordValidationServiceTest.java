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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.users.model.PasswordConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.model.ldap.PasswordLengthValidationRule;
import com.armedia.acm.services.users.model.ldap.PasswordShouldMatchPattern;
import com.armedia.acm.services.users.model.ldap.PasswordShouldNotContainUserId;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasswordValidationServiceTest
{
    private PasswordValidationService unit = new PasswordValidationService();
    private PasswordLengthValidationRule minLengthRule = new PasswordLengthValidationRule();
    private PasswordConfig passwordConfig = new PasswordConfig();

    @Before
    public void setUp()
    {
        /**
         * These are the patterns declared in `spring-library-user-service.xml`
         */
        PasswordShouldMatchPattern lowercaseCharRule = new PasswordShouldMatchPattern("^.*?[a-z].*$",
                "Password must contain at least one lowercase character");

        PasswordShouldMatchPattern uppercaseCharRule = new PasswordShouldMatchPattern("^.*?[A-Z].*$",
                "Password must contain at least one uppercase character");

        PasswordShouldMatchPattern digitRule = new PasswordShouldMatchPattern("^.*?[0-9].*$",
                "Password must contain at least one digit (0-9)");

        PasswordShouldMatchPattern specialCharRule = new PasswordShouldMatchPattern(
                "^.*?[\\Q[\\E~!@#$%^&*_+=`|\\(){}:;\"'<>,.?/-\\Q]\\E].*$",
                "Password must contain at least one special character");
        passwordConfig.setPasswordLength(7);
        passwordConfig.setPasswordLengthMessage("Password must be of minimum length of");
        minLengthRule.setPasswordConfig(passwordConfig);

        unit.setPasswordRules(Arrays.asList(new PasswordShouldNotContainUserId(), lowercaseCharRule, uppercaseCharRule, digitRule,
                specialCharRule, minLengthRule));
    }

    @Test
    public void passwordIsValid()
    {
        List<String> errorMessages = unit.validate("ann-acm", "AcMd3v[");
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void passwordIsTooShort()
    {
        List<String> errorMessages = unit.validate("ann-acm", "Ac3$");

        assertTrue(errorMessages.size() == 1);
        assertEquals("Password must be of minimum length of 7", errorMessages.get(0));
    }

    @Test
    public void invalidPassword()
    {
        List<String> errorMessages = unit.validate("ann-acm", "ann-acm\\$\"'?");

        // no digit char, no uppercase letter and contains userId
        assertTrue(errorMessages.size() == 3);
    }

    @Test
    public void generatedPasswordsAreValid()
    {
        List<String> passwords = Stream.generate(() -> MapperUtils.generatePassword(minLengthRule.getMinLength()))
                .limit(50)
                .collect(Collectors.toList());

        List<String> errorMessages = passwords.stream()
                .flatMap(password -> unit.validate("ann-acm", password).stream())
                .collect(Collectors.toList());
        errorMessages.forEach(System.out::println);
        assertTrue(errorMessages.isEmpty());
    }
}
