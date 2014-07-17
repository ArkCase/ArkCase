package com.armedia.acm.auth;

import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;
import java.util.Map;

/**
 * Cycle through the configured authentication provider.  If one of them works,
 * map the provider's groups to ACM groups.
 */
public class AcmAuthenticationManager implements AuthenticationManager
{
    private SpringContextHolder springContextHolder;
    private AcmGrantedAuthoritiesMapper authoritiesMapper;
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;

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
        return new AcmAuthentication(
                acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.getPrincipal(), providerAuthentication.isAuthenticated(),
                providerAuthentication.getName());
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
}
