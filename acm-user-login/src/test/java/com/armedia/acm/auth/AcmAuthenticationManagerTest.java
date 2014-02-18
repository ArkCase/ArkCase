package com.armedia.acm.auth;

import com.armedia.acm.spring.SpringContextHolder;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by dmiller on 2/7/14.
 */
public class AcmAuthenticationManagerTest extends EasyMockSupport
{
    private AcmAuthenticationManager unit;
    private SpringContextHolder mockContextHolder;
    private Authentication mockAuthentication;
    private AuthenticationProvider mockFirstProvider;
    private AuthenticationProvider mockSecondProvider;
    private AcmGrantedAuthoritiesMapper mockAuthoritiesMapper;
    private DefaultAuthenticationEventPublisher mockEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        mockContextHolder = createMock(SpringContextHolder.class);
        mockAuthentication = createMock(Authentication.class);
        mockFirstProvider = createMock(AuthenticationProvider.class);
        mockSecondProvider = createMock(AuthenticationProvider.class);
        mockAuthoritiesMapper = createMock(AcmGrantedAuthoritiesMapper.class);
        mockEventPublisher = createMock(DefaultAuthenticationEventPublisher.class);

        unit = new AcmAuthenticationManager();

        unit.setSpringContextHolder(mockContextHolder);
        unit.setAuthoritiesMapper(mockAuthoritiesMapper);
        unit.setAuthenticationEventPublisher(mockEventPublisher);
    }

    @Test
    public void authenticate_firstOneWorks()
    {
        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        List<AcmGrantedAuthority> authsFromProvider = Arrays.asList(
                new AcmGrantedAuthority("LDAP_INVESTIGATOR")
        );

        List<AcmGrantedAuthority> authsFromMapper = Arrays.asList(
                new AcmGrantedAuthority("INVESTIGATOR")
        );

        AcmAuthentication successAuthentication = new AcmAuthentication(
                authsFromProvider, null, null, null, true, null);

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockFirstProvider.authenticate(mockAuthentication)).andReturn(successAuthentication);
        expect(mockAuthoritiesMapper.mapAuthorities(authsFromProvider)).andReturn(authsFromMapper);

        replayAll();

        Authentication found = unit.authenticate(mockAuthentication);

        verifyAll();

        assertEquals(authsFromMapper, found.getAuthorities());


    }

    @Test
    public void authenticate_shouldThrowLastProviderException()
    {
        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        AuthenticationException firstException = new ProviderNotFoundException("test message");

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockFirstProvider.authenticate(mockAuthentication)).andThrow(firstException);
        expect(mockSecondProvider.authenticate(mockAuthentication)).andReturn(null);
        mockEventPublisher.publishAuthenticationFailure(firstException, mockAuthentication);

        replayAll();

        try
        {
            unit.authenticate(mockAuthentication);
            fail("should have gotten an exception");
        }
        catch (AuthenticationException ae)
        {
            assertEquals(firstException, ae);
        }

        verifyAll();
    }

    private Map<String, AuthenticationProvider> getAuthenticationProviderMap()
    {
        Map<String, AuthenticationProvider> providers = new TreeMap<String, AuthenticationProvider>();
        providers.put("A", mockFirstProvider);
        providers.put("B", mockSecondProvider);
        return providers;
    }
}
