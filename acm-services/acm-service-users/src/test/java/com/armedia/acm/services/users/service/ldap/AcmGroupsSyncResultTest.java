package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
         * Group B ->
         *       member cn=3,cn=Users
         * Group C ->
         *       member
         */
        // @formatter:on
        @Test
        public void allNewLdapGroupsWithOnlyUserMembersTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), new ArrayList<>(), currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));

            assertThat(unit.getChangedGroups(), is(empty()));
            assertThat(unit.getNewGroups().size(), is(3));
            assertThat("New groups should be:",
                    unit.getNewGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A", "B", "C"))));

            assertThat(unit.getUserNewGroups().size(), is(3));
            assertThat("For user 1 groups for roles should be:", unit.getUserNewGroups().get("1"),
                    everyItem(isIn(fromArray("A"))));
            assertThat("For user 2 groups for roles should be:", unit.getUserNewGroups().get("2"),
                    everyItem(isIn(fromArray("A"))));
            assertThat("For user 3 groups for roles should be:", unit.getUserNewGroups().get("3"),
                    everyItem(isIn(fromArray("B"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->
         *       member cn=1,cn=Users
         *       member cn=2,cn=Users
         * Group B ->
         *       member cn=3,cn=Users
         *       member cn=A,cn=Groups
         * Group C ->
         *       member
         */
        // @formatter:on
        @Test
        public void allNewLdapGroupsWithMemberGroupTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users", "cn=A,cn=Groups"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), new ArrayList<>(), currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));

            assertThat(unit.getChangedGroups(), is(empty()));
            assertThat(unit.getNewGroups().size(), is(3));
            assertThat("New groups should be:",
                    unit.getNewGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A", "B", "C"))));

            assertThat(unit.getUserNewGroups().size(), is(3));
            assertThat("For user 1 groups for roles should be:", unit.getUserNewGroups().get("1"),
                    everyItem(isIn(fromArray("A", "B"))));
            assertThat("For user 2 groups for roles should be:", unit.getUserNewGroups().get("2"),
                    everyItem(isIn(fromArray("A", "B"))));
            assertThat("For user 3 groups for roles should be:", unit.getUserNewGroups().get("3"),
                    everyItem(isIn(fromArray("B"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->
         *       member cn=1,cn=Users
         *       member cn=2,cn=Users
         *       member cn=B,cn=Groups
         * Group B ->
         *       member cn=3,cn=Users
         *       member cn=A,cn=Groups
         * Group C ->
         *       member
         */
        // @formatter:on
        @Test
        public void allNewLdapGroupsWithMemberGroupCycleTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=B,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users", "cn=A,cn=Groups"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), new ArrayList<>(), currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));

            assertThat(unit.getChangedGroups(), is(empty()));
            assertThat(unit.getNewGroups().size(), is(3));
            assertThat("New groups should be:",
                    unit.getNewGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A", "B", "C"))));

            assertThat(unit.getUserNewGroups().size(), is(3));
            assertThat("For user 1 groups for roles should be:", unit.getUserNewGroups().get("1"),
                    everyItem(isIn(fromArray("A", "B"))));
            assertThat("For user 2 groups for roles should be:", unit.getUserNewGroups().get("2"),
                    everyItem(isIn(fromArray("A", "B"))));
            assertThat("For user 3 groups for roles should be:", unit.getUserNewGroups().get("3"),
                    everyItem(isIn(fromArray("A", "B"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->                    AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=Users             member cn=2,cn=Users
         * Group B ->                    AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         * Group C ->                    AcmGroup C ->
         *       member                           member
         */
        // @formatter:on
        @Test
        public void onlyOneChangedGroupWithUpdatedDescriptionTest()
        {
            LdapGroup a = ldapGroup("A", "Updated group");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A"))));
            assertThat("Changed group A should have description updated", unit.getChangedGroups().get(0).getDescription(),
                    equalTo(a.getDescription()));

            assertThat(unit.getUserNewGroups().size(), is(0));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->                    AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=Users             member cn=2,cn=Users
         * Group B ->                    AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         * Group C ->                    AcmGroup C ->
         *       member cn=4,cn=Users             member
         */
        // @formatter:on
        @Test
        public void changedGroupWithNewUserAddedTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
            b.setMembers(fromArray("cn=3,cn=Users"));
            c.setMembers(fromArray("cn=4,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));
            assertThat("User 4 has groups:", groupNamesByUserIdMap.get("4"), everyItem(isIn(fromArray("C"))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("C"))));
            assertThat("Changed group C should have new user member", unit.getChangedGroups().get(0).getUserMembers(),
                    everyItem(isIn(userStream(u4).collect(Collectors.toSet()))));

            assertThat(unit.getUserNewGroups().size(), is(1));
            assertThat("User 4 should be added to new group", unit.getUserNewGroups().keySet(),
                    everyItem(isIn(fromArray("4"))));
            assertThat("User 4 should be added to group C", unit.getUserNewGroups().get("4"),
                    everyItem(isIn(fromArray("C"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->                    AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=Users             member cn=2,cn=Users
         *       member cn=C,cn=Groups            member cn=C,cn=Groups
         * Group B ->                    AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         * Group C ->                    AcmGroup C ->
         *       member cn=4,cn=Users             member
         */
        // @formatter:on
        @Test
        public void changedGroupWithAscendantsWithNewUserAddedTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=C,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users"));
            c.setMembers(fromArray("cn=4,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupA.setMemberGroups(groupSet(acmGroupC));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));
            assertThat("User 4 has groups:", groupNamesByUserIdMap.get("4"), everyItem(isIn(fromArray("C"))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("C"))));
            assertThat("Changed group C should have new user member", unit.getChangedGroups().get(0).getUserMembers(),
                    everyItem(isIn(userStream(u4).collect(Collectors.toSet()))));

            assertThat(unit.getUserNewGroups().size(), is(1));
            assertThat("User 4 should be added to new group", unit.getUserNewGroups().keySet(),
                    everyItem(isIn(fromArray("4"))));
            assertThat("User 4 should be added to group A and C", unit.getUserNewGroups().get("4"),
                    everyItem(isIn(fromArray("A", "C"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->                    AcmGroup A ->
         *       member cn=1,cn=Users             member cn=1,cn=Users
         *       member cn=2,cn=Users             member cn=2,cn=Users
         *       member cn=C,cn=Groups
         * Group B ->                    AcmGroup B ->
         *       member cn=3,cn=Users             member cn=3,cn=Users
         * Group C ->                    AcmGroup C ->
         *       member cn=4,cn=Users             member cn=4,cn=Users
         */
        // @formatter:on
        @Test
        public void changedGroupWithMemberGroupAddedTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=C,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users"));
            c.setMembers(fromArray("cn=4,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupC.setUserMembers(userStream(u4).collect(Collectors.toSet()));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));
            assertThat("User 4 has groups:", groupNamesByUserIdMap.get("4"), everyItem(isIn(fromArray("C"))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A"))));
            assertThat("Changed group A should have new group member", unit.getChangedGroups().get(0)
                            .getGroupMemberIds().collect(Collectors.toSet()),
                    everyItem(isIn(fromArray("C"))));

            assertThat(unit.getUserNewGroups().size(), is(1));
            assertThat("User 4 should be added to new group", unit.getUserNewGroups().keySet(),
                    everyItem(isIn(fromArray("4"))));
            assertThat("User 4 should be added to group A", unit.getUserNewGroups().get("4"),
                    everyItem(isIn(fromArray("A"))));
            assertThat(unit.getUserRemovedGroups().size(), is(0));
        }

    // @formatter:off
        /**
         * Group A ->                     AcmGroup A ->
         *       member cn=1,cn=Users              member cn=1,cn=Users
         *       member cn=2,cn=Users              member cn=2,cn=Users
         *       member cn=C,cn=Groups             member cn=C,cn=Users
         * Group B ->                     AcmGroup B ->
         *       member cn=3,cn=Users              member cn=3,cn=Users
         * Group C ->                     AcmGroup C ->
         *       member                            member cn=4,cn=Users
         */
        // @formatter:on
        @Test
        public void changedGroupWithUserMemberRemovedTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users", "cn=C,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupA.setMemberGroups(groupSet(acmGroupC));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupC.setUserMembers(userStream(u4).collect(Collectors.toSet()));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has groups:", groupNamesByUserIdMap.get("2"), everyItem(isIn(fromArray("A"))));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));
            assertThat("User 4 has no groups:", groupNamesByUserIdMap.get("4"), equalTo(null));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("C"))));
            assertThat("Changed group C should have removed member", unit.getChangedGroups().get(0)
                    .getGroupMemberIds().count(), equalTo(0L));

            assertThat(unit.getUserNewGroups().size(), is(0));
            assertThat(unit.getUserRemovedGroups().size(), is(1));
            assertThat("User 4 should be removed from group", unit.getUserNewGroups().keySet(),
                    everyItem(isIn(fromArray("4"))));
            assertThat("User 4 should be removed from group A and C", unit.getUserRemovedGroups().get("4"),
                    everyItem(isIn(fromArray("A", "C"))));
        }

    // @formatter:off
        /**
         * Group A ->                     AcmGroup A ->
         *       member cn=1,cn=Users              member cn=1,cn=Users
         *                                         member cn=2,cn=Users
         *       member cn=C,cn=Groups             member cn=C,cn=Users
         * Group B ->                     AcmGroup B ->
         *       member cn=3,cn=Users              member cn=3,cn=Users
         *       member cn=A,cn=Groups             member cn=A,cn=Groups
         * Group C ->                     AcmGroup C ->
         *       member cn=4,cn=Users              member cn=4,cn=Users
         *       member cn=B,cn=Groups             member cn=B,cn=Groups
         */
        // @formatter:on
        @Test
        public void changedGroupWithMultipleAscendantsUserMemberRemovedTest()
        {
            LdapGroup a = ldapGroup("A", "");
            LdapGroup b = ldapGroup("B", "");
            LdapGroup c = ldapGroup("C", "");

            AcmUser u1 = acmUser("1");
            AcmUser u2 = acmUser("2");
            AcmUser u3 = acmUser("3");
            AcmUser u4 = acmUser("4");

            a.setMembers(fromArray("cn=1,cn=Users", "cn=C,cn=Groups"));
            b.setMembers(fromArray("cn=3,cn=Users", "cn=A,cn=Groups"));
            c.setMembers(fromArray("cn=4,cn=Users", "cn=B,cn=Groups"));

            Map<String, AcmUser> currentUsers = userStream(u1, u2, u3, u4)
                    .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

            AcmGroup acmGroupA = acmGroup("A", "");
            AcmGroup acmGroupB = acmGroup("B", "");
            AcmGroup acmGroupC = acmGroup("C", "");

            acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
            acmGroupA.setMemberGroups(groupSet(acmGroupC));
            acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
            acmGroupB.setMemberGroups(groupSet(acmGroupA));
            acmGroupC.setUserMembers(userStream(u4).collect(Collectors.toSet()));
            acmGroupC.setMemberGroups(groupSet(acmGroupB));

            List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

            Map<String, Set<String>> groupNamesByUserIdMap = unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

            assertThat("Key set should be:", groupNamesByUserIdMap.keySet(), everyItem(isIn(currentUsers.keySet())));
            assertThat("User 1 has groups:", groupNamesByUserIdMap.get("1"), everyItem(isIn(fromArray("A"))));
            assertThat("User 2 has no groups:", groupNamesByUserIdMap.get("2"), equalTo(null));
            assertThat("User 3 has groups:", groupNamesByUserIdMap.get("3"), everyItem(isIn(fromArray("B"))));
            assertThat("User 4 has groups:", groupNamesByUserIdMap.get("4"), everyItem(isIn(fromArray("C"))));

            assertThat(unit.getNewGroups(), is(empty()));
            assertThat(unit.getChangedGroups().size(), is(1));
            assertThat("Changed groups should be:",
                    unit.getChangedGroups()
                            .stream()
                            .map(AcmGroup::getName)
                            .collect(Collectors.toList()), everyItem(isIn(fromArray("A"))));
            assertThat("Changed group A should have removed member", unit.getChangedGroups().get(0)
                    .getGroupMemberIds().count(), equalTo(1L));

            assertThat(unit.getUserNewGroups().size(), is(0));
            assertThat(unit.getUserRemovedGroups().size(), is(1));
            assertThat("User 2 should be removed from group", unit.getUserNewGroups().keySet(),
                    everyItem(isIn(fromArray("2"))));
            assertThat("User 2 should be removed from group A, B and C", unit.getUserRemovedGroups().get("2"),
                    everyItem(isIn(fromArray("A", "B", "C"))));
        }

    private LdapGroup ldapGroup(String name, String description)
    {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(name);
        ldapGroup.setDistinguishedName(String.format("cn=%s,cn=Groups", name));
        ldapGroup.setDescription(description);
        return ldapGroup;
    }

    private AcmUser acmUser(String id)
    {
        AcmUser acmUser = new AcmUser();
        acmUser.setUserId(id);
        acmUser.setDistinguishedName(String.format("cn=%s,cn=Users", id));
        return acmUser;
    }

    private AcmGroup acmGroup(String name, String description)
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(name);
        acmGroup.setDistinguishedName(String.format("cn=%s,cn=Groups", name));
        acmGroup.setDescription(description);
        return acmGroup;
    }

    private Stream<AcmUser> userStream(AcmUser... users)
    {
        return Arrays.stream(users);
    }

    private Set<AcmGroup> groupSet(AcmGroup... groups)
    {
        return Arrays.stream(groups).collect(Collectors.toSet());
    }

    static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }
}
