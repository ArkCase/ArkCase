package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.*;

/**
 * Created by armdev on 7/3/14.
 */
public class LdapSyncServiceTest extends EasyMockSupport
{
    static final Logger log = LoggerFactory.getLogger(LdapSyncServiceTest.class);
    private LdapSyncService unit;

    private SpringLdapDao mockLdapDao;
    private LdapTemplate mockLdapTemplate;

    static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

    @Before
    public void setUp()
    {
        mockLdapDao = createMock(SpringLdapDao.class);
        mockLdapTemplate = createMock(LdapTemplate.class);

        unit = new LdapSyncService();
        unit.setLdapDao(mockLdapDao);
    }

    @Test
    public void reverseRoleToGroupMap_multipleGroupsPerRole()
    {
        Map<String, String> roleToGroups = new HashMap<>();
        roleToGroups.put("ROLE ONE", "GROUP A,GROUP B");
        roleToGroups.put("ROLE TWO", "GROUP B, GROUP C ");
        roleToGroups.put("ROLE THREE", "GROUP D");

        Map<String, List<String>> groupToRoles = unit.reverseRoleToGroupMap(roleToGroups);

        assertNotNull(groupToRoles.get("GROUP A"));
        assertNotNull(groupToRoles.get("GROUP B"));
        assertNotNull(groupToRoles.get("GROUP C"));
        assertNotNull(groupToRoles.get("GROUP D"));

        assertEquals(1, groupToRoles.get("GROUP A").size());
        assertEquals(2, groupToRoles.get("GROUP B").size());
        assertEquals(1, groupToRoles.get("GROUP C").size());
        assertEquals(1, groupToRoles.get("GROUP D").size());

        assertEquals("ROLE ONE", groupToRoles.get("GROUP A").get(0));
        assertEquals("ROLE TWO", groupToRoles.get("GROUP C").get(0));
        assertEquals("ROLE THREE", groupToRoles.get("GROUP D").get(0));

        assertTrue(groupToRoles.get("GROUP B").contains("ROLE ONE"));
        assertTrue(groupToRoles.get("GROUP B").contains("ROLE TWO"));

        assertFalse(groupToRoles.containsKey("GROUP A,GROUP B"));
        assertFalse(groupToRoles.containsKey("GROUP B, GROUP C "));

    }

