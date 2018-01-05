package com.armedia.acm.auth;

import javax.naming.Name;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import com.armedia.acm.services.alfresco.ldap.syncer.AlfrescoLdapSyncer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.service.ldap.LdapSyncService;

/**
 * Created by riste.tutureski on 4/11/2016.
 */
public class AcmLdapAuthenticationProvider extends LdapAuthenticationProvider
{
    private UserDao userDao;
    private LdapSyncService ldapSyncService;

    private AlfrescoLdapSyncer alfrescoLdapSyncer;

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
        String principal = StringUtils.substringBeforeLast(authentication.getName(), "@");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials());

        DirContextOperations dirContextOperations = super.doAuthentication(token);

        Name dn = dirContextOperations.getDn();
        AcmUser user = getUserDao().findByUserId(authentication.getName());

        if (user == null || AcmUserState.VALID != user.getUserState())
        {
            alfrescoLdapSyncer.initiateSync();
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

    /**
     * @param alfrescoLdapSyncer
     *            the alfrescoLdapSyncer to set
     */
    public void setAlfrescoLdapSyncer(AlfrescoLdapSyncer alfrescoLdapSyncer)
    {
        this.alfrescoLdapSyncer = alfrescoLdapSyncer;
    }
}