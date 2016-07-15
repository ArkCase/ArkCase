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

    private String userIdAttributeName;
    private String mailAttributeName;

    @Override
    public AcmUser mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        String fullName = adapter.getStringAttribute("cn");

        // exclude AD disabled users
        if (adapter.attributeExists("userAccountControl"))
        {
            if (isUserDisabled(adapter))
            {
                log.info("User '{}' is disabled and won't be synced", fullName);
                return null;
            }
        }
        AcmUser user = setLdapUser(new AcmUser(), adapter);
        log.debug("Retrieved user '{}'", user.getDistinguishedName());
        return user;
    }

    private AcmUser setLdapUser(AcmUser user, DirContextAdapter adapter)
    {
        user.setFullName(adapter.getStringAttribute("cn"));

        if (adapter.attributeExists("sn"))
        {
            user.setLastName(adapter.getStringAttribute("sn"));
        }
        if (adapter.attributeExists("givenName"))
        {
            user.setFirstName(adapter.getStringAttribute("givenName"));
        }

        user.setUserId(adapter.getStringAttribute(getUserIdAttributeName()));
        user.setMail(adapter.getStringAttribute(getMailAttributeName()));
        user.setDistinguishedName(adapter.getStringAttribute("dn") != null ?
                adapter.getStringAttribute("dn") : adapter.getStringAttribute("distinguishedname"));

        Set<String> ldapGroupsForUser = new HashSet<>();
        if (adapter.attributeExists("memberOf"))
        {
            String[] groupsUserIsMemberOf = adapter.getStringAttributes("memberOf");
            ldapGroupsForUser = MapperUtils.arrayToSet(groupsUserIsMemberOf, MapperUtils.MEMBER_TO_COMMON_NAME_UPPERCASE);
        }
        user.setLdapGroups(ldapGroupsForUser);

        return user;
    }

    protected boolean isUserDisabled(DirContextAdapter adapter)
    {
        String uac = adapter.getStringAttribute("userAccountControl");
        long userAccountControl = Long.valueOf(uac).longValue();
        boolean disabled = (userAccountControl & ACTIVE_DIRECTORY_DISABLED_BIT) == ACTIVE_DIRECTORY_DISABLED_BIT;
        return disabled;
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
