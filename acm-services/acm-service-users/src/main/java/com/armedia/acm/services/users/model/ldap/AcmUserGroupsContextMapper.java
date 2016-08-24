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
    public static String[] USER_LDAP_ATTRIBUTES = {
            "cn",
            "userAccountControl",
            "sn",
            "givenName",
            "dn",
            "distinguishedname",
            "memberOf"
    };
    private Logger log = LoggerFactory.getLogger(getClass());
    private String userIdAttributeName;
    private String mailAttributeName;

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
        // so we return them, but mark them DISABLED.  The DAO will filter them.
        String uac = MapperUtils.getAttribute(adapter, "userAccountControl");
        if (isUserDisabled(uac))
        {
            log.debug("User '{}' is disabled and won't be synced", fullName);
            user.setUserState("DISABLED");
        }

        user.setLastName(MapperUtils.getAttribute(adapter, "sn"));
        user.setFirstName(MapperUtils.getAttribute(adapter, "givenName"));

        user.setUserId(MapperUtils.getAttribute(adapter, getUserIdAttributeName()));
        user.setMail(MapperUtils.getAttribute(adapter, getMailAttributeName()));
        user.setDistinguishedName(MapperUtils.getAttribute(adapter, "dn", "distinguishedname"));

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

    public String getUserIdAttributeName()
    {
        return userIdAttributeName;
    }

    public void setUserIdAttributeName(String userIdAttributeName)
    {
        this.userIdAttributeName = userIdAttributeName;
    }

    public String getMailAttributeName()
    {
        return mailAttributeName;
    }

    public void setMailAttributeName(String mailAttributeName)
    {
        this.mailAttributeName = mailAttributeName;
    }

}
