package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 06.11.2015.
 */
public class AccessControlRuleCheckerImplTest extends EasyMockSupport
{
    /**
     * Access Control rule checker.
     */
    private AccessControlRuleCheckerImpl accessControlRuleChecker;

    /**
     * Access Control rules mock.
     */
    private AccessControlRules accessControlRulesMock;

    /**
     * Authentication token mock.
     */
    private Authentication authenticationMock;

    @Before
    public void setUp()
    {
        accessControlRulesMock = createMock(AccessControlRules.class);
        authenticationMock = createMock(Authentication.class);
        accessControlRuleChecker = new AccessControlRuleCheckerImpl();
        accessControlRuleChecker.setAccessControlRules(accessControlRulesMock);
    }

    @Test
    public void testMissingRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(null).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testEmptyRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(new ArrayList<AccessControlRule>()).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testNonMatchingPermission()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");

        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testNonMatchingTargetType()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("completeTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");

        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "COMPLAINT", "completeTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testMatchingRolesAll()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAll(Arrays.asList("ROLE_ADMINISTRATOR"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testNonMatchingRolesAll()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAll(Arrays.asList("ROLE_ADMINISTRATOR", "ROLE_SUPERVISOR"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testEmptyRolesAll()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testMatchingRolesAny()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAny(Arrays.asList("ROLE_ADMINISTRATOR", "ROLE_SUPERVISOR"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testNonMatchingRolesAny()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAny(Arrays.asList("ROLE_INVESTIGATOR", "ROLE_SUPERVISOR"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testEmptyRolesAny()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask");
        assertTrue(granted);
        verifyAll();
    }
}
