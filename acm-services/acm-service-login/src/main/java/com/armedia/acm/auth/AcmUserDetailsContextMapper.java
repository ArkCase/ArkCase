/**
 * 
 */
package com.armedia.acm.auth;

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 * @author nadica.cuculova
 *
 */
public class AcmUserDetailsContextMapper extends LdapUserDetailsMapper
{

    private AcmLdapSyncConfig acmLdapSyncConfig;

    /**
     * @param acmLdapSyncConfig
     */
    public AcmUserDetailsContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        super();
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.security.ldap.userdetails.LdapUserDetailsMapper#mapUserFromContext(org.springframework.ldap.
     * core.DirContextOperations, java.lang.String, java.util.Collection)
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities)
    {
        // TODO Auto-generated method stub
        username = String.format("%s@%s", username, acmLdapSyncConfig.getUserDomain());
        return super.mapUserFromContext(ctx, username, authorities);
    }

}
