package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.ApplicationRolesToGroupsConfig;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmRoleToGroupMappingTest extends EasyMockSupport
{
    private AcmRoleToGroupMapping unit;
    private Map<String, List<String>> roleToGroupMap;
    private ApplicationRolesToGroupsConfig mockConfig;

    @Before
    public void setup()
    {

        roleToGroupMap = new HashMap<>();
        roleToGroupMap.put("ROLE_ADMINISTRATOR", Arrays.asList("ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR"));
        roleToGroupMap.put("ROLE_INVESTIGATOR_SUPERVISOR", Arrays.asList("ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR"));
        unit = new AcmRoleToGroupMapping();
        mockConfig = createMock(ApplicationRolesToGroupsConfig.class);
        unit.setRolesToGroupsConfig(mockConfig);
    }

    @Test
    public void testRoleToGroupsMap()
    {
        expect(mockConfig.getRolesToGroups()).andReturn(roleToGroupMap).anyTimes();

        replayAll();

        Map<String, Set<String>> roleToGroups = unit.getRoleToGroupsMap();

        verifyAll();
        assertThat("Key set should be the same", roleToGroups.keySet(), everyItem(isIn(roleToGroupMap.keySet())));

        Set<String> expectedGroups = roleToGroupMap.entrySet()
                .stream()
                .flatMap(entry -> {
                    String[] parts = entry.getValue().toArray(new String[0]);
                    return Arrays.stream(parts);
                })
                .collect(Collectors.toSet());

        Set<String> actualGroups = roleToGroups.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        assertThat("Value set should match", actualGroups, everyItem(isIn(expectedGroups)));

        roleToGroupMap.forEach((role, groups) -> roleToGroups.get(role)
                .forEach(group -> assertThat("String with group list should contain mapped groups",
                        groups, contains(group))));
    }

    @Test
    public void getGroupToRolesMapTest()
    {
        Map<String, List<String>> roleToGroupArray = new HashMap<>();
        roleToGroupArray.put("ROLE_ADMINISTRATOR", Arrays.asList("ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR"));
        roleToGroupArray.put("ROLE_INVESTIGATOR_SUPERVISOR", Arrays.asList("ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR"));

        expect(mockConfig.getRolesToGroups()).andReturn(roleToGroupMap).anyTimes();

        replayAll();

        Map<String, List<String>> groupToRoles = unit.getGroupToRolesMap();

        verifyAll();

        Set<String> expectedValues = groupToRoles.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<String> actualValues = roleToGroupArray.values()
                .stream()
                .flatMap(it -> {
                    String[] parts = it.toArray(new String[0]);
                    return Arrays.stream(parts);
                })
                .collect(Collectors.toSet());

        assertThat("Key set from actual should be expected's values", roleToGroupArray.keySet(),
                everyItem(isIn(expectedValues)));
        assertThat("Values from actual should be expected's key set", actualValues,
                everyItem(isIn(groupToRoles.keySet())));

        groupToRoles.forEach((key, value) -> {
            value.forEach(role -> {
                List<String> groups = roleToGroupArray.get(role);
                assertThat("Comma separated groups string should contain key", groups, contains(key));
            });
        });
    }

    /**
     * Test if the active mappings have one key for each
     * value from the configuration, with the value for that key being the list
     * of keys that have that value. The configuration map logical role names
     * to LDAP groups (to ensure each role name is accounted for). But we
     * need to map group names to roles. So we want to reverse the configuration
     * - make one key for each property value. But a group might map to more
     * than one role. Hence the need to have a list of roles for each group.
     *
     * Also the group and role names should be normalized to upper case.
     *
     * Also group and role names should be trimmed.
     *
     * Also null or empty group or role names - that property should be ignored.
     *
     * Also the roles should have "ROLE_" in front.
     *
     * E.g., if the properties have:
     * - ROLE1=group1
     * - Role2= group2
     * - ROLE_ROLE3 =group1
     * Then the active mapping should be:
     * - GROUP1=ROLE_ROLE1,ROLE_ROLE3
     * - GROUP2=ROLE_ROLE2
     */
    @Test
    public void initializeRolesToGroupsMapping()
    {
        Map<String, List<String>> roleToGroupsMapping = new HashMap<>();
        roleToGroupsMapping.put("ROLE1", Arrays.asList("group1"));
        roleToGroupsMapping.put("Role2", Arrays.asList("group2"));
        roleToGroupsMapping.put("ROLE_ROLE3", Arrays.asList(("group1")));
        roleToGroupsMapping.put("", Arrays.asList(("group5")));

        expect(mockConfig.getRolesToGroups()).andReturn(roleToGroupsMapping).anyTimes();
        replayAll();

        verifyAll();
        assertEquals(2, unit.getGroupToRolesMap().size());
        assertTrue(unit.getGroupToRolesMap().containsKey("GROUP1"));
        assertTrue(unit.getGroupToRolesMap().containsKey("GROUP2"));

        List<String> foundGroup1Roles = unit.getGroupToRolesMap().get("GROUP1");
        assertEquals(2, foundGroup1Roles.size());
        assertTrue(foundGroup1Roles.contains("ROLE_ROLE1"));
        assertTrue(foundGroup1Roles.contains("ROLE_ROLE3"));

        List<String> foundGroup2Roles = unit.getGroupToRolesMap().get("GROUP2");
        assertEquals(1, foundGroup2Roles.size());
        assertTrue(foundGroup2Roles.contains("ROLE_ROLE2"));
    }

}
