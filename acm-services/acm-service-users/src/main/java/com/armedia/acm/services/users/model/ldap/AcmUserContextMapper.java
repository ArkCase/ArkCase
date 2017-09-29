package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;


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
            user.setState(AcmUserState.DISABLED.name());
        } else
        {
            user.setState(AcmUserState.VALID.name());
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
        user.setPasswordExpirationDate(Directory.valueOf(acmLdapSyncConfig.getDirectoryType()).getPasswordExpirationDate(adapter));
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
}
