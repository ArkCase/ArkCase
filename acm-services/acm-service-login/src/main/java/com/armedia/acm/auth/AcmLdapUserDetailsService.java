package com.armedia.acm.auth;

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

/**
 * @author ncuculova
 *
 */
public class AcmLdapUserDetailsService extends LdapUserDetailsService
{
    private AcmLdapSyncConfig acmLdapSyncConfig;
    private static final Logger log = LoggerFactory.getLogger(AcmLdapUserDetailsService.class);

    /**
     * @param userSearch {@link LdapUserSearch}
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch)
    {
        super(userSearch);
    }

    /**
     * @param userSearch {@link LdapUserSearch}
     * @param authoritiesPopulator {@link LdapAuthoritiesPopulator}
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator)
    {
        super(userSearch, authoritiesPopulator);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.ldap.userdetails.LdapUserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        String truncatedUsername = StringUtils.substringBeforeLast(username, "@");
        log.info("Authenticate user [{}] to LDAP without user domain [{}]", username, truncatedUsername);
        AcmUserDetailsContextMapper userDetailsContextMapper = new AcmUserDetailsContextMapper(acmLdapSyncConfig);
        setUserDetailsMapper(userDetailsContextMapper);
        return super.loadUserByUsername(truncatedUsername);
    }

    public AcmLdapSyncConfig getAcmLdapSyncConfig()
    {
        return acmLdapSyncConfig;
    }

    public void setAcmLdapSyncConfig(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

}
