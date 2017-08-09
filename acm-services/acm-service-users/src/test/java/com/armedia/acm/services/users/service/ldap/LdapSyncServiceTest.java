package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.LdapUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class LdapSyncServiceTest extends EasyMockSupport
{
    static final Logger log = LoggerFactory.getLogger(LdapSyncServiceTest.class);

    private LdapSyncService unit;

    private SpringLdapDao mockLdapDao;

    static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

    @Before
    public void setUp()
    {
        mockLdapDao = createMock(SpringLdapDao.class);

        unit = new LdapSyncService();
        unit.setLdapDao(mockLdapDao);
    }

    @Test
    public void testRoleToGroupsMap()
    {
        Map<String, String> roleToGroupMap = new HashMap<>();
        roleToGroupMap.put("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR");
        roleToGroupMap.put("ROLE_INVESTIGATOR_SUPERVISOR", "ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR");

        Map<String, Set<String>> roleToGroups = unit.roleToGroups(roleToGroupMap);

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

        roleToGroupMap.forEach((role, groupsString) ->
                roleToGroups.get(role).forEach(group ->
                        assertThat("String with group list should contain mapped groups",
                                groupsString, containsString(group))
                )
        );
    }

    @Test
    public void reverseRoleToGroupsMap()
    {
        Map<String, String> roleToGroupArray = new HashMap<>();
        roleToGroupArray.put("ROLE_ADMINISTRATOR", "ACM_ADMINISTRATOR_DEV,ARKCASE_ADMINISTRATOR");
        roleToGroupArray.put("ROLE_INVESTIGATOR_SUPERVISOR", "ACM_SUPERVISOR_DEV,ACM_INVESTIGATOR_VA,"
                + "ACM_INVESTIGATOR_DEV,ACM_ANALYST_DEV,ACM_CALLCENTER_DEV,ACM_ADMINISTRATOR_DEV,ACM_INVESTIGATOR_MK,"
                + "ARKCASE_ADMINISTRATOR");

        Map<String, List<String>> groupToRoles = unit.reverseRoleToGroupMap(roleToGroupArray);

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

    @Test
    public void testPopulateChildParentPair()
    {
        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setName("GROUP-1");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setName("GROUP-2");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setName("GROUP-3");

        //set parents
        Set<String> parentsGroup1 = new HashSet<>();
        parentsGroup1.add(ldapGroup2.getName());
        ldapGroup1.setParentGroups(parentsGroup1);
        Set<String> parentsGroup3 = new HashSet<>();
        parentsGroup3.add(ldapGroup2.getName());
        ldapGroup3.setParentGroups(parentsGroup3);
        // Group 2 has no parents
        ldapGroup2.setParentGroups(new HashSet<>());

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(ldapGroup1);
        ldapGroups.add(ldapGroup2);
        ldapGroups.add(ldapGroup3);

        Map<String, String> expected = new HashMap<>();
        expected.put(ldapGroup1.getName(), ldapGroup2.getName());
        expected.put(ldapGroup3.getName(), ldapGroup2.getName());

        Map<String, String> childParentPair = unit.populateGroupParentPairs(ldapGroups);

        log.debug("Expected: {}", expected);
        log.debug("Actual: {}", childParentPair);
        assertThat("Map should be equal", childParentPair.entrySet(), everyItem(isIn(expected.entrySet())));
    }

    @Test
    public void testSetParentGroupsToGroups()
    {
        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setName("GROUP-1");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setName("GROUP-2");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setName("GROUP-3");

        //set parents existing groups
        Set<String> parentsGroup1 = new HashSet<>();
        parentsGroup1.add(ldapGroup2.getName());
        Set<String> parentsGroup3 = new HashSet<>();
        parentsGroup3.add(ldapGroup2.getName());

        String[] expectedParentsGroup1 = parentsGroup1.toArray(new String[parentsGroup1.size()]);
        String[] expectedParentsGroup2 = new String[0];
        String[] expectedParentsGroup3 = parentsGroup3.toArray(new String[parentsGroup3.size()]);

        // add some non-existing LDAP groups
        parentsGroup1.add("GROUP-X");
        ldapGroup1.setParentGroups(parentsGroup1);
        parentsGroup3.add("GROUP-W");
        parentsGroup3.add("GROUP-Z");
        ldapGroup3.setParentGroups(parentsGroup3);
        ldapGroup2.setParentGroups(new HashSet<>());

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(ldapGroup1);
        ldapGroups.add(ldapGroup2);
        ldapGroups.add(ldapGroup3);

        unit.filterParentGroups(ldapGroups);

        assertThat("Arrays should be equal", ldapGroups.get(0).getParentGroups(), containsInAnyOrder(expectedParentsGroup1));
        assertThat("Arrays should be equal", ldapGroups.get(1).getParentGroups(), containsInAnyOrder(expectedParentsGroup2));
        assertThat("Arrays should be equal", ldapGroups.get(2).getParentGroups(), containsInAnyOrder(expectedParentsGroup3));
    }

    @Test
    public void testGetUsersByLdapGroup()
    {
        List<LdapGroup> ldapGroups = new ArrayList<>();
        List<LdapUser> ldapUsers = new ArrayList<>();
        Map<String, Set<LdapUser>> expected = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        Map<String, Set<LdapUser>> actual = unit.getUsersByLdapGroup(ldapGroups, ldapUsers);

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    @Test
    public void testGetUsersByApplicationRole()
    {
        List<LdapGroup> ldapGroups = new ArrayList<>();
        List<LdapUser> ldapUsers = new ArrayList<>();
        Map<String, Set<LdapUser>> ldapGroupUsers = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        AcmLdapSyncConfig mockLdapSyncConfig = createMock(AcmLdapSyncConfig.class);
        unit.setLdapSyncConfig(mockLdapSyncConfig);

        Map<String, String> roleToGroupMap = new HashMap<>();
        List<String> groups = new ArrayList<>(ldapGroupUsers.keySet());
        Map<String, Set<LdapUser>> expected = new HashMap<>();
        for (int i = 0; i < 3; ++i)
        {
            Collections.shuffle(groups);
            List<String> someGroups = groups.subList(0, groups.size() - 1);
            String groupsString = String.join(",", someGroups);
            String role = String.format("ROLE-%d", i + 1);
            roleToGroupMap.put(role, groupsString);
            Set<LdapUser> usersSet = new HashSet<>();
            for (String group : someGroups)
            {
                usersSet.addAll(ldapGroupUsers.get(group));
            }
            expected.put(role, usersSet);
        }

        expect(mockLdapSyncConfig.getRoleToGroupMap()).andReturn(roleToGroupMap);

        replayAll();

        Map<String, Set<LdapUser>> actual = unit.getUsersByApplicationRole(ldapGroupUsers);

        verifyAll();

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    private Map<String, Set<LdapUser>> setupTestLdapUsersGroups(List<LdapGroup> ldapGroups, List<LdapUser> ldapUsers)
    {
        Map<String, Set<LdapUser>> mockResult = new TreeMap<>();

        for (int i = 0; i < 3; ++i)
        {
            String group = String.format("GROUP-%d", i + 1);
            mockResult.put(group, new HashSet<>());
            LdapGroup ldapGroup = new LdapGroup();
            ldapGroup.setName(group);
            ldapGroup.setParentGroups(new HashSet<>());
            ldapGroups.add(ldapGroup);
        }
        LdapGroup parent = new LdapGroup();
        parent.setName("PARENT-GROUP");
        parent.setParentGroups(new HashSet<>());
        ldapGroups.add(parent);
        mockResult.put(parent.getName(), new HashSet<>());

        // set parent to groups 1 and 2
        ldapGroups.get(0).setParentGroups(new HashSet<>(Arrays.asList(parent.getName())));
        ldapGroups.get(1).setParentGroups(new HashSet<>(Arrays.asList(parent.getName())));

        for (int i = 0; i < 5; ++i)
        {
            LdapUser user = new LdapUser();
            user.setDistinguishedName(String.format("USER-%d", i + 1));
            user.setUserId(String.valueOf(i + 1));
            ldapUsers.add(user);
        }

        for (int i = 0; i < ldapGroups.size(); ++i)
        {
            LdapGroup group = ldapGroups.get(i);
            String groupName = group.getName();
            if (i < ldapGroups.size() - 1)
            {
                ldapUsers.get(i).getLdapGroups().add(groupName);
                mockResult.get(groupName).add(ldapUsers.get(i));
                ldapUsers.get(i + 1).getLdapGroups().add(groupName);
                mockResult.get(groupName).add(ldapUsers.get(i + 1));
            } else
            {
                // last group with all users
                for (LdapUser user : ldapUsers)
                {
                    if (!group.getName().equals("PARENT-GROUP"))
                    {
                        user.getLdapGroups().add(groupName);
                        mockResult.get(groupName).add(user);
                    }
                }
            }
        }

        // add parent group in result
        Set<LdapUser> allUsers = new HashSet<>();
        Set<LdapUser> firstGroupUsers = mockResult.get(ldapGroups.get(0).getName());
        Set<LdapUser> secondGroupUsers = mockResult.get(ldapGroups.get(1).getName());
        allUsers.addAll(firstGroupUsers);
        allUsers.addAll(secondGroupUsers);
        mockResult.put(parent.getName(), allUsers);
        return mockResult;
    }

    private void printMap(Map<String, Set<LdapUser>> map)
    {
        System.out.println("Printing result map");
        for (String group : map.keySet())
        {
            System.out.println(group);
            for (LdapUser user : map.get(group))
            {
                System.out.println(String.format("-> member is: %s", user.getDistinguishedName()));
            }
        }

    }

    @Test
    public void testSetGroupsToUsers()
    {
        Set<String> potentialUserGroups = fromArray("GROUP-A", "GROUP-B", "GROUP-C");

        LdapUser user = new LdapUser();
        user.setDistinguishedName("USER-1");
        user.setLdapGroups(potentialUserGroups);
        List<LdapUser> ldapUsers = new ArrayList<>();
        ldapUsers.add(user);

        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setName("GROUP-A");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setName("GROUP-B");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setName("GROUP-X");

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(ldapGroup1);
        ldapGroups.add(ldapGroup2);
        ldapGroups.add(ldapGroup3);

        // add potentialUserGroups which are existing LDAP groups
        List<String> expectedUserGroups = Arrays.asList("GROUP-A", "GROUP-B");

        unit.filterUserGroups(ldapUsers, ldapGroups);

        assertThat("Arrays should be equal", ldapUsers.get(0).getLdapGroups(), containsInAnyOrder(expectedUserGroups.toArray()));
    }

    @Test
    public void testFilterUsersForKnownGroups()
    {
        Set<String> userGroups1 = fromArray("GROUP-A", "GROUP-B", "GROUP-C");
        Set<String> userGroups2 = fromArray("GROUP-X", "GROUP-Y", "GROUP-Z");
        List<LdapUser> ldapUsers = new ArrayList<>();
        List<LdapUser> expected = new ArrayList<>();
        for (int i = 0; i < 5000; ++i)
        {
            LdapUser user = new LdapUser();
            user.setDistinguishedName(String.format("USER-%d", (i + 1)));
            if (i % 5 == 0)
            {
                user.setLdapGroups(userGroups1);
                expected.add(user);
            } else
            {
                user.setLdapGroups(userGroups2);
            }
            ldapUsers.add(user);
        }
        List<LdapGroup> ldapGroups = new ArrayList<>();
        for (int i = 0; i < 1000; ++i)
        {
            LdapGroup ldapGroup = new LdapGroup();
            ldapGroup.setName(String.format("GROUP-%d", (i + 1)));
            ldapGroups.add(ldapGroup);
        }
        LdapGroup found = new LdapGroup();
        found.setName("GROUP-A");
        ldapGroups.add(found);
        long start = System.currentTimeMillis();
        List<LdapUser> result = unit.filterUsersForKnownGroups(ldapUsers, ldapGroups);
        log.debug("Took: {}ms", System.currentTimeMillis() - start);
        assertThat("Arrays should be equal", result, containsInAnyOrder(expected.toArray()));
    }
}
