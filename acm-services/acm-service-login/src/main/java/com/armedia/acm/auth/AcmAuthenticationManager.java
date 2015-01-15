package com.armedia.acm.auth;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Cycle through the configured authentication provider.  If one of them works,
 * map the provider's groups to ACM groups.
 */
public class AcmAuthenticationManager implements AuthenticationManager
{
    private SpringContextHolder springContextHolder;
    private AcmGrantedAuthoritiesMapper authoritiesMapper;
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;
    private UserDao userDao;
    private AcmGroupDao groupDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        Map<String, AuthenticationProvider> providerMap =
                getSpringContextHolder().getAllBeansOfType(AuthenticationProvider.class);
        Authentication providerAuthentication = null;
        AuthenticationException lastException = null;
        for ( Map.Entry<String, AuthenticationProvider> providerEntry : providerMap.entrySet() )
        {
            try
            {
                providerAuthentication = providerEntry.getValue().authenticate(authentication);
                if ( providerAuthentication != null )
                {
                    break;
                }
            }
            catch (AuthenticationException ae)
            {
                lastException = ae;
            }
        }

        if ( providerAuthentication != null )
        {
            // Spring Security publishes an authentication success event all by itself, so we do not have to raise
            // one here.
            AcmAuthentication acmAuth = getAcmAuthentication(providerAuthentication);
            return acmAuth;
        }
        if ( lastException != null )
        {
            getAuthenticationEventPublisher().publishAuthenticationFailure(lastException, authentication);
            throw lastException;
        }

        // didn't get an exception, or an authentication either, so we can throw a provider not found exception, since
        // either there are no providers, or no providers can handle the incoming authentication

        ProviderNotFoundException providerNotFoundException = new ProviderNotFoundException(
                "No providers to handle authentication of type: " + authentication.getClass().getName());
        getAuthenticationEventPublisher().publishAuthenticationFailure(providerNotFoundException, authentication);
        throw providerNotFoundException;
    }

    private AcmAuthentication getAcmAuthentication(Authentication providerAuthentication) {
        Collection<AcmGrantedAuthority> acmAuths =
                getAuthoritiesMapper().mapAuthorities(providerAuthentication.getAuthorities());
        
        // Collection with LDAP and ADHOC authority groups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsGroups = getAuthorityGroups(providerAuthentication.getName());
        
        // Collection with application roles for LDAP and ADHOC groups/subgroups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsRoles = getAuthoritiesMapper().mapAuthorities(acmAuthsGroups);
        
        // Add to all
        acmAuths.addAll(acmAuthsGroups);
        acmAuths.addAll(acmAuthsRoles);
        
        return new AcmAuthentication(
                acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.getPrincipal(), providerAuthentication.isAuthenticated(),
                providerAuthentication.getName());
    }
    
    private Collection<AcmGrantedAuthority> getAuthorityGroups(String userId)
    {
    	// Result
    	Set<AcmGrantedAuthority> authGroups = new HashSet<>();
    	
    	AcmUser user = getUserDao().findByUserId(userId);
    	
    	// All LDAP and ADHOC groups that the user belongs to (all these we are keeping in the database)
    	List<AcmGroup> groups = getGroupDao().findByUserMember(user);
    	
    	if (groups != null && groups.size() > 0)
    	{
    		for (AcmGroup group : groups)
    		{
    			// For each group create authority starting from the group to top parent group (recursion)
    			authGroups = getAuthGroupsToTop(group, authGroups);
    		}
    	}
    	
    	return authGroups;
    }
    
    private Set<AcmGrantedAuthority> getAuthGroupsToTop(AcmGroup group, Set<AcmGrantedAuthority> authorities)
    {
    	if (group != null)
    	{
    		// Create authority for the group name
			AcmGrantedAuthority authority = new AcmGrantedAuthority(group.getName());
			authorities.add(authority);
    		
    		// If the group has parent, continue with recursion, if not, return filled map
    		if (group.getParentGroup() != null)
    		{
		    	AcmGroup parent = getGroupDao().findByName(group.getParentGroup().getName());

		    	return getAuthGroupsToTop(parent, authorities);
    		}
    	}
    	
    	return authorities;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public AcmGrantedAuthoritiesMapper getAuthoritiesMapper() {
        return authoritiesMapper;
    }

    public void setAuthoritiesMapper(AcmGrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public void setAuthenticationEventPublisher(DefaultAuthenticationEventPublisher authenticationEventPublisher) {
        this.authenticationEventPublisher = authenticationEventPublisher;
    }

    public DefaultAuthenticationEventPublisher getAuthenticationEventPublisher() {
        return authenticationEventPublisher;
    }

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
}
