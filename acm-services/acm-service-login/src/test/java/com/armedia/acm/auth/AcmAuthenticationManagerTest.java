package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    private AcmAuthenticationMapper mockAuthenticationMapper;
    private DefaultAuthenticationEventPublisher mockEventPublisher;
    private UserDao mockUserDao;

    @Before
    public void setUp() throws Exception
    {
        mockContextHolder = createMock(SpringContextHolder.class);
        mockAuthentication = createMock(Authentication.class);
        mockFirstProvider = createMock(AuthenticationProvider.class);
        mockSecondProvider = createMock(AuthenticationProvider.class);
        mockEventPublisher = createMock(DefaultAuthenticationEventPublisher.class);
        mockUserDao = createMock(UserDao.class);
        mockAuthenticationMapper = createMock(AcmAuthenticationMapper.class);

        unit = new AcmAuthenticationManager();

        unit.setSpringContextHolder(mockContextHolder);
        unit.setAuthenticationEventPublisher(mockEventPublisher);
        unit.setUserDao(mockUserDao);
        unit.setAuthenticationMapper(mockAuthenticationMapper);
    }

    @Test
    public void authenticate_firstOneWorks()
    {
        AcmUser user = new AcmUser();
        user.setUserId("test-user");

        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        Set<AcmGrantedAuthority> authsFromProvider = new HashSet<>(Arrays.asList(
                new AcmGrantedGroupAuthority("LDAP_INVESTIGATOR", 1L)));

        Set<AcmGrantedAuthority> authsFromMapper = new HashSet<>(Arrays.asList(
                new AcmGrantedAuthority("INVESTIGATOR")));

        AcmAuthentication successAuthentication = new AcmAuthentication(authsFromProvider, null, null,
                true, user.getUserId(), user.getIdentifier());

        AcmAuthentication authenticationFromMapper = new AcmAuthentication(authsFromMapper, null, null,
                true, user.getUserId(), user.getIdentifier());

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockAuthentication.getName()).andReturn(user.getUserId());
        expect(mockFirstProvider.authenticate(mockAuthentication)).andReturn(successAuthentication);
        expect(mockAuthenticationMapper.getAcmAuthentication(successAuthentication)).andReturn(authenticationFromMapper);

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
        expect(mockAuthentication.getName()).andReturn(null);
        Capture<AuthenticationException> captureCustomException = Capture.newInstance();
        mockEventPublisher.publishAuthenticationFailure(capture(captureCustomException), eq(mockAuthentication));
        expectLastCall().once();

        try
        {
            replayAll();
            unit.authenticate(mockAuthentication);
            verifyAll();
            fail("should have gotten an exception");

        }
        catch (AuthenticationException ae)
        {
            assertEquals(captureCustomException.getValue(), ae);

        }

    }

    @Test(expected = AuthenticationServiceException.class)
    public void authenticate_shouldThrowBadCredentials()
    {
        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        BadCredentialsException badCredentialsException = new BadCredentialsException("Bad credentials");

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockFirstProvider.authenticate(mockAuthentication)).andThrow(badCredentialsException);
        expect(mockSecondProvider.authenticate(mockAuthentication)).andReturn(null);
        expect(mockAuthentication.getName()).andReturn("ann-acm").times(2);
        expect(mockUserDao.isUserPasswordExpired("ann-acm")).andReturn(false);

        Capture<AuthenticationServiceException> authenticationServiceExceptionCapture = Capture.newInstance();
        mockEventPublisher.publishAuthenticationFailure(capture(authenticationServiceExceptionCapture), eq(mockAuthentication));
        expectLastCall().once();

        replayAll();

        unit.authenticate(mockAuthentication);

        verifyAll();

        AuthenticationServiceException actualException = authenticationServiceExceptionCapture.getValue();
        AuthenticationServiceException expected = new AuthenticationServiceException(
                ExceptionUtils.getRootCauseMessage(badCredentialsException), badCredentialsException);
        assertNotNull(actualException);
        assertEquals(expected, actualException);
    }

    @Test(expected = AuthenticationServiceException.class)
    public void authenticate_emptyUsername_shouldThrowBadCredentials()
    {
        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        BadCredentialsException badCredentialsException = new BadCredentialsException("Bad credentials");

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockFirstProvider.authenticate(mockAuthentication)).andThrow(badCredentialsException);
        expect(mockSecondProvider.authenticate(mockAuthentication)).andReturn(null);
        expect(mockAuthentication.getName()).andReturn("").times(2);
        expect(mockUserDao.isUserPasswordExpired("")).andReturn(false);

        Capture<AuthenticationServiceException> authenticationServiceExceptionCapture = Capture.newInstance();
        mockEventPublisher.publishAuthenticationFailure(capture(authenticationServiceExceptionCapture), eq(mockAuthentication));
        expectLastCall().once();

        replayAll();

        unit.authenticate(mockAuthentication);

        verifyAll();

        AuthenticationServiceException actualException = authenticationServiceExceptionCapture.getValue();
        AuthenticationServiceException expected = new AuthenticationServiceException(
                ExceptionUtils.getRootCauseMessage(badCredentialsException), badCredentialsException);
        assertNotNull(actualException);
        assertEquals(expected, actualException);
    }

    @Test(expected = AuthenticationServiceException.class)
    public void authenticate_shouldThrowExceptionAndShowEmailSentMsg()
    {
        Map<String, AuthenticationProvider> providers = getAuthenticationProviderMap();

        BadCredentialsException badCredentialsException = new BadCredentialsException("Bad credentials");

        expect(mockContextHolder.getAllBeansOfType(AuthenticationProvider.class)).andReturn(providers);
        expect(mockFirstProvider.authenticate(mockAuthentication)).andThrow(badCredentialsException);
        expect(mockSecondProvider.authenticate(mockAuthentication)).andReturn(null);
        expect(mockAuthentication.getName()).andReturn("ann-acm").times(2);
        expect(mockUserDao.isUserPasswordExpired("ann-acm")).andReturn(true);

        Capture<AuthenticationServiceException> authenticationServiceExceptionCapture = Capture.newInstance();
        mockEventPublisher.publishAuthenticationFailure(capture(authenticationServiceExceptionCapture), eq(mockAuthentication));
        expectLastCall().once();

        replayAll();

        unit.authenticate(mockAuthentication);

        verifyAll();
        AuthenticationServiceException actualException = authenticationServiceExceptionCapture.getValue();
        AuthenticationServiceException expected = new AuthenticationServiceException(
                "Your password has expired! An email with reset password link was sent to you.", badCredentialsException);
        assertNotNull(actualException);
        assertEquals(expected, actualException);
    }

    private Map<String, AuthenticationProvider> getAuthenticationProviderMap()
    {
        Map<String, AuthenticationProvider> providers = new TreeMap<>();
        providers.put("A", mockFirstProvider);
        providers.put("B", mockSecondProvider);
        return providers;
    }

}
