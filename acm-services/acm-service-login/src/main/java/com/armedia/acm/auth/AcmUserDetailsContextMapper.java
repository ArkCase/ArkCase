package com.armedia.acm.auth;

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 * @author ncuculova
 *
 */
public class AcmUserDetailsContextMapper extends LdapUserDetailsMapper
{
    private AcmLdapSyncConfig acmLdapSyncConfig;
    private static final Logger log = LoggerFactory.getLogger(AcmUserContextMapper.class);

    /**
     * @param acmLdapSyncConfig LDAP directory properties configuration
     */
    public AcmUserDetailsContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
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
        String usernameWithDomain = String.format("%s@%s", username, acmLdapSyncConfig.getUserDomain());
        log.info("Map authenticated user [{}] from LDAP context", usernameWithDomain);
        return super.mapUserFromContext(ctx, usernameWithDomain, authorities);
    }

}
