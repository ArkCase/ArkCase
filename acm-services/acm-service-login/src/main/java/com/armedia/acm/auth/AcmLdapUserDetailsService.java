/**
 * 
 */
package com.armedia.acm.auth;

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

/**
 * @author nadica.cuculova
 *
 */
public class AcmLdapUserDetailsService extends LdapUserDetailsService
{
    private AcmLdapSyncConfig acmLdapSyncConfig;

    /**
     * @param userSearch
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch)
    {
        super(userSearch);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userSearch
     * @param authoritiesPopulator
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator)
    {
        super(userSearch, authoritiesPopulator);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.ldap.userdetails.LdapUserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        // TODO Auto-generated method stub
        username = StringUtils.substringBeforeLast(username, "@");
        AcmUserDetailsContextMapper userDetailsContextMapper = new AcmUserDetailsContextMapper(acmLdapSyncConfig);
        setUserDetailsMapper(userDetailsContextMapper);
        return super.loadUserByUsername(username);
    }

    /**
     * @return the acmLdapSyncConfig
     */
    public AcmLdapSyncConfig getAcmLdapSyncConfig()
    {
        return acmLdapSyncConfig;
    }

    /**
     * @param acmLdapSyncConfig
     *            the acmLdapSyncConfig to set
     */
    public void setAcmLdapSyncConfig(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

}
