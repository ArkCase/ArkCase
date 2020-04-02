package com.armedia.acm.plugins.addressable.service;

/*-
 * #%L
 * ACM Default Plugin: Addressable
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.addressable.exceptions.AcmContactMethodValidationException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ContactMethodsUtil
{

    /**
     * Validates the {@link com.armedia.acm.plugins.addressable.model.ContactMethod} fields.
     *
     * @param contactMethods
     *            the {@link ContactMethod} to validate
     * @throws AcmCreateObjectFailedException
     *             when at least one of the {@link ContactMethod} is not valid.
     */
    public static void validateContactMethodFields(List<ContactMethod> contactMethods, PhoneRegexConfig phoneRegexConfig)
            throws AcmContactMethodValidationException
    {
        Pattern phoneRegex = Pattern.compile(phoneRegexConfig.getPhoneRegex(), Pattern.CASE_INSENSITIVE);
        List<ContactMethod> invalidEmails = contactMethods.stream()
                .filter(m -> "email".equals(m.getType().toLowerCase()))
                .filter(m -> m.getValue() != null && !m.getValue().isEmpty())
                .filter(m -> !ContactMethod.EMAIL_ADDRESS_REGEX.matcher(m.getValue()).matches())
                .collect(Collectors.toList());

        List<ContactMethod> invalidPhones = contactMethods.stream()
                .filter(m -> "phone".equals(m.getType().toLowerCase()))
                .filter(m -> m.getValue() != null && !m.getValue().isEmpty())
                .filter(m -> !phoneRegex.matcher(m.getValue()).matches())
                .collect(Collectors.toList());

        if (invalidEmails.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Invalid email in Contact Method!");
            invalidEmails.forEach(invalid -> {
                errorMessage.append(" [ContactMethodId: " + invalid.getId() + " emailValue: " + invalid.getValue() + "]");
            });
            throw new AcmContactMethodValidationException(errorMessage.toString(), null);
        }
        if (invalidPhones.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Invalid phone in Contact Method!");
            invalidPhones.forEach(invalid -> {
                errorMessage.append(" [ContactMethodId: " + invalid.getId() + " phoneValue: " + invalid.getValue() + "]");
            });
            throw new AcmContactMethodValidationException(errorMessage.toString(), null);
        }

    }
}
