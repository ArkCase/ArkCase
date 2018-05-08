package com.armedia.acm.plugins.addressable.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.addressable.exceptions.AcmContactMethodValidationException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

import java.util.List;
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
    public static void validateContactMethodFields(List<ContactMethod> contactMethods) throws AcmContactMethodValidationException
    {
        List<ContactMethod> invalidEmails = contactMethods.stream()
                .filter(m -> "email".equals(m.getType().toLowerCase()))
                .filter(m -> !ContactMethod.EMAIL_ADDRESS_REGEX.matcher(m.getValue()).matches())
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
    }
}
