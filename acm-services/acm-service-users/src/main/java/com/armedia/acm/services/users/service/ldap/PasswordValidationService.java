package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.ldap.PasswordValidationRule;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PasswordValidationService
{
    private List<PasswordValidationRule> passwordRules;

    public List<String> validate(String userId, String password)
    {
        return passwordRules.stream()
                .map(rule -> rule.runValidationAndGetMessage(userId, password))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void setPasswordRules(List<PasswordValidationRule> passwordRules)
    {
        this.passwordRules = passwordRules;
    }
}
