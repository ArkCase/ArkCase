package com.armedia.acm.services.users.service.ldap;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.armedia.acm.spring.SpringContextHolder;

/**
 * Ldap Authenticate Manager cycles through all available LdapAuthenticateService beans to
 * authenticate a userName and password provided.
 */
public class LdapAuthenticateManager {
	private SpringContextHolder springContextHolder;
	
	public Boolean authenticate(String userName, String password)
	{
        Map<String, LdapAuthenticateService> ldapAuthServiceMap =
                getSpringContextHolder().getAllBeansOfType(LdapAuthenticateService.class);

        Boolean authenticated = Boolean.FALSE;
        for ( Map.Entry<String, LdapAuthenticateService> ldapAuthService : ldapAuthServiceMap.entrySet() )
        {
            authenticated = ldapAuthService.getValue().authenticate(userName, password);
            if ( authenticated )
            {
                break;
            }
        }
        return authenticated;
	}
	
    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
    
}
