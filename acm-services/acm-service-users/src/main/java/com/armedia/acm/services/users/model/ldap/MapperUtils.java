package com.armedia.acm.services.users.model.ldap;

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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import javax.annotation.Nullable;
import javax.naming.directory.BasicAttribute;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Common operations in mapping LDAP attributes
 */
public class MapperUtils
{
    public static final Function<DirContextAdapter, LocalDate> convertFileTimeTimestampToDate = adapter -> {
        String expirationTimePasswordAttr = MapperUtils.getAttribute(adapter, "msDS-UserPasswordExpiryTimeComputed");
        if (expirationTimePasswordAttr != null)
        {
            // FILETIME - representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
            long fileTimeTimestamp = Long.parseLong(expirationTimePasswordAttr);
            // 116444736000000000 100ns between 1601 and 1970
            // https://stackoverflow.com/questions/5200192/convert-64-bit-windows-number-to-time-java
            long mmSecTimestamp = (fileTimeTimestamp - 116444736000000000L) / 10000;
            Instant instant = Instant.ofEpochMilli(mmSecTimestamp);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDate localDate = localDateTime.toLocalDate();
            // prevent "Data truncation: Incorrect date value" on mysql when date exceeds valid range
            if (localDate.isAfter(LocalDate.now().plusYears(100L)))
            {
                return null;
            }
            return localDate;
        }
        return null;
    };
    public static final Function<DirContextAdapter, LocalDate> calculatePasswordExpirationDateByShadowAccount = adapter -> {
        String shadowMaxAttr = MapperUtils.getAttribute(adapter, "shadowMax");
        String shadowLastChangeAttr = MapperUtils.getAttribute(adapter, "shadowLastChange");
        if (shadowLastChangeAttr != null && shadowMaxAttr != null)
        {
            int passwordValidDays = Integer.parseInt(shadowMaxAttr);
            // days since Jan 1, 1970 that password was last changed
            int passwordLastChangedDays = Integer.parseInt(shadowLastChangeAttr);
            LocalDate date = LocalDate.ofEpochDay(0);
            // calculate the date when password was last changed
            date = date.plusDays(passwordLastChangedDays);
            // calculate last date the password must be changed
            date = date.plusDays(passwordValidDays);
            return date;
        }
        return null;
    };
    public static final Function<String, BasicAttribute> openLdapPasswordToAttribute = password -> new BasicAttribute("userPassword",
            password.getBytes());
    public static final Function<String, BasicAttribute> openLdapCurrentPasswordToAttribute = password -> new BasicAttribute(
            "userPassword");
    private static Logger log = LogManager.getLogger(MapperUtils.class);
    public static final Function<String, BasicAttribute> activeDirectoryPasswordToAttribute = password -> {
        final byte[] passwordBytes;
        passwordBytes = MapperUtils.encodeUTF16LE(password);
        return new BasicAttribute("unicodePwd", passwordBytes);

    };

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
            }
            catch (BadLdapGrammarException | IllegalArgumentException e)
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

    public static String appendToDn(String dn, String... suffixes)
    {
        DistinguishedName distinguishedName = new DistinguishedName(dn);
        Arrays.stream(suffixes).forEach(suffix -> {
            DistinguishedName suffixDn = new DistinguishedName(suffix);
            distinguishedName.prepend(suffixDn);
        });
        return distinguishedName.toString();
    }

    public static String buildGroupName(String name, String domain)
    {
        Optional<String> optionalDomain = Optional.of(domain);
        return String.format("%s%s", name, optionalDomain.map(it -> String.format("@%s", it))
                .orElse(""))
                .toUpperCase();
    }

    public static String buildGroupName(String name, AcmLdapSyncConfig ldapSyncConfig)
    {
        String groupPrefix = ldapSyncConfig.getGroupPrefix();
        if (StringUtils.isNotBlank(groupPrefix) && !name.startsWith(groupPrefix))
        {
            String groupName = String.format("%s.%s", groupPrefix, name);
            return buildGroupName(groupName, ldapSyncConfig.getUserDomain());
        }
        return buildGroupName(name, ldapSyncConfig.getUserDomain());
    }

    public static String buildUserId(String userId, String domain)
    {
        Optional<String> optionalDomain = Optional.of(domain);
        return String.format("%s%s", userId, optionalDomain.map(it -> String.format("@%s", it))
                .orElse(""))
                .toLowerCase();
    }

    public static String buildUserId(String userId, AcmLdapSyncConfig ldapSyncConfig)
    {
        String userPrefix = ldapSyncConfig.getUserPrefix();
        if (StringUtils.isNotBlank(userPrefix) &&
                ldapSyncConfig.getUserIdAttributeName().equalsIgnoreCase("samaccountname")
                && !userId.startsWith(userPrefix))
        {
            String username = String.format("%s.%s", userPrefix, userId);
            username = StringUtils.left(username, 20);
            return buildUserId(username, ldapSyncConfig.getUserDomain());
        }
        return buildUserId(userId, ldapSyncConfig.getUserDomain());

    }

    public static byte[] encodeUTF16LE(String str)
    {
        return String.format("\"%s\"", str).getBytes(StandardCharsets.UTF_16LE);
    }

    public static String generatePassword(int minLength)
    {
        String specialChar = RandomStringUtils.random(1, "~!@#$%^?");
        String lcsPart = RandomStringUtils.random(minLength, "abcdefghijklmnopqrstuvwxyz");
        String ucsPart = RandomStringUtils.random(2, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        String digitChar = RandomStringUtils.random(2, "0123456789");
        return String.format("%s%s%s%s", specialChar, lcsPart, ucsPart, digitChar);
    }

    public static String buildPrincipalName(String principal, @Nullable String userPrefix, @Nullable String userDomain)
    {
        boolean userDomainIncluded = StringUtils.substringAfterLast(principal, "@").equals(userDomain);

        if (StringUtils.isNotBlank(userPrefix) && !StringUtils.startsWith(principal, userPrefix))
        {
            principal = String.format("%s.%s", userPrefix, principal);
            if (userDomainIncluded)
            {
                String userIdDomainTruncated = StringUtils.substringBeforeLast(principal, "@");
                principal = StringUtils.left(userIdDomainTruncated, 20);
                principal = String.format("%s@%s", principal, userDomain);
            }
            else
            {
                principal = StringUtils.left(principal, 20);
            }
        }

        if (StringUtils.isNotBlank(userDomain) && !userDomainIncluded)
        {
            principal = String.format("%s@%s", principal, userDomain);
        }

        return principal;
    }
}
