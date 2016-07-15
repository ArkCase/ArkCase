package com.armedia.acm.services.users.model.ldap;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Common operations in mapping LDAP attributes
 */
public class MapperUtils
{
    public static final Function<String, String> MEMBER_TO_COMMON_NAME_UPPERCASE = element -> {
        String groupCommonName = StringUtils.substringBefore(element, ",");
        groupCommonName = StringUtils.substringAfter(groupCommonName, "=");
        return groupCommonName.trim().toUpperCase();
    };

    public static Set<String> arrayToSet(String[] elements, Function<String, String> mapper)
    {
        return Arrays.stream(elements)
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
