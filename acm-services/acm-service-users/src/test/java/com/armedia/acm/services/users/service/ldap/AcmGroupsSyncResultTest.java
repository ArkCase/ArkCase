package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Every.everyItem;

public class AcmGroupsSyncResultTest
{
    private AcmGroupsSyncResult unit;

    @Before
    public void setup()
    {
        unit = new AcmGroupsSyncResult();
    }

    // @formatter:off
        /**
         * Group A ->
         *       member cn=1,cn=Users
         *       member cn=2,cn=Users
         *       member cn=B,cn=Groups
         *       member cn=C,cn=Groups
         * Group B ->
         *       member cn=3,cn=Users
         *       member cn=A,cn=Groups
         *       member cn=C,cn=Groups
         * Group C ->
         *       member cn=A,cn=Groups
         */
        // @formatter:on
        @Test
        public void syncAllNewLdapGroupsWithMemberGroupCycleTest()
        {
            LdapGroup a = ldapGroup("A");
            LdapGroup b = ldapGroup("B");
            LdapGroup c = ldapGroup("C");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=B,cn=Groups", "cn=C,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users", "cn=A,cn=Groups", "cn=C,cn=Groups"));
            c.setMembers(fromArray("cn=A,cn=Groups"));

            Map<String, AcmUser> acmUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            Map<String, Set<AcmGroup>> groupsByUserIdMap = unit.sync(Arrays.asList(a, b, c), new ArrayList<>(), acmUsers);

            assertThat("Key set should be:", groupsByUserIdMap.keySet(), everyItem(isIn(acmUsers.keySet())));
            assertThat("User 1 has groups:", groupsByUserIdMap.get("1"),
                    everyItem(isIn(groupSet(acmGroup("A"), acmGroup("B"), acmGroup("C")))));
            assertThat("User 2 has groups:", groupsByUserIdMap.get("2"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 3 has groups:", groupsByUserIdMap.get("3"),
                    everyItem(isIn(groupSet(acmGroup("B")))));

            assertThat(unit.getModifiedGroups(), is(empty()));
            assertThat(unit.getNewGroups().size(), is(3));
            assertThat("New groups should be:",
                    unit.getNewGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A", "B", "C"))));

            Map<String, AcmGroup> syncedNewGroups = getGroupByName(unit.getNewGroups());

            assertThat("Group A should have B and C as member group", syncedNewGroups.get("A").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("B", "C"))));
            assertThat("Ascendants string for group A should be", syncedNewGroups.get("A").getAscendantsList(), is("B,C"));
            assertThat("Group B should have A and C as member group", syncedNewGroups.get("B").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "C"))));
            assertThat("Ascendants string for group B should be", syncedNewGroups.get("B").getAscendantsList(), is("A,C"));
            assertThat("Group C should have 1 group member", syncedNewGroups.get("C").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("A"))));
            assertThat("Ascendants string for group C should be", syncedNewGroups.get("C").getAscendantsList(), is("A,B"));

            Map<String, AcmGroup> groupForRoles = getSyncedGroups(groupsByUserIdMap);

            assertThat("Group A should have B and C as member groups", groupForRoles.get("A").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("B", "C"))));
            assertThat("Ascendants string for group A should be", groupForRoles.get("A").getAscendantsList(), is("B,C"));
            assertThat("Group B should have A and C as member groups", groupForRoles.get("B").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "C"))));
            assertThat("Ascendants string for group B should be", groupForRoles.get("B").getAscendantsList(), is("A,C"));
            assertThat("Group C not found since it has no users", groupForRoles.containsKey("C"), is(false));
        }

    // @formatter:off
        /**
         * ldap state                        db state
         * Group A ->                        AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=User              member cn=2,cn=Users
         *       description 'Updated group'      description ''
         * Group B ->                        AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         *       member cn=C,cn=Groups            member cn=C,cn=Groups
         * Group C ->                        AcmGroup C ->
         *       member                           member
         */
        // @formatter:on
        @Test
        public void syncOnlyOneChangedGroupWithUpdatedDescriptionTest()
        {
            LdapGroup a = ldapGroup("A", "Updated group");
            LdapGroup b = ldapGroup("B");
            LdapGroup c = ldapGroup("C");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users", "cn=C,cn=Groups"));

            Map<String, AcmUser> acmUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A");
            AcmGroup acmGroupB = acmGroup("B");
            AcmGroup acmGroupC = acmGroup("C", "", "B");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupB.setMemberGroups(groupSet(acmGroupC));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<AcmGroup>> groupsByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, acmUsers);

            assertThat("Key set should be:", groupsByUserIdMap.keySet(), everyItem(isIn(acmUsers.keySet())));
            assertThat("User 1 has groups:", groupsByUserIdMap.get("1"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 2 has groups:", groupsByUserIdMap.get("2"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 3 has groups:", groupsByUserIdMap.get("3"),
                    everyItem(isIn(groupSet(acmGroup("B")))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getDeletedGroups(), is(empty()));
            assertThat(unit.getModifiedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getModifiedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A"))));
            AcmGroup actualGroupA = unit.getModifiedGroups().get(0);
            assertThat("Changed group A should have description updated", actualGroupA.getDescription(),
                    equalTo(a.getDescription()));
            assertThat("Group A should have 0 member groups", actualGroupA.getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group A should be", actualGroupA.getAscendantsList(), nullValue());

            Map<String, AcmGroup> groupForRoles = getSyncedGroups(groupsByUserIdMap);

            assertThat("Group A should have 0 member groups", groupForRoles.get("A").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group A should be", groupForRoles.get("A").getAscendantsList(), nullValue());
            assertThat("Group B should have C as member group", groupForRoles.get("B").getGroupMemberNames()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("C"))));
            assertThat("Ascendants string for group B should be", groupForRoles.get("B").getAscendantsList(), nullValue());
            assertThat("Group C not found since it has no users", groupForRoles.containsKey("C"), is(false));
        }

    // @formatter:off
        /**
         * Group A ->                    AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=4,cn=Users             member cn=2,cn=Users
         * Group B ->                    AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         * Group C ->                    AcmGroup C ->
         *       member cn=4,cn=Users             member cn=4,cn=Users
         */
        // @formatter:on
        @Test
        public void changedGroupsWithAddedAndRemovedUsersTest()
        {
            LdapGroup a = ldapGroup("A");
            LdapGroup b = ldapGroup("B");
            LdapGroup c = ldapGroup("C");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=4,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users"));
            c.setMembers(fromArray("cn=4,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A");
            AcmGroup acmGroupB = acmGroup("B");
            AcmGroup acmGroupC = acmGroup("C");

            acmGroupA.setUserMembers(userSet(u1, u2));
            acmGroupB.setUserMembers(userSet(u3));
            acmGroupC.setUserMembers(userSet(u4));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<AcmGroup>> groupsByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupsByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupsByUserIdMap.get("1"), everyItem(isIn(groupSet(acmGroupA))));
            assertThat("User 2 has groups:", groupsByUserIdMap.containsKey("2"), is(false));
            assertThat("User 3 has groups:", groupsByUserIdMap.get("3"), everyItem(isIn(groupSet(acmGroupB))));
            assertThat("User 4 has groups:", groupsByUserIdMap.get("4"), everyItem(isIn(groupSet(acmGroupA, acmGroupC))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getModifiedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getModifiedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A"))));
            assertThat("Changed group A should have user members", unit.getModifiedGroups().get(0).getUserMembers(),
                    everyItem(isIn(userSet(u4, u1))));
            assertThat("Ascendants string for C should be null", unit.getModifiedGroups().get(0).getAscendantsList(), nullValue());

            Map<String, AcmGroup> syncedGroups = getSyncedGroups(groupsByUserIdMap);

            assertThat("Group A should have member groups", syncedGroups.get("A").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group A should be", syncedGroups.get("A").getAscendantsList(), nullValue());
            assertThat("Group A should have user members", syncedGroups.get("A").getUserMembers(),
                    everyItem(isIn(userSet(u4, u1))));

            assertThat("Group B should have member group", syncedGroups.get("B").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group B should be", syncedGroups.get("B").getAscendantsList(), nullValue());
            assertThat("Group B should have user members", syncedGroups.get("B").getUserMembers(),
                    everyItem(isIn(userSet(u3))));

            assertThat("Group C should have member group", syncedGroups.get("C").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group C should be", syncedGroups.get("C").getAscendantsList(), nullValue());
            assertThat("Group C should have user members", syncedGroups.get("C").getUserMembers(),
                    everyItem(isIn(userSet(u4))));
        }

    // @formatter:off
        /**
         * ldap state                        db state
         *
         * Group A ->                        AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=Users             member cn=2,cn=Users
         * Group B ->                        AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         *                                        member cn=C,cn=Groups
         * Group C ->                        AcmGroup C ->
         *       member cn=A,cn=Groups            member
         *       member cn=D,cn=Groups
         * Group D ->                        AcmGroup D ->
         *       member cn=A,cn=Groups            member cn=A,cn=Groups
         */
        // @formatter:on
        @Test
        public void syncGroupsWithAddedAndRemovedGroupMembersTest()
        {
            LdapGroup a = ldapGroup("A");
            LdapGroup b = ldapGroup("B");
            LdapGroup c = ldapGroup("C");
            LdapGroup d = ldapGroup("D");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users"));
            c.setMembers(fromArray("cn=A,cn=Groups", "cn=D,cn=Groups"));
            d.setMembers(fromArray("cn=A,cn=Groups"));

            Map<String, AcmUser> acmUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "", "D");
            AcmGroup acmGroupB = acmGroup("B");
            AcmGroup acmGroupC = acmGroup("C", "", "B");
            AcmGroup acmGroupD = acmGroup("D");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupB.setMemberGroups(groupSet(acmGroupC));
            acmGroupD.setMemberGroups(groupSet(acmGroupA));

            List<AcmGroup> acmGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC, acmGroupD);

            Map<String, Set<AcmGroup>> groupsByUserIdMap = unit.sync(Arrays.asList(a, b, c, d), acmGroups, acmUsers);

            assertThat("Key set should be:", groupsByUserIdMap.keySet(), everyItem(isIn(acmUsers.keySet())));
            assertThat("User 1 has groups:", groupsByUserIdMap.get("1"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 2 has groups:", groupsByUserIdMap.get("2"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 3 has groups:", groupsByUserIdMap.get("3"),
                    everyItem(isIn(groupSet(acmGroup("B")))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getDeletedGroups(), is(empty()));
            assertThat(unit.getModifiedGroups().size(), is(4));

            Map<String, AcmGroup> modifiedGroupsByName = getGroupByName(unit.getModifiedGroups());

            assertThat("Changed groups should be:", modifiedGroupsByName.keySet(),
                    everyItem(isIn(fromArray("A", "B", "C", "D"))));

            assertThat("Ascendants string for group A should be", modifiedGroupsByName.get("A").getAscendantsList(),
                    is("C,D"));

            assertThat("Group C should have member groups", modifiedGroupsByName.get("C")
                    .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "D"))));
            assertThat("Ascendants string for group C should be", modifiedGroupsByName.get("C").getAscendantsList(), nullValue());

            assertThat("Ascendants string for group D should be", modifiedGroupsByName.get("D").getAscendantsList(),
                    is("C"));

            Map<String, AcmGroup> groupForRoles = getSyncedGroups(groupsByUserIdMap);

            assertThat("Group A should have 0 member groups", groupForRoles.get("A").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group A should be", groupForRoles.get("A").getAscendantsList(), is("C,D"));
            assertThat("Group B should have 0 member group", groupForRoles.get("B").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group B should be", groupForRoles.get("B").getAscendantsList(), nullValue());
            assertThat("Group C not found since it has no users", groupForRoles.containsKey("C"), is(false));
            assertThat("Group D not found since it has no users", groupForRoles.containsKey("D"), is(false));
        }

    // @formatter:off
        /**
         * ldap state                        db state
         * Group A ->                        AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=User              member cn=2,cn=Users
         *       description 'Updated group'      description ''
         *       member cn=C,cn=Groups            member cn=C,cn=Groups
         *                                   AcmGroup B ->
         *                                        member cn=3,cn=Users
         *                                        member cn=C,cn=Groups
         * Group C ->                        AcmGroup C ->
         *       member                           member
         * Group D ->
         *       member cn=A,cn=Groups
         */
        // @formatter:on
        @Test
        public void syncDeletedNewAndModifiedGroupTest()
        {
            LdapGroup a = ldapGroup("A", "Updated group");
            LdapGroup c = ldapGroup("C");
            LdapGroup d = ldapGroup("D");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=C,cn=Groups"));
            d.setMembers(fromArray("cn=A,cn=Groups"));

            Map<String, AcmUser> acmUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A");
            AcmGroup acmGroupB = acmGroup("B");
            AcmGroup acmGroupC = acmGroup("C", "", "A");

            acmGroupA.setUserMembers(userSet(u1, u2));
            acmGroupA.setMemberGroups(groupSet(acmGroupC));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupB.setMemberGroups(groupSet(acmGroupC));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<AcmGroup>> groupsByUserIdMap = unit.sync(Arrays.asList(a, c, d), currentGroups, acmUsers);

            assertThat("Key set should be:", groupsByUserIdMap.keySet(), everyItem(isIn(acmUsers.keySet())));
            assertThat("User 1 should have groups:", groupsByUserIdMap.get("1"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 2 should have groups:", groupsByUserIdMap.get("2"),
                    everyItem(isIn(groupSet(acmGroup("A")))));
            assertThat("User 3 not member in any group:", groupsByUserIdMap.containsKey("3"), is(false));

            assertThat(unit.getNewGroups().get(0), is(acmGroup("D")));

            assertThat(unit.getDeletedGroups(), everyItem(isIn(groupSet(acmGroupB))));
            AcmGroup actualDeletedGroup = unit.getDeletedGroups().get(0);
            assertThat("Group B should have 0 member groups", actualDeletedGroup.getMemberGroups().size(), is(0));
            assertThat("Group B should have 0 user groups", actualDeletedGroup.getUserMembers().size(), is(0));
            assertThat("Ascendants string for group B should be", actualDeletedGroup.getAscendantsList(), nullValue());
            assertThat("Group B should have status inactive", actualDeletedGroup.getStatus(), equalTo(AcmGroupStatus.INACTIVE));

            assertThat(unit.getModifiedGroups().size(), is(2));

            Map<String, AcmGroup> modifiedGroupsByName = getGroupByName(unit.getModifiedGroups());

            assertThat("Changed groups should be:", modifiedGroupsByName.keySet(),
                    everyItem(isIn(fromArray("A", "C"))));
            assertThat("Changed group A should have description updated", modifiedGroupsByName.get("A").getDescription(),
                    equalTo(a.getDescription()));
            assertThat("Group A should have 0 member groups", modifiedGroupsByName.get("A").getMemberGroups().size(), is(1));
            assertThat("Group A should have user members", modifiedGroupsByName.get("A").getUserMemberIds()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("1", "2"))));
            assertThat("Ascendants string for group A should be", modifiedGroupsByName.get("A").getAscendantsList(), is("D"));

            assertThat("Group C should have 0 member groups", modifiedGroupsByName.get("C").getMemberGroups().size(), is(0));
            assertThat("Ascendants string for group C should be", modifiedGroupsByName.get("C").getAscendantsList(), is("A,D"));

            Map<String, AcmGroup> groupsForUserRoles = getSyncedGroups(groupsByUserIdMap);

            assertThat("Synced groups should be:", groupsForUserRoles.keySet(),
                    everyItem(isIn(fromArray("A"))));

            assertThat("Group A should have description updated", groupsForUserRoles.get("A").getDescription(),
                    equalTo(a.getDescription()));
            assertThat("Group A should have 0 member groups", groupsForUserRoles.get("A").getMemberGroups().size(), is(1));
            assertThat("Group A should have user members", groupsForUserRoles.get("A").getUserMemberIds()
                    .collect(Collectors.toSet()), everyItem(isIn(fromArray("1", "2"))));
            assertThat("Ascendants string for group A should be", groupsForUserRoles.get("A").getAscendantsList(), is("D"));
        }

    private LdapGroup ldapGroup(String name, String description)
    {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(name);
        ldapGroup.setDistinguishedName(String.format("cn=%s,cn=Groups", name));
        ldapGroup.setDescription(description);
        return ldapGroup;
    }

    private LdapGroup ldapGroup(String name)
    {
        return ldapGroup(name, "");
    }

    private AcmUser acmUser(String id)
    {
        AcmUser acmUser = new AcmUser();
        acmUser.setUserId(id);
        acmUser.setDistinguishedName(String.format("cn=%s,cn=Users", id));
        return acmUser;
    }

    private AcmGroup acmGroup(String name, String description, String ascendantsList)
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(name);
        acmGroup.setDistinguishedName(String.format("cn=%s,cn=Groups", name));
        acmGroup.setDescription(description);
        acmGroup.setStatus(AcmGroupStatus.ACTIVE);
        acmGroup.setAscendantsList(ascendantsList);
        return acmGroup;
    }

    private AcmGroup acmGroup(String name, String description)
    {
        return acmGroup(name, description, null);
    }

    private AcmGroup acmGroup(String name)
    {
        return acmGroup(name, "");
    }

    private Stream<AcmUser> userStream(AcmUser... users)
    {
        return Arrays.stream(users);
    }

    private Set<AcmUser> userSet(AcmUser... users)
    {
        return userStream(users).collect(Collectors.toSet());
    }

    private Set<AcmGroup> groupSet(AcmGroup... groups)
    {
        return Arrays.stream(groups).collect(Collectors.toSet());
    }

    private static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

    private Map<String, AcmGroup> getSyncedGroups(Map<String, Set<AcmGroup>> groupsByUserId)
    {
        return groupsByUserId.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }

    private Map<String, AcmGroup> getGroupByName(List<AcmGroup> groups)
    {
        return groups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }
}
