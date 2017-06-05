package com.armedia.acm.services.users.model.ldap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Common operations in mapping LDAP attributes
 */
public class MapperUtils
{
    private static Logger log = LoggerFactory.getLogger(MapperUtils.class);

    public static final Function<String, String> MEMBER_TO_COMMON_NAME_UPPERCASE = element ->
    {
        if (StringUtils.isBlank(element))
        {
            return "";
        }
        try
        {
            DistinguishedName dn = new DistinguishedName(element);
            return dn.getValue("cn").toUpperCase();
        } catch (BadLdapGrammarException e)
        {
            return "";
        }
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

    public static String stripBaseFromDn(String dn, String base)
    {
        if (dn.endsWith(base))
        {
            base = "," + base;
            dn = dn.substring(0, dn.indexOf(base));
        }
        return dn;
    }

    public static byte[] encodeUTF16LE(String str)
    {
        try
        {
            return String.format("\"%s\"", str).getBytes("UTF-16LE");

        } catch (UnsupportedEncodingException e)
        {
            log.warn("Unsupported encoding", e);
        }
        return str.getBytes();
    }
}
