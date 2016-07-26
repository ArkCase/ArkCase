package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
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
import static org.junit.Assert.assertThat;

/**
 * Created by armdev on 7/3/14.
 */
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
        Map<String, Set<AcmUser>> expected = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        Map<String, Set<AcmUser>> actual = unit.getUsersByLdapGroup(ldapGroups, ldapUsers);

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    @Test
    public void testGetUsersByApplicationRole()
    {
        List<LdapGroup> ldapGroups = new ArrayList<>();
        List<AcmUser> ldapUsers = new ArrayList<>();
        Map<String, Set<AcmUser>> ldapGroupUsers = setupTestLdapUsersGroups(ldapGroups, ldapUsers);

        AcmLdapSyncConfig mockLdapSyncConfig = createMock(AcmLdapSyncConfig.class);
        unit.setLdapSyncConfig(mockLdapSyncConfig);

        Map<String, String> roleToGroupMap = new HashMap<>();
        List<String> groups = new ArrayList<>(ldapGroupUsers.keySet());
        Map<String, Set<AcmUser>> expected = new HashMap<>();
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
            expected.put(role, usersSet);
        }

        expect(mockLdapSyncConfig.getRoleToGroupMap()).andReturn(roleToGroupMap);

        replayAll();

        Map<String, Set<AcmUser>> actual = unit.getUsersByApplicationRole(ldapGroupUsers);

        verifyAll();

        printMap(expected);
        printMap(actual);

        assertThat("Map should be equal", actual.entrySet(), everyItem(isIn(expected.entrySet())));

    }

    private Map<String, Set<AcmUser>> setupTestLdapUsersGroups(List<LdapGroup> ldapGroups, List<AcmUser> ldapUsers)
    {
        Map<String, Set<AcmUser>> mockResult = new TreeMap<>();

        for (int i = 0; i < 3; ++i)
        {
            String group = String.format("GROUP-%d", i + 1);
            mockResult.put(group, new HashSet<>());
            LdapGroup ldapGroup = new LdapGroup();
            ldapGroup.setGroupName(group);
            ldapGroup.setMemberOfGroups(new HashSet<>());
            ldapGroups.add(ldapGroup);
        }
        LdapGroup parent = new LdapGroup();
        parent.setGroupName("PARENT-GROUP");
        parent.setMemberOfGroups(new HashSet<>());
        ldapGroups.add(parent);
        mockResult.put(parent.getGroupName(), new HashSet<>());

        // set parent to groups 1 and 2
        ldapGroups.get(0).setMemberOfGroups(new HashSet<>(Arrays.asList(parent.getGroupName())));
        ldapGroups.get(1).setMemberOfGroups(new HashSet<>(Arrays.asList(parent.getGroupName())));

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
        Set<AcmUser> allUsers = new HashSet<>();
        Set<AcmUser> firstGroupUsers = mockResult.get(ldapGroups.get(0).getGroupName());
        Set<AcmUser> secondGroupUsers = mockResult.get(ldapGroups.get(1).getGroupName());
        allUsers.addAll(firstGroupUsers);
        allUsers.addAll(secondGroupUsers);
        mockResult.put(parent.getGroupName(), allUsers);
        return mockResult;
    }

    private void printMap(Map<String, Set<AcmUser>> map)
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
