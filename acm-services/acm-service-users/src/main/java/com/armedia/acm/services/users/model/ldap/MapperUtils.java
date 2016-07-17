package com.armedia.acm.services.users.model.ldap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Common operations in mapping LDAP attributes
 */
public class MapperUtils
{
    public static final Function<String, String> MEMBER_TO_COMMON_NAME_UPPERCASE = element ->
    {
        if (StringUtils.isBlank(element))
        {
            return "";
        }
        String[] parts = element.split(",\\w+="); // eg. (,ou=)
        String commonName = parts[0];
        commonName = StringUtils.substringAfter(commonName, "=");
        if (commonName.contains(","))
        {
            commonName = commonName.replaceAll(",\\s*", " ");
        }
        return commonName.trim().toUpperCase();
    };

    public static Set<String> arrayToSet(String[] elements, Function<String, String> mapper)
    {
        return Arrays.stream(elements)
                .map(mapper)
                .collect(Collectors.toSet());
    }

    public static String getAttribute(DirContextAdapter adapter, String... names)
    {
        for (String name : names)
        {
            String result = adapter.attributeExists(name) ? adapter.getStringAttribute(name) : null;
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }
}
