package com.armedia.acm.services.dataaccess.service.impl;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AccessControlRuleCheckerImpl accessControlRuleChecker = new AccessControlRuleCheckerImpl();

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
        propertiesMapping = new HashMap<>();
        propertiesMapping.put("object_sub_type_s", "objectSubType");
        propertiesMapping.put("status_lcs", "status");

        DataAccessControlConfig config = new DataAccessControlConfig();
        config.setFallbackInsertObjectExpression("create");
        config.setFallbackDeleteObjectExpression("delete");
        config.setFallbackEditObjectExpression("save|insert|remove|add|edit|change|lock|complete|unlock|merge|restrict|declare|rename|write");
        config.setFallbackGetObjectExpression("get|list|read|download|view|subscribe");
        accessControlRuleChecker.setDacConfig(config);
    }

    @Test
    public void testMissingRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(null).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "completeTask", solrDocument);
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testEmptyRules()
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(new ArrayList<>()).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "completeTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "completeTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "COMPLAINT",
                "completeTask", solrDocument);
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testStatusAsArrayInJSONRule() throws IOException
    {
        JSONObject rule = new JSONObject();
        rule.put("actionName", "editAttachments");
        rule.put("objectType", "CASE_FILE");
        rule.put("objectProperties", new JSONObject("{status : [\"DRAFT\", \"ACTIVE\", \"Quality Control\"]}"));

        ObjectMapper mapper = new ObjectMapper();
        AccessControlRule accessControlRule = mapper.readValue(rule.toString(), AccessControlRule.class);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "editAttachments", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testMatchingParentFallbackPermission() throws IOException
    {
        JSONObject rule = new JSONObject();
        rule.put("actionName", "editObject");
        rule.put("objectType", "CASE_FILE");
        rule.put("objectProperties", new JSONObject("{status : [\"DRAFT\", \"ACTIVE\", \"Quality Control\"]}"));

        ObjectMapper mapper = new ObjectMapper();
        AccessControlRule accessControlRule = mapper.readValue(rule.toString(), AccessControlRule.class);

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);

        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping).anyTimes();
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "editAttachments", solrDocument);
        assertTrue(granted);

        granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "mergeCase", solrDocument);
        assertTrue(granted);

        granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "completeCase", solrDocument);
        assertTrue(granted);

        granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "saveCase", solrDocument);
        assertTrue(granted);

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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testMatchingRolesAllWithWildcardRole()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAll(Arrays.asList("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV@*"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        GrantedAuthority grantedAuthority4 = new SimpleGrantedAuthority("ACM_ADMINISTRATOR_DEV@armedia.com");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3, grantedAuthority4);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testNonMatchingRolesAllWithWildcardRole()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAll(Arrays.asList("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV@*"));

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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testCaseInsensitiveMatchingRolesAll()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAll(Arrays.asList("Role_Administrator", "Role_Analyst"));

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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testMatchingRolesAnyWithWildcardRole()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAny(Arrays.asList("ROLE_SUPERVISOR", "ACM_ADMINISTRATOR@*"));

        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ACM_ADMINISTRATOR@armedia.com");
        Collection grantedAuthorities = Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3);
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCaseInsensitiveMatchingRolesAny()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setActionName("createTask");
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setObjectSubType("ORDER");
        accessControlRule.setUserRolesAny(Arrays.asList("Role_Administrator", "Role_Supervisor"));

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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
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
        accessControlRule.setUserRolesAny(Arrays.asList("ROLE_INVESTIGATOR", "ROLE_SUPERVISOR", "ACM_ADMINISTRATOR@*"));

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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
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

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "createTask", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenPrincipalIsAssignee()
    {
        List<String> userIsParticipantTypeAny = Arrays.asList("assignee", "supervisor", "owning group");
        AccessControlRule accessControlRule = getAccessControlRuleForParticipantTypesTest();
        accessControlRule.setUserIsParticipantTypeAny(userIsParticipantTypeAny);
        accessControlRule.setObjectSubType("ORDER");

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "restrictCase", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenPrincipalIsReader()
    {
        List<String> userIsParticipantTypeAny = Arrays.asList("assignee", "supervisor", "owning group", "reader");
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setUserIsParticipantTypeAny(userIsParticipantTypeAny);
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setActionName("restrictCase");
        accessControlRule.setObjectSubType("ORDER");

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        JSONObject solrDocumentJson = new JSONObject(solrDocument);
        JSONObject solrResultJson = solrDocumentJson.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
        solrResultJson.put(ACM_PARTICIPANTS_LCS, "[{\"ldapId\":\"ACM_INVESTIGATOR_DEV\", \"type\":\"owning group\"}," +
                "{\"ldapId\":\"ian-acm\", \"type\":\"assignee\"}, {\"ldapId\":\"joy-acm\", \"type\":\"reader\"}," +
                "{\"ldapId\":\"hope-acm\", \"type\":\"reader\"}, {\"ldapId\":\"ann-acm\", \"type\":\"reader\"}]");

        boolean granted = accessControlRuleChecker.isAccessGranted(new AcmAuthentication(grantedAuthorities, null,
                null, true, "hope-acm"), 1L, "CASE_FILE",
                "restrictCase", solrDocumentJson.toString());
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenPrincipalIsInOneOfTheRequiredGroups()
    {
        List<String> userIsParticipantTypeAny = Arrays.asList("assignee", "supervisor", "owning group");
        AccessControlRule accessControlRule = getAccessControlRuleForParticipantTypesTest();
        accessControlRule.setUserIsParticipantTypeAny(userIsParticipantTypeAny);
        accessControlRule.setObjectSubType("ORDER");

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        JSONObject solrDocumentJson = new JSONObject(solrDocument);
        JSONObject solrResultJson = solrDocumentJson.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
        solrResultJson.put(ACM_PARTICIPANTS_LCS, "[{\"ldapId\":\"ACM_ADMINISTRATOR\", \"type\":\"owning group\"}," +
                "{\"ldapId\":\"ian-acm\", \"type\":\"assignee\"}]");
        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "restrictCase", solrDocumentJson.toString());
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenPrincipalIsNotInAnyOfRequiredTypes()
    {
        List<String> userIsParticipantTypeAny = Arrays.asList("assignee", "supervisor", "owning group");
        AccessControlRule accessControlRule = getAccessControlRuleForParticipantTypesTest();
        accessControlRule.setUserIsParticipantTypeAny(userIsParticipantTypeAny);

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        JSONObject solrDocumentJson = new JSONObject(solrDocument);
        JSONObject solrResultJson = solrDocumentJson.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
        solrResultJson.put(ACM_PARTICIPANTS_LCS, "[{\"ldapId\":\"ACM_INVESTIGATOR_DEV\", \"type\":\"owning group\"}," +
                "{\"ldapId\":\"ian-acm\", \"type\":\"assignee\"}]");
        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "restrictCase", solrDocumentJson.toString());
        assertFalse(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenUserIsParticipantTypeAnyListIsNull()
    {
        AccessControlRule accessControlRule = getAccessControlRuleForParticipantTypesTest();
        accessControlRule.setUserIsParticipantTypeAny(null);
        accessControlRule.setObjectSubType("ORDER");

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "restrictCase", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    @Test
    public void testCheckParticipantTypesWhenUserIsParticipantTypeAnyListIsEmpty()
    {
        AccessControlRule accessControlRule = getAccessControlRuleForParticipantTypesTest();
        accessControlRule.setUserIsParticipantTypeAny(new ArrayList<>());
        accessControlRule.setObjectSubType("ORDER");

        Collection grantedAuthorities = getGrantedAuthoritiesMockList();

        mockExpectsWhenParticipantTypesTest(accessControlRule, grantedAuthorities);

        boolean granted = accessControlRuleChecker.isAccessGranted(authenticationMock, 1L, "CASE_FILE",
                "restrictCase", solrDocument);
        assertTrue(granted);
        verifyAll();
    }

    public void mockExpectsWhenParticipantTypesTest(AccessControlRule accessControlRule, Collection grantedAuthorities)
    {
        // mock the behavior
        EasyMock.expect(accessControlRulesMock.getAccessControlRuleList()).andReturn(Arrays.asList(accessControlRule)).anyTimes();
        EasyMock.expect(accessControlRulesMock.getPropertiesMapping()).andReturn(propertiesMapping);
        EasyMock.expect(authenticationMock.getName()).andReturn("ann-acm").anyTimes();
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(grantedAuthorities).anyTimes();
        replayAll();
    }

    public Collection getGrantedAuthoritiesMockList()
    {
        GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_ANALYST");
        GrantedAuthority grantedAuthority3 = new SimpleGrantedAuthority("ROLE_TECHNICIAN");
        GrantedAuthority grantedAuthority4 = new SimpleGrantedAuthority("ACM_ADMINISTRATOR");
        return Arrays.asList(grantedAuthority1, grantedAuthority2, grantedAuthority3, grantedAuthority4);
    }

    public AccessControlRule getAccessControlRuleForParticipantTypesTest()
    {
        AccessControlRule accessControlRule = new AccessControlRule();
        accessControlRule.setObjectType("CASE_FILE");
        accessControlRule.setActionName("restrictCase");
        return accessControlRule;
    }
}
