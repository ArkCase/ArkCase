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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmRoleToGroupMappingTest
{
    private AcmRoleToGroupMapping unit;
    private Map<String, String> roleToGroupMap;

    @Before
    public void setup()
    {

        roleToGroupMap = new HashMap<>();
        roleToGroupMap.put("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR");
        roleToGroupMap.put("ROLE_INVESTIGATOR_SUPERVISOR", "ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR");
        unit = new AcmRoleToGroupMapping();
        unit.setRoleToGroupMap(roleToGroupMap);
    }

    @Test
    public void testRoleToGroupsMap()
    {
        Map<String, Set<String>> roleToGroups = unit.getRoleToGroupsMap();

        assertThat("Key set should be the same", roleToGroups.keySet(), everyItem(isIn(roleToGroupMap.keySet())));

        Set<String> expectedGroups = roleToGroupMap.entrySet()
                .stream()
                .flatMap(entry -> {
                    String[] parts = entry.getValue().split(",");
                    return Arrays.stream(parts);
                })
                .collect(Collectors.toSet());

        Set<String> actualGroups = roleToGroups.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        assertThat("Value set should match", actualGroups, everyItem(isIn(expectedGroups)));

        roleToGroupMap.forEach((role, groupsString) -> roleToGroups.get(role)
                .forEach(group -> assertThat("String with group list should contain mapped groups",
                        groupsString, containsString(group))));
    }

    @Test
    public void getGroupToRolesMapTest()
    {
        Map<String, String> roleToGroupArray = new HashMap<>();
        roleToGroupArray.put("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR");
        roleToGroupArray.put("ROLE_INVESTIGATOR_SUPERVISOR", "ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR");

        Map<String, List<String>> groupToRoles = unit.getGroupToRolesMap();

        Set<String> expectedValues = groupToRoles.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<String> actualValues = roleToGroupArray.values()
                .stream()
                .flatMap(it -> {
                    String[] parts = it.split(",");
                    return Arrays.stream(parts);
                })
                .collect(Collectors.toSet());

        assertThat("Key set from actual should be expected's values", roleToGroupArray.keySet(),
                everyItem(isIn(expectedValues)));
        assertThat("Values from actual should be expected's key set", actualValues,
                everyItem(isIn(groupToRoles.keySet())));

        groupToRoles.forEach((key, value) -> {
            value.forEach(role -> {
                String groups = roleToGroupArray.get(role);
                assertThat("Comma separated groups string should contain key", groups, containsString(key));
            });
        });
    }
}
