package com.armedia.acm.auth;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.ldap.LdapSyncService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import javax.naming.Name;

/**
 * Created by riste.tutureski on 4/11/2016.
 */
public class AcmLdapAuthenticationProvider extends LdapAuthenticationProvider
{
    private UserDao userDao;
    private LdapSyncService ldapSyncService;

    public AcmLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator)
    {
        super(authenticator, authoritiesPopulator);
    }

    public AcmLdapAuthenticationProvider(LdapAuthenticator authenticator)
    {
        super(authenticator);
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication)
    {
        DirContextOperations dirContextOperations = super.doAuthentication(authentication);

        Name dn = dirContextOperations.getDn();

        AcmUser user = getUserDao().findByUserId(authentication.getName());

        if (user == null || !"VALID".equalsIgnoreCase(user.getUserState()))
        {
            getLdapSyncService().syncUserByDn(dn.toString());
        }

        return dirContextOperations;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LdapSyncService getLdapSyncService()
    {
        return ldapSyncService;
    }

    public void setLdapSyncService(LdapSyncService ldapSyncService)
    {
        this.ldapSyncService = ldapSyncService;
    }
}
