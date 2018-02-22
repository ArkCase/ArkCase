package com.armedia.acm.auth.okta.auth;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.Collection;

public class AcmMultiFactorAuthenticationManager extends AcmAuthenticationManager
{
    private Logger LOGGER = LoggerFactory.getLogger(AcmMultiFactorAuthenticationManager.class);

    @Override
    protected AcmAuthentication getAcmAuthentication(Authentication providerAuthentication)
    {

        AcmUser user = getUserDao().findByUserIdAnyCase(providerAuthentication.getName());

        Collection<AcmGrantedAuthority> acmAuths = getAuthoritiesMapper().mapAuthorities(providerAuthentication.getAuthorities());

        // Collection with LDAP and ADHOC authority groups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsGroups = getAuthorityGroups(user);

        // Collection with application roles for LDAP and ADHOC groups/subgroups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsRoles = getAuthoritiesMapper().mapAuthorities(acmAuthsGroups);

        // Add to all
        acmAuths.addAll(acmAuthsGroups);
        acmAuths.addAll(acmAuthsRoles);

        LOGGER.debug("Granting [{}] role 'ROLE_PRE_AUTHENTICATED'", providerAuthentication.getName());
        acmAuths.add(new AcmGrantedAuthority("ROLE_PRE_AUTHENTICATED"));

        return new AcmAuthentication(acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.isAuthenticated(), user.getUserId());
    }
}
