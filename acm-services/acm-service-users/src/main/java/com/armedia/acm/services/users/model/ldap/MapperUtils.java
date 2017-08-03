package com.armedia.acm.services.users.model.ldap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Common operations in mapping LDAP attributes
 */
public class MapperUtils
{
    private static Logger log = LoggerFactory.getLogger(MapperUtils.class);

    public static Function<String, String> getRdnMappingFunction(final String key)
    {
        return element -> {
            if (StringUtils.isBlank(element))
            {
                return "";
            }
            try
            {
                DistinguishedName dn = new DistinguishedName(element);
                return dn.getValue(key);
            } catch (BadLdapGrammarException | IllegalArgumentException e)
            {
                log.warn("No RDN with the requested key [{}]", key);

                return "";
            }
        };
    }

    public static Stream<String> mapAttributes(String[] elements, Function<String, String> mapper)
    {
        return Arrays.stream(elements)
                .map(mapper);
    }

    public static String getAttribute(DirContextAdapter adapter, String... names)
    {
        return Arrays.stream(names)
                .filter(adapter::attributeExists)
                .map(adapter::getStringAttribute)
                .findFirst()
                .orElse(null);
    }

    public static String stripBaseFromDn(String dn, String base)
    {
        DistinguishedName distinguishedName = new DistinguishedName(dn);
        DistinguishedName baseDn = new DistinguishedName(base);
        if (distinguishedName.startsWith(baseDn))
        {
            distinguishedName.removeFirst(baseDn);
        }
        return distinguishedName.toString();
    }

    public static String appendBaseToDn(String dn, String base)
    {
        DistinguishedName distinguishedName = new DistinguishedName(dn);
        DistinguishedName baseDn = new DistinguishedName(base);
        distinguishedName.prepend(baseDn);
        return distinguishedName.toString();
    }

    public static byte[] encodeUTF16LE(String str) throws UnsupportedEncodingException
    {
        return String.format("\"%s\"", str).getBytes("UTF-16LE");
    }
}
