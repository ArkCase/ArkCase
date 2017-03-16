package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base LDAP Controller
 */
public class SecureLdapController
{
    protected SpringContextHolder acmContextHolder;
    private Logger log = LoggerFactory.getLogger(getClass());

    protected void checkIfLdapManagementIsAllowed(String directory) throws AcmAppErrorJsonMsg
    {
        if (!isLdapManagementEnabled(directory))
        {
            log.warn("Updates on {} LDAP directory are not allowed!", directory);
            throw new AcmAppErrorJsonMsg(String.format("Updates on %s LDAP directory are not allowed!",
                    directory), null, "null", null);
        }
    }

    protected boolean isLdapManagementEnabled(String directory)
    {
        AcmLdapAuthenticateConfig acmLdapAuthenticateConfig = acmContextHolder.getAllBeansOfType(AcmLdapAuthenticateConfig.class).
                get(String.format("%s_authenticate", directory));
        if (acmLdapAuthenticateConfig != null)
        {
            return acmLdapAuthenticateConfig.getEnableEditingLdapUsers();
        }
        return false;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }
}
