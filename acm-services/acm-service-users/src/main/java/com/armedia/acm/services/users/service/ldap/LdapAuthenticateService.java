package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import java.time.LocalDateTime;

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
    private AcmLdapSyncConfig ldapSyncConfig;

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

    public void changeUserPassword(String userName, String currentPassword, String newPassword) throws AcmUserActionFailedException
    {
        log.debug("Changing password for user:{}", userName);
        LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapAuthenticateConfig);
        AcmUser acmUser = userDao.findByUserId(userName);
        try
        {
            ldapUserDao.changeUserPassword(acmUser.getDistinguishedName(), currentPassword, newPassword, ldapTemplate,
                    ldapAuthenticateConfig);
            log.debug("Password changed successfully for User: {}", userName);
            savePasswordExpirationDate(acmUser, ldapTemplate);
        } catch (AcmLdapActionFailedException e)
        {
            throw new AcmUserActionFailedException("change password", "USER", null, "Change password action failed!", null);
        }
    }

    public void resetUserPassword(String token, String password) throws AcmUserActionFailedException
    {
        try
        {
            AcmUser user = userDao.findByPasswordResetToken(token);
            if (user == null)
            {
                throw new AcmUserActionFailedException("reset password", "USER", null, "User not found!", null);
            }
            invalidateToken(user);
            log.debug("Changing password for user:{}", user.getUserId());
            LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapAuthenticateConfig);
            ldapUserDao.changeUserPasswordWithAdministrator(user.getDistinguishedName(), password, ldapTemplate, ldapAuthenticateConfig);
            savePasswordExpirationDate(user, ldapTemplate);
        } catch (AcmLdapActionFailedException e)
        {
            throw new AcmUserActionFailedException("reset password", "USER", null, "Change password action failed!", null);
        }
    }

    protected void savePasswordExpirationDate(AcmUser acmUser, LdapTemplate ldapTemplate)
    {
        // passwordExpirationDate is set by ldap after the entry is there
        AcmUser userEntry = getLdapUserDao().lookupUser(acmUser.getDistinguishedName(), ldapTemplate, ldapSyncConfig);
        acmUser.setPasswordExpirationDate(userEntry.getPasswordExpirationDate());
        userDao.save(acmUser);
    }

    protected void invalidateToken(AcmUser acmUser)
    {
        PasswordResetToken passwordResetToken = acmUser.getPasswordResetToken();
        passwordResetToken.setExpiryDate(LocalDateTime.now().minusDays(1));
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
