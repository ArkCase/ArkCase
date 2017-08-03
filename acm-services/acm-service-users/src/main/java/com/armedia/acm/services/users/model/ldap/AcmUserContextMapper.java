package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmUserContextMapper implements ContextMapper
{
    public static final int ACTIVE_DIRECTORY_DISABLED_BIT = 2;
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmLdapSyncConfig acmLdapSyncConfig;

    public AcmUserContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    @Override
    public LdapUser mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        LdapUser user = mapToLdapUser(adapter);
        log.trace("Retrieved user [{}]", user.getDistinguishedName());
        return user;
    }

    protected LdapUser mapToLdapUser(DirContextAdapter adapter)
    {
        LdapUser user = new LdapUser();
        user.setDirectoryName(acmLdapSyncConfig.getDirectoryName());
        user.setLastName(MapperUtils.getAttribute(adapter, "sn"));
        user.setFirstName(MapperUtils.getAttribute(adapter, "givenName"));

        String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());
        user.setFullName(fullName);

        // because of how the LDAP query paging works, we can no longer return null for the disabled accounts.
        // so we return them, but mark them DISABLED. The DAO will filter them.
        String uac = MapperUtils.getAttribute(adapter, "userAccountControl");
        if (isUserDisabled(uac))
        {
            log.debug("User [{}] is disabled and won't be synced", fullName);
            user.setState("DISABLED");
        } else
        {
            user.setState("VALID");
        }

        user.setDistinguishedName(MapperUtils.appendBaseToDn(adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        user.setUserId(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getUserIdAttributeName()));
        user.setMail(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getMailAttributeName()));
        user.setCountry(MapperUtils.getAttribute(adapter, "co"));
        user.setCountryAbbreviation(MapperUtils.getAttribute(adapter, "c"));
        user.setCompany(MapperUtils.getAttribute(adapter, "company"));
        user.setDepartment(MapperUtils.getAttribute(adapter, "department"));
        user.setTitle(MapperUtils.getAttribute(adapter, "title"));
        user.setsAMAccountName(MapperUtils.getAttribute(adapter, "samAccountName"));
        user.setUserPrincipalName(MapperUtils.getAttribute(adapter, "userPrincipalName"));
        user.setUid(MapperUtils.getAttribute(adapter, "uid"));
        user.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getAllUsersSortingAttribute()));

        Set<String> ldapGroupsForUser = new HashSet<>();
        if (adapter.attributeExists("memberOf"))
        {
            String[] groupsUserIsMemberOf = adapter.getStringAttributes("memberOf");
            ldapGroupsForUser = MapperUtils.mapAttributes(groupsUserIsMemberOf, MapperUtils.getRdnMappingFunction("cn"))
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
        }
        user.setLdapGroups(ldapGroupsForUser);
        user.setPasswordExpirationDate(getPasswordExpirationDate(adapter));
        return user;
    }

    private LocalDate getPasswordExpirationDate(DirContextAdapter adapter)
    {
        if (Directory.ACTIVE_DIRECTORY.getType().equals(acmLdapSyncConfig.getDirectoryType()))
        {
            String expirationTimePasswordAttr = MapperUtils.getAttribute(adapter, "msDS-UserPasswordExpiryTimeComputed");
            if (expirationTimePasswordAttr != null)
            {
                return convertFileTimeTimestampToDate(expirationTimePasswordAttr);
            }
        } else if (Directory.OPEN_LDAP.getType().equals(acmLdapSyncConfig.getDirectoryType()))
        {
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
        }
        return null;
    }

    private LocalDate convertFileTimeTimestampToDate(String expirationTimePasswordAttr)
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
            localDate = null;
        }
        return localDate;
    }

    protected boolean isUserDisabled(String uac)
    {
        try
        {
            long userAccountControl = Long.valueOf(uac);
            return (userAccountControl & ACTIVE_DIRECTORY_DISABLED_BIT) == ACTIVE_DIRECTORY_DISABLED_BIT;
        } catch (NumberFormatException nfe)
        {
            log.warn("user account control value [{}] is not a number!", uac);
            return false;
        }
    }
}