    @Test
    public void queryLdapUsers_applicationRoles_differentCases()
    {
        Map<String, String> rolesToGroupMap = new HashMap<>();
        String groupOne = "GroupOne";
        String roleOne = "RoleOne";
        rolesToGroupMap.put(roleOne, groupOne);

        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setRoleToGroupMap(rolesToGroupMap);

        String directoryName = "directoryName";

        Set<String> roles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();
        Map<String, String> childParentPairs = new HashMap<>();

        String userDnOne = "dn1";
        String userDnTwo = "dn2";
        String[] memberDns = {userDnOne, userDnTwo};
        LdapGroup group = new LdapGroup();
        group.setGroupName(groupOne.toLowerCase());
        group.setMemberDistinguishedNames(memberDns);

        List<LdapGroup> groups = Arrays.asList(group);

        AcmLdapEntity userOne = new AcmUser();
        userOne.setDistinguishedName(userDnOne);

        AcmLdapEntity userTwo = new AcmUser();
        userTwo.setDistinguishedName(userDnTwo);

        List<AcmLdapEntity> entities = Arrays.asList(userOne, userTwo);

        unit.setLdapSyncConfig(new AcmLdapSyncConfig());

        expect(mockLdapDao.buildLdapTemplate(config)).andReturn(mockLdapTemplate);
        expect(mockLdapDao.findGroups(mockLdapTemplate, config)).andReturn(groups);
        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, group)).andReturn(entities);

        replayAll();

        unit.queryLdapUsers(config, directoryName, roles, users, usersByApplicationRole, usersByLdapGroup, childParentPairs);

        verifyAll();

        assertEquals(1, usersByApplicationRole.size());
        assertEquals(1, usersByLdapGroup.size());

    }

    @Test
    public void queryLdapUsers_nestedGroups()
    {
        Map<String, String> rolesToGroupMap = new HashMap<>();
        String groupName = "GROUP";
        rolesToGroupMap.put("ROLE", groupName);
        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setRoleToGroupMap(rolesToGroupMap);

        String directoryName = "directoryName";

        Set<String> roles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();
        Map<String, String> childParentPairs = new HashMap<>();

        String userDistinguishedName = "dn1";
        String groupDistinguishedName = "dn2";
        String[] memberDns = {userDistinguishedName, groupDistinguishedName};
        LdapGroup group = new LdapGroup();
        group.setGroupName(groupName);
        group.setMemberDistinguishedNames(memberDns);

        List<LdapGroup> groups = new ArrayList<>();
        groups.add(group);

        AcmLdapEntity user = new AcmUser();
        user.setDistinguishedName(userDistinguishedName);

        AcmLdapEntity role = new AcmRole();
        role.setDistinguishedName(groupDistinguishedName);

        List<AcmLdapEntity> entities = Arrays.asList(user, role);

        unit.setLdapSyncConfig(new AcmLdapSyncConfig());

        expect(mockLdapDao.buildLdapTemplate(config)).andReturn(mockLdapTemplate);
        expect(mockLdapDao.findGroups(mockLdapTemplate, config)).andReturn(groups);
        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, group)).andReturn(entities);

        LdapGroup nestedGroup = new LdapGroup();
        expect(mockLdapDao.findGroup(mockLdapTemplate, config, groupDistinguishedName)).andReturn(nestedGroup);

        List<AcmLdapEntity> nestedUsers = new ArrayList<>();
        AcmUser user1 = new AcmUser();
        AcmUser user2 = new AcmUser();
        AcmRole role1 = new AcmRole();
        role1.setDistinguishedName("dnRole1");
        nestedUsers.add(user1);
        nestedUsers.add(user2);
        nestedUsers.add(role1);

        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, nestedGroup)).andReturn(nestedUsers);

        expect(mockLdapDao.findGroup(mockLdapTemplate, config, role1.getDistinguishedName())).andReturn(nestedGroup);

        AcmUser user3 = new AcmUser();
        AcmUser user4 = new AcmUser();
        List<AcmLdapEntity> secondLevelNestedUsers = new ArrayList<>();
        secondLevelNestedUsers.add(user3);
        secondLevelNestedUsers.add(user4);

        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, nestedGroup)).andReturn(secondLevelNestedUsers);

        replayAll();

        unit.queryLdapUsers(config, directoryName, roles, users, usersByApplicationRole, usersByLdapGroup, childParentPairs);

        verifyAll();

        assertEquals(5, users.size());
        assertEquals(1, usersByApplicationRole.size());
        assertEquals(1, usersByLdapGroup.size());
    }

    @Test
    public void testPopulateChildParentPair()
    {
        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setGroupName("GROUP-1");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setGroupName("GROUP-2");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setGroupName("GROUP-3");

        //set parents
        Set<String> parentsGroup1 = new HashSet<>();
        parentsGroup1.add(ldapGroup2.getGroupName());
        ldapGroup1.setMemberOfGroups(parentsGroup1);
        Set<String> parentsGroup3 = new HashSet<>();
        parentsGroup3.add(ldapGroup2.getGroupName());
        ldapGroup3.setMemberOfGroups(parentsGroup3);
        // Group 2 has no parents
        ldapGroup2.setMemberOfGroups(new HashSet<>());

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(ldapGroup1);
        ldapGroups.add(ldapGroup2);
        ldapGroups.add(ldapGroup3);

        Map<String, String> expected = new HashMap<>();
        expected.put(ldapGroup1.getGroupName(), ldapGroup2.getGroupName());
        expected.put(ldapGroup3.getGroupName(), ldapGroup2.getGroupName());

        Map<String, String> childParentPair = unit.populateGroupParentPairs(ldapGroups);

        log.debug("Expected: {}", expected);
        log.debug("Actual: {}", childParentPair);
        assertThat("Map should be equal", childParentPair.entrySet(), everyItem(isIn(expected.entrySet())));
    }

    @Test
    public void testSetParentGroupsToGroups()
    {
        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setGroupName("GROUP-1");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setGroupName("GROUP-2");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setGroupName("GROUP-3");

        //set parents existing groups
        Set<String> parentsGroup1 = new HashSet<>();
        parentsGroup1.add(ldapGroup2.getGroupName());
        Set<String> parentsGroup3 = new HashSet<>();
        parentsGroup3.add(ldapGroup2.getGroupName());

        String[] expectedParentsGroup1 = parentsGroup1.toArray(new String[parentsGroup1.size()]);
        String[] expectedParentsGroup2 = new String[0];
        String[] expectedParentsGroup3 = parentsGroup3.toArray(new String[parentsGroup3.size()]);

        // add some non-existing LDAP groups
        parentsGroup1.add("GROUP-X");
        ldapGroup1.setMemberOfGroups(parentsGroup1);
        parentsGroup3.add("GROUP-W");
        parentsGroup3.add("GROUP-Z");
        ldapGroup3.setMemberOfGroups(parentsGroup3);
        ldapGroup2.setMemberOfGroups(new HashSet<>());

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(ldapGroup1);
        ldapGroups.add(ldapGroup2);
        ldapGroups.add(ldapGroup3);

        unit.filterParentGroups(ldapGroups);

        assertThat("Arrays should be equal", ldapGroups.get(0).getMemberOfGroups(), containsInAnyOrder(expectedParentsGroup1));
        assertThat("Arrays should be equal", ldapGroups.get(1).getMemberOfGroups(), containsInAnyOrder(expectedParentsGroup2));
        assertThat("Arrays should be equal", ldapGroups.get(2).getMemberOfGroups(), containsInAnyOrder(expectedParentsGroup3));
    }

    @Test
    public void testGetUsersByLdapGroup()
    {
        List<LdapGroup> ldapGroups = new ArrayList<>();
        List<AcmUser> ldapUsers = new ArrayList<>();
        Map<String, List<AcmUser>> expected = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        Map<String, List<AcmUser>> actual = unit.getUsersByLdapGroup(ldapGroups, ldapUsers);

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    @Test
    public void testGetUsersByApplicationRole()
    {
        List<LdapGroup> ldapGroups = new ArrayList<>();
        List<AcmUser> ldapUsers = new ArrayList<>();
        Map<String, List<AcmUser>> ldapGroupUsers = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        AcmLdapSyncConfig mockLdapSyncConfig = createMock(AcmLdapSyncConfig.class);
        unit.setLdapSyncConfig(mockLdapSyncConfig);

        Map<String, String> roleToGroupMap = new HashMap<>();
        List<String> groups = new ArrayList<>(ldapGroupUsers.keySet());
        Map<String, List<AcmUser>> expected = new HashMap<>();
        for (int i = 0; i < 3; ++i)
        {
            Collections.shuffle(groups);
            List<String> someGroups = groups.subList(0, groups.size() - 1);
            String groupsString = String.join(",", someGroups);
            String role = String.format("ROLE-%d", i + 1);
            roleToGroupMap.put(role, groupsString);
            Set<AcmUser> usersSet = new HashSet<>();
            for (String group : someGroups)
            {
                System.out.println(group);
                usersSet.addAll(ldapGroupUsers.get(group));
            }
            expected.put(role, new ArrayList<>(usersSet));
        }

        expect(mockLdapSyncConfig.getRoleToGroupMap()).andReturn(roleToGroupMap);

        replayAll();

        Map<String, List<AcmUser>> actual = unit.getUsersByApplicationRole(ldapGroupUsers);

        verifyAll();

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    private Map<String, List<AcmUser>> setupTestLdapUsersGroups(List<LdapGroup> ldapGroups, List<AcmUser> ldapUsers)
    {
        Map<String, List<AcmUser>> mockResult = new TreeMap<>();

        for (int i = 0; i < 3; ++i)
        {
            String group = String.format("GROUP-%d", i + 1);
            mockResult.put(group, new ArrayList<>());
            LdapGroup ldapGroup = new LdapGroup();
            ldapGroup.setGroupName(group);
            ldapGroup.setMemberOfGroups(new HashSet<>());
            ldapGroups.add(ldapGroup);
        }
        LdapGroup parent = new LdapGroup();
        parent.setGroupName("PARENT-GROUP");
        parent.setMemberOfGroups(new HashSet<>());
        ldapGroups.add(parent);
        mockResult.put(parent.getGroupName(), new ArrayList<>());

        // set parent to group
        ldapGroups.get(0).setMemberOfGroups(new HashSet<>(Arrays.asList(parent.getGroupName())));

        for (int i = 0; i < 5; ++i)
        {
            AcmUser user = new AcmUser();
            user.setDistinguishedName(String.format("USER-%d", i + 1));
            user.setUserId(String.valueOf(i + 1));
            ldapUsers.add(user);
        }

        for (int i = 0; i < ldapGroups.size(); ++i)
        {
            LdapGroup group = ldapGroups.get(i);
            String groupName = group.getGroupName();
            if (i < ldapGroups.size() - 1)
            {
                ldapUsers.get(i).getLdapGroups().add(groupName);
                mockResult.get(groupName).add(ldapUsers.get(i));
                ldapUsers.get(i + 1).getLdapGroups().add(groupName);
                mockResult.get(groupName).add(ldapUsers.get(i + 1));
            } else
            {
                // last group with all users
                for (AcmUser user : ldapUsers)
                {
                    if (!group.getGroupName().equals("PARENT-GROUP"))
                    {
                        user.getLdapGroups().add(groupName);
                        mockResult.get(groupName).add(user);
                    }
                }
            }
        }

        // add parent group in result
        mockResult.put(parent.getGroupName(), mockResult.get(ldapGroups.get(0).getGroupName()));
        return mockResult;
    }

    private void printMap(Map<String, List<AcmUser>> map)
    {
        System.out.println("Printing result map");
        for (String group : map.keySet())
        {
            System.out.println(group);
            for (AcmUser user : map.get(group))
            {
                System.out.println(String.format("-> member is: %s", user.getDistinguishedName()));
            }
        }

    }

    @Test
    public void testSetGroupsToUsers()
    {
        Set<String> potentialUserGroups = fromArray("GROUP-A", "GROUP-B", "GROUP-C");

        AcmUser user = new AcmUser();
        user.setDistinguishedName("USER-1");
        user.setLdapGroups(potentialUserGroups);
        List<AcmUser> ldapUsers = new ArrayList<>();
        ldapUsers.add(user);

        LdapGroup ldapGroup1 = new LdapGroup();
        ldapGroup1.setGroupName("GROUP-A");
        LdapGroup ldapGroup2 = new LdapGroup();
        ldapGroup2.setGroupName("GROUP-B");
        LdapGroup ldapGroup3 = new LdapGroup();
        ldapGroup3.setGroupName("GROUP-X");

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
        List<AcmUser> ldapUsers = new ArrayList<>();
        List<AcmUser> expected = new ArrayList<>();
        for (int i = 0; i < 5000; ++i)
        {
            AcmUser user = new AcmUser();
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
            ldapGroup.setGroupName(String.format("GROUP-%d", (i + 1)));
            ldapGroups.add(ldapGroup);
        }
        LdapGroup found = new LdapGroup();
        found.setGroupName("GROUP-A");
        ldapGroups.add(found);
        long start = System.currentTimeMillis();
        List<AcmUser> result = unit.filterUsersForKnownGroups(ldapUsers, ldapGroups);
        log.debug("Took: {}ms", System.currentTimeMillis() - start);
        assertThat("Arrays should be equal", result, containsInAnyOrder(expected.toArray()));
    }


}
