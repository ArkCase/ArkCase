package com.armedia.acm.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.function.Predicate;

public class AcmSpringActiveProfile
{
    @Autowired
    private Environment environment;

    public boolean isSAMLEnabledEnvironment()
    {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0)
        {
            activeProfiles = environment.getDefaultProfiles();
        }
        Predicate<String> isSamlProfile = it -> it.equals("externalSaml");
        Predicate<String> isExternalSamlProfile = it -> it.equals("ssoSaml");

        return Arrays.stream(activeProfiles)
                .anyMatch(isSamlProfile.or(isExternalSamlProfile));
    }
}
