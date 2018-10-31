package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class AcmSpringActiveProfile
{
    @Autowired
    private Environment environment;

    public boolean isSAMLEnabledEnvironment()
    {
        String[] activeProfiles = getActiveProfiles();
        Predicate<String> isSamlProfile = it -> it.equals("externalSaml");
        Predicate<String> isExternalSamlProfile = it -> it.equals("ssoSaml");

        return Arrays.stream(activeProfiles)
                .anyMatch(isSamlProfile.or(isExternalSamlProfile));
    }

    public String[] getActiveProfiles()
    {
        if (environment.getActiveProfiles().length > 0)
        {
            return environment.getActiveProfiles();
        }
        else
        {
            return environment.getDefaultProfiles();
        }
    }

    public Optional<String> getExtensionActiveProfile()
    {
        String[] activeProfiles = getActiveProfiles();
        final String prefix = "extension-";
        return Arrays.stream(activeProfiles)
                .filter(it -> it.startsWith(prefix))
                .map(it -> it.substring(prefix.length()))
                .findFirst();
    }
}
