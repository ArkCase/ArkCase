package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.HashSet;
import java.util.Set;

public class AcmUserGroupsContextMapper implements ContextMapper
{
    public static final int ACTIVE_DIRECTORY_DISABLED_BIT = 2;
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmLdapSyncConfig acmLdapSyncConfig;
    public static String[] USER_LDAP_ATTRIBUTES;

    public AcmUserGroupsContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    @Override
    public AcmUser mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        AcmUser user = setLdapUser(new AcmUser(), adapter);
        log.trace("Retrieved user '{}'", user.getDistinguishedName());
        return user;
    }

    private AcmUser setLdapUser(AcmUser user, DirContextAdapter adapter)
    {
        String fullName = MapperUtils.getAttribute(adapter, "cn");

        user.setFullName(fullName);

        // because of how the LDAP query paging works, we can no longer return null for the disabled accounts.
        // so we return them, but mark them DISABLED. The DAO will filter them.
        String uac = MapperUtils.getAttribute(adapter, "userAccountControl");
        if (isUserDisabled(uac))
        {
            log.debug("User '{}' is disabled and won't be synced", fullName);
            user.setUserState("DISABLED");
        } else
        {
            user.setUserState("VALID");
        }

        user.setLastName(MapperUtils.getAttribute(adapter, "sn"));
        user.setFirstName(MapperUtils.getAttribute(adapter, "givenName"));
        user.setUserId(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getUserIdAttributeName()));
        user.setMail(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getMailAttributeName()));
        user.setCountry(MapperUtils.getAttribute(adapter, "c"));
        user.setCountryAbbreviation(MapperUtils.getAttribute(adapter, "co"));
        user.setCompany(MapperUtils.getAttribute(adapter, "company"));
        user.setDepartment(MapperUtils.getAttribute(adapter, "department"));
        user.setDistinguishedName(String.format("%s,%s", adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        user.setsAMAccountName(MapperUtils.getAttribute(adapter, "samAccountName"));
        user.setUserPrincipalName(MapperUtils.getAttribute(adapter, "userPrincipalName"));
        user.setUid(MapperUtils.getAttribute(adapter, "uid"));
        user.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getAllUsersSortingAttribute()));

        Set<String> ldapGroupsForUser = new HashSet<>();
        if (adapter.attributeExists("memberOf"))
        {
            String[] groupsUserIsMemberOf = adapter.getStringAttributes("memberOf");
            ldapGroupsForUser = MapperUtils.arrayToSet(groupsUserIsMemberOf, MapperUtils.MEMBER_TO_COMMON_NAME_UPPERCASE);
        }
        user.setLdapGroups(ldapGroupsForUser);

        return user;
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

    public static void setUserLdapAttributes(String userLdapAttributes)
    {
        AcmUserGroupsContextMapper.USER_LDAP_ATTRIBUTES = userLdapAttributes.trim().split(",");
    }
}
