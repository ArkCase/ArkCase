package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Authenticates a user id and password against LDAP directory. To support multiple LDAP configurations, create multiple
 * Spring
 * beans, each with its own LdapAuthenticateService.
 */
public class LdapAuthenticateService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;
    private SpringLdapUserDao ldapUserDao;
    private SpringLdapDao ldapDao;
    private AcmLdapAuthenticateConfig ldapAuthenticateConfig;
    private AcmLdapSyncConfig ldapSyncConfig;

    /*
     * Authenticates user against LDAP
     */
    public Boolean authenticate(String userName, String password)
    {
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());

        String userIdAttributeName = getLdapAuthenticateConfig().getUserIdAttributeName();
        String searchBase = getLdapAuthenticateConfig().getSearchBase();

        userName = StringUtils.substringBeforeLast(userName, "@");
        String filter = "(" + userIdAttributeName + "=" + userName + ")";
        boolean authenticated = template.authenticate(searchBase, filter, password);

        log.debug("searchBase[{}], filter[{}], authenticated[{}]", searchBase, filter, authenticated);

        return authenticated;
    }

    public void changeUserPassword(String userName, String currentPassword, String newPassword) throws AcmUserActionFailedException
    {
        log.debug("Changing password for user:{}", userName);
        AcmUser acmUser = userDao.findByUserId(userName);
        LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapAuthenticateConfig, acmUser.getDistinguishedName(), currentPassword);
        try
        {
            ldapUserDao.changeUserPassword(acmUser.getDistinguishedName(), currentPassword, newPassword, ldapTemplate,
                    ldapAuthenticateConfig);
            log.debug("Password changed successfully for User: {}", userName);
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmUserActionFailedException("change password", "USER", null, "Change password action failed!", null);
        }
        try
        {
            ldapTemplate = ldapDao.buildLdapTemplate(ldapAuthenticateConfig, acmUser.getDistinguishedName(), newPassword);
            savePasswordExpirationDate(acmUser, ldapTemplate);
        }
        catch (Exception e)
        {
            log.warn("Password expiration date was not set for user [{}]", acmUser.getUserId(), e);
        }
    }

    public void resetUserPassword(String token, String password) throws AcmUserActionFailedException
    {
        AcmUser user = userDao.findByPasswordResetToken(token);
        if (user == null)
        {
            throw new AcmUserActionFailedException("reset password", "USER", null, "User not found!", null);
        }
        LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapAuthenticateConfig);
        try
        {
            log.debug("Changing password for user: [{}]", user.getUserId());

            ldapUserDao.changeUserPasswordWithAdministrator(user.getDistinguishedName(), password, ldapTemplate, ldapAuthenticateConfig);
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmUserActionFailedException("reset password", "USER", null, "Change password action failed!", e);
        }
        try
        {
            savePasswordExpirationDate(user, ldapTemplate);
            invalidateToken(user);
        }
        catch (AuthenticationException e)
        {
            log.warn("Password expiration date was not set for user [{}]", user.getUserId(), e);
        }
    }

    protected void savePasswordExpirationDate(AcmUser acmUser, LdapTemplate ldapTemplate)
    {
        // passwordExpirationDate is set by ldap after the entry is there
        LdapUser userEntry = getLdapUserDao().lookupUser(acmUser.getDistinguishedName(), ldapTemplate, ldapSyncConfig);
        acmUser.setPasswordExpirationDate(userEntry.getPasswordExpirationDate());
        userDao.save(acmUser);
    }

    protected void invalidateToken(AcmUser acmUser)
    {
        acmUser.setPasswordResetToken(null);
        userDao.save(acmUser);
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

    public void setLdapAuthenticateConfig(AcmLdapAuthenticateConfig ldapAuthenticateConfig)
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

    public AcmLdapSyncConfig getLdapSyncConfig()
    {
        return ldapSyncConfig;
    }

    public void setLdapSyncConfig(AcmLdapSyncConfig ldapSyncConfig)
    {
        this.ldapSyncConfig = ldapSyncConfig;
    }
}
