package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.Attribute;

/**
 * Created by armdev on 5/29/14.
 */
public class AcmLdapEntityContextMapper implements ContextMapper
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public static final int ACTIVE_DIRECTORY_DISABLED_BIT = 2;

    private String userIdAttributeName;

    @Override
    public AcmLdapEntity mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        String fullName = adapter.getStringAttribute("cn");

        // exclude AD disabled users
        if ( adapter.attributeExists("userAccountControl") )
        {
            if ( isUserDisabled(adapter) )
            {
                if ( log.isInfoEnabled() )
                {
                    log.info("User '" + fullName + "' is disabled; will not be synced.");
                }
                return null;
            }
        }


        Attribute objectClasses = adapter.getAttributes().get("objectClass");
        boolean isGroup = objectClasses.contains("group") || objectClasses.contains("groupOfNames");

        log.debug("Ldap Entity '" + fullName + "' is a group? " + isGroup);

        AcmLdapEntity retval;
        if ( isGroup )
        {
            retval = generateAcmRole(adapter);
        }
        else
        {
            retval = generateAcmUser(adapter);
        }

        return retval;
    }

    private AcmRole generateAcmRole(DirContextAdapter adapter)
    {
        AcmRole role = new AcmRole();
        role.setRoleName(adapter.getStringAttribute("cn"));
        role.setRoleType("LDAP_GROUP");

        return role;
    }

    private AcmUser generateAcmUser(DirContextAdapter adapter)
    {
        AcmUser retval = new AcmUser();

        retval.setFullName(adapter.getStringAttribute("cn"));

        if ( adapter.attributeExists("sn") )
        {
            retval.setLastName(adapter.getStringAttribute("sn"));
        }
        if ( adapter.attributeExists("givenName") )
        {
            retval.setFirstName(adapter.getStringAttribute("givenName"));
        }
        retval.setUserId(adapter.getStringAttribute(getUserIdAttributeName()));

        retval.setDistinguishedName(adapter.getStringAttribute("dn"));

        return retval;
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
}
