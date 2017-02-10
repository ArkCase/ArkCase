package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Authenticates a user id and password against LDAP directory.  To support multiple LDAP configurations, create multiple Spring
 * beans, each with its own LdapAuthenticateService.
 */
public class LdapAuthenticateService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;
    private SpringLdapUserDao ldapUserDao;
    private SpringLdapDao ldapDao;
    private AcmLdapAuthenticateConfig ldapAuthenticateConfig;

    /*
     * Authenticates user against LDAP
     */
    public Boolean authenticate(String userName, String password)
    {
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());

        String userIdAttributeName = getLdapAuthenticateConfig().getUserIdAttributeName();
        String searchBase = getLdapAuthenticateConfig().getSearchBase();

        String filter = "(" + userIdAttributeName + "=" + userName + ")";
        boolean authenticated = template.authenticate(searchBase, filter, password);

        log.debug("searchBase[{}], filter[{}], authenticated[{}]", searchBase, filter, authenticated);

        return authenticated;
    }


    public void changeUserPassword(String userName, String password)
    {
        log.debug("Changing password for user:{}", userName);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());
        AcmUser acmUser = userDao.findByUserId(userName);
        ldapUserDao.changeUserPassword(acmUser.getDistinguishedName(), password, ldapTemplate, getLdapAuthenticateConfig());
        log.debug("Password changed successfully for user:{}", userName);
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public AcmLdapAuthenticateConfig getLdapAuthenticateConfig()
    {
        return ldapAuthenticateConfig;
    }

    public void setLdapAuthenticateConfig(
            AcmLdapAuthenticateConfig ldapAuthenticateConfig)
    {
        this.ldapAuthenticateConfig = ldapAuthenticateConfig;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SpringLdapUserDao getLdapUserDao()
    {
        return ldapUserDao;
    }

    public void setLdapUserDao(SpringLdapUserDao ldapUserDao)
    {
        this.ldapUserDao = ldapUserDao;
    }
}
