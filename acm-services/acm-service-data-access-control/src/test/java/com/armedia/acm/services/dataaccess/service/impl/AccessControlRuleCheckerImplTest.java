package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 06.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class AccessControlRuleCheckerImplTest extends EasyMockSupport
{
    /**
     * Access Control rule checker.
     */
    @TestSubject
    private AccessControlRuleChecker accessControlRuleChecker = new AccessControlRuleCheckerImpl();

    /**
     * Access Control rules mock.
     */
    @Mock
    private AccessControlRules accessControlRulesMock;

    /**
     * Authentication token mock.
     */
    @Mock
    private Authentication authenticationMock;

    /**
     * Solr data stored for the object.
     */
    private String solrDocument;

    /**
     * Solr to objectProperties name mapping.
     */
    private Map<String, String> propertiesMapping;

    @Before
    public void setUp() throws Exception
    {
        accessControlRuleChecker.setAccessControlRules(accessControlRulesMock);
        // load Solr document from disk
        URI uri = getClass().getClassLoader().getResource("solrDocument.json").toURI();
        byte[] encoded = Files.readAllBytes(Paths.get(uri));
        solrDocument = new String(encoded, StandardCharsets.UTF_8);
        // initialize property mappings
        propertiesMapping = new HashMap<String, String>();
        propertiesMapping.put("object_sub_type_s", "objectSubType");
    }

    @Test
    public void testMissingRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(null).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask", solrDocument);
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testEmptyRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(new ArrayList<AccessControlRule>()).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "completeTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "COMPLAINT", "completeTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
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
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE", "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }
}
