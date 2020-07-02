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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Every.everyItem;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.MapperUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmGroupsSyncResultTest
{
    private AcmGroupsSyncResult unit;

    private static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

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

        unit.sync(Arrays.asList(a, b, c), new ArrayList<>(), acmUsers);

        assertThat(unit.getModifiedGroups(), is(empty()));
        assertThat(unit.getNewGroups().size(), is(3));

        Map<String, AcmGroup> syncedNewGroups = getGroupByName(unit.getNewGroups());
        assertThat("New groups should be:", syncedNewGroups.keySet(), everyItem(isIn(fromArray("A", "B", "C"))));
        assertThat("Group A should have B and C as member group", syncedNewGroups.get("A").getGroupMemberNames()
                .collect(Collectors.toSet()), everyItem(isIn(fromArray("B", "C"))));
        assertThat("Ascendants string for group A should be", syncedNewGroups.get("A").getAscendantsList(), is("B||C"));
        assertThat("Group B should have A and C as member group", syncedNewGroups.get("B").getGroupMemberNames()
                .collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "C"))));
        assertThat("Ascendants string for group B should be", syncedNewGroups.get("B").getAscendantsList(), is("A||C"));
        assertThat("Group C should have 1 group member", syncedNewGroups.get("C").getGroupMemberNames()
                .collect(Collectors.toSet()), everyItem(isIn(fromArray("A"))));
        assertThat("Ascendants string for group C should be", syncedNewGroups.get("C").getAscendantsList(), is("A||B"));
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

        unit.sync(Arrays.asList(a, b, c), currentGroups, acmUsers);

        assertThat(unit.getNewGroups(), is(empty()));
        assertThat(unit.getDeletedGroups(), is(empty()));
        assertThat(unit.getModifiedGroups().size(), is(1));

        AcmGroup actualGroupA = unit.getModifiedGroups().get(0);
        assertThat("Changed group should be:", actualGroupA.getName(), is("A"));
        assertThat("Changed group A should have description updated", actualGroupA.getDescription(),
                equalTo(a.getDescription()));
        assertThat("Group A should have user members: ", actualGroupA.getUserMemberIds().collect(Collectors.toSet()),
                everyItem(isIn(Arrays.asList("1", "2"))));
        assertThat("Group A should have 0 member groups", actualGroupA.getMemberGroups().size(), is(0));
        assertThat("Ascendants string for group A should be", actualGroupA.getAscendantsList(), nullValue());
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

        unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

        AcmGroup actualGroupA = unit.getModifiedGroups().get(0);
        assertThat(unit.getModifiedGroups().size(), is(1));
        assertThat("Changed group should be:", actualGroupA.getName(), is("A"));
        assertThat("Changed group A should have user members", actualGroupA.getUserMembers(false),
                everyItem(isIn(userSet(u4, u1))));
        assertThat("Ascendants string for C should be null", actualGroupA.getAscendantsList(), nullValue());
    }

    // @formatter:off
    /**
     * Group A ->                    AcmGroup A ->                          User 1 -> invalidated
     *       member cn=1,cn=Users             member cn=1,cn=Users                    cn=1,cn=Users,ou=Deleted
     *       member cn=4,cn=Users             member cn=2,cn=Users          User 01 -> same DN
     * Group B ->                    AcmGroup B ->                                    cn=1,cn=Users
     *       member cn=3,cn=Users             member cn=3,cn=Users
     * Group C ->                    AcmGroup C ->
     *       member cn=4,cn=Users             member cn=4,cn=Users
     */
    // @formatter:on
    @Test
    public void changedGroupsWithAddedAndRemovedUsersAndOneNewUserWithExistingDnTest()
    {
        LdapGroup a = ldapGroup("A");
        LdapGroup b = ldapGroup("B");
        LdapGroup c = ldapGroup("C");

        AcmUser u1 = acmUser("1");
        u1.setDistinguishedName(MapperUtils.appendToDn(u1.getDistinguishedName(), AcmLdapConstants.DC_DELETED));
        AcmUser u01 = acmUser("01");
        u01.setDistinguishedName("cn=1,cn=Users");
        AcmUser u2 = acmUser("2");
        AcmUser u3 = acmUser("3");
        AcmUser u4 = acmUser("4");

        a.setMembers(fromArray("cn=1,cn=Users", "cn=4,cn=Users"));
        b.setMembers(fromArray("cn=3,cn=Users"));
        c.setMembers(fromArray("cn=4,cn=Users"));

        Map<String, AcmUser> currentUsers = userStream(u1, u01, u2, u3, u4)
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

        AcmGroup acmGroupA = acmGroup("A");
        AcmGroup acmGroupB = acmGroup("B");
        AcmGroup acmGroupC = acmGroup("C");

        acmGroupA.setUserMembers(userSet(u1, u2));
        acmGroupB.setUserMembers(userSet(u3));
        acmGroupC.setUserMembers(userSet(u4));

        List<AcmGroup> currentGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

        unit.sync(Arrays.asList(a, b, c), currentGroups, currentUsers);

        AcmGroup actualGroupA = unit.getModifiedGroups().get(0);
        assertThat(unit.getModifiedGroups().size(), is(1));
        assertThat("Changed group should be:", actualGroupA.getName(), is("A"));
        assertThat("Changed group A should have user members", actualGroupA.getUserMembers(false),
                everyItem(isIn(userSet(u4, u01))));
        assertThat("Ascendants string for C should be null", actualGroupA.getAscendantsList(), nullValue());
    }

    // @formatter:off
    /**
     * Group A ->                    AcmGroup A ->                          User 1 ->  invalidated
     *       member cn=1,cn=Users             member cn=1,cn=Users                     cn=1,cn=Users,ou=Deleted
     *                                                                      User 01 -> same DN
     *                                                                                 cn=1,cn=Users
     */
    // @formatter:on
    @Test
    public void changedGroupWithAddedAndRemovedUserSyncedWithExistingDnTest()
    {
        LdapGroup a = ldapGroup("A");

        AcmUser u1 = acmUser("1");
        u1.setDistinguishedName(MapperUtils.appendToDn(u1.getDistinguishedName(), AcmLdapConstants.DC_DELETED));
        AcmUser u01 = acmUser("01");
        u01.setDistinguishedName("cn=1,cn=Users");

        a.setMembers(fromArray("cn=1,cn=Users"));

        Map<String, AcmUser> currentUsers = userStream(u1, u01)
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

        AcmGroup acmGroupA = acmGroup("A");

        acmGroupA.setUserMembers(userSet(u1));

        unit.sync(Collections.singletonList(a), Collections.singletonList(acmGroupA), currentUsers);

        AcmGroup actualGroupA = unit.getModifiedGroups().get(0);
        assertThat(unit.getModifiedGroups().size(), is(1));
        assertThat("Changed group should be:", actualGroupA.getName(), is("A"));
        assertThat("Changed group A should have user members", actualGroupA.getUserMembers(false),
                everyItem(isIn(userSet(u01))));
        assertThat("Ascendants string for C should be null", actualGroupA.getAscendantsList(), nullValue());
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

        unit.sync(Arrays.asList(a, b, c, d), acmGroups, acmUsers);

        assertThat(unit.getNewGroups(), is(empty()));
        assertThat(unit.getDeletedGroups(), is(empty()));
        assertThat(unit.getModifiedGroups().size(), is(4));

        Map<String, AcmGroup> modifiedGroupsByName = getGroupByName(unit.getModifiedGroups());

        assertThat("Changed groups should be:", modifiedGroupsByName.keySet(),
                everyItem(isIn(fromArray("A", "B", "C", "D"))));

        assertThat("Ascendants string for group A should be", modifiedGroupsByName.get("A").getAscendantsList(),
                is("C||D"));
        assertThat("Group A should have 0 member groups", modifiedGroupsByName.get("A").getMemberGroups().size(), is(0));
        assertThat("Group A should have user members", modifiedGroupsByName.get("A")
                .getUserMemberIds().collect(Collectors.toSet()), everyItem(isIn(fromArray("1", "2"))));

        assertThat("Ascendants string for group B should be", modifiedGroupsByName.get("B").getAscendantsList(),
                nullValue());
        assertThat("Group B should have member groups", modifiedGroupsByName.get("B")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("C"))));
        assertThat("Group B should have user members", modifiedGroupsByName.get("B")
                .getUserMemberIds().collect(Collectors.toSet()), everyItem(isIn(fromArray("3"))));

        assertThat("Group C should have member groups", modifiedGroupsByName.get("C")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "D"))));
        assertThat("Group C should have 0 user members", modifiedGroupsByName.get("C").getUserMembers(false).size(), is(0));
        assertThat("Ascendants string for group C should be", modifiedGroupsByName.get("C").getAscendantsList(), nullValue());

        assertThat("Ascendants string for group D should be", modifiedGroupsByName.get("D").getAscendantsList(), is("C"));
        assertThat("Group D should have 0 user members", modifiedGroupsByName.get("D").getUserMembers(false).size(), is(0));
        assertThat("Group D should have member groups", modifiedGroupsByName.get("D")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("A"))));
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

        unit.sync(Arrays.asList(a, c, d), currentGroups, acmUsers);

        assertThat(unit.getNewGroups().get(0), is(acmGroup("D")));

        assertThat(unit.getDeletedGroups(), everyItem(isIn(groupSet(acmGroupB))));
        AcmGroup actualDeletedGroup = unit.getDeletedGroups().get(0);
        assertThat("Group B should have 0 member groups", actualDeletedGroup.getMemberGroups().size(), is(0));
        assertThat("Group B should have 0 user groups", actualDeletedGroup.getUserMembers(false).size(), is(0));
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
        assertThat("Ascendants string for group C should be", modifiedGroupsByName.get("C").getAscendantsList(), is("A||D"));
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
    *       member cn=X,cn=Groups            member cn=C,cn=Groups
    * Group C ->                        AcmGroup C ->
    *       member cn=A,cn=Groups            member
    *       member cn=D,cn=Groups
    * Group D ->
    *       member cn=A,cn=Groups
    * Group X ->
    *       member cn=D,cn=Groups
    */
    // @formatter:on
    @Test
    public void syncTwoNewGroupsOneAddedInExistingAndRemovedGroupFromGroupTest()
    {
        LdapGroup a = ldapGroup("A");
        LdapGroup b = ldapGroup("B");
        LdapGroup c = ldapGroup("C");
        LdapGroup d = ldapGroup("D");
        LdapGroup x = ldapGroup("X");

        AcmUser u1 = acmUser("1");
        AcmUser u2 = acmUser("2");
        AcmUser u3 = acmUser("3");

        a.setMembers(fromArray("cn=1,cn=Users", "cn=2,cn=Users"));
        b.setMembers(fromArray("cn=3,cn=Users", "cn=X,cn=Groups"));
        c.setMembers(fromArray("cn=A,cn=Groups", "cn=D,cn=Groups"));
        d.setMembers(fromArray("cn=A,cn=Groups"));
        x.setMembers(fromArray("cn=D,cn=Groups"));

        Map<String, AcmUser> acmUsers = userStream(u1, u2, u3)
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));

        AcmGroup acmGroupA = acmGroup("A", "", "D");
        AcmGroup acmGroupB = acmGroup("B");
        AcmGroup acmGroupC = acmGroup("C", "", "B");

        acmGroupA.setUserMembers(userStream(u1, u2).collect(Collectors.toSet()));
        acmGroupB.setUserMembers(userStream(u3).collect(Collectors.toSet()));
        acmGroupB.setMemberGroups(groupSet(acmGroupC));

        List<AcmGroup> acmGroups = Arrays.asList(acmGroupA, acmGroupB, acmGroupC);

        unit.sync(Arrays.asList(a, b, c, d, x), acmGroups, acmUsers);

        assertThat(unit.getNewGroups().size(), is(2));
        assertThat(unit.getDeletedGroups(), is(empty()));
        assertThat(unit.getModifiedGroups().size(), is(4));

        Map<String, AcmGroup> newGroupsByName = getGroupByName(unit.getNewGroups());

        assertThat("New groups should be:", newGroupsByName.keySet(),
                everyItem(isIn(fromArray("D", "X"))));

        assertThat("Ascendants string for group X should be", newGroupsByName.get("X").getAscendantsList(),
                is("B"));
        assertThat("Group X should have member groups", newGroupsByName.get("X").getGroupMemberNames()
                .collect(Collectors.toSet()), everyItem(isIn(fromArray("D"))));
        assertThat("Group X should have 0 user members", newGroupsByName.get("X").getUserMembers(false).size(), is(0));

        assertThat("Ascendants string for group D should be", newGroupsByName.get("D").getAscendantsList(), is("B||C||X"));
        assertThat("Group D should have 0 user members", newGroupsByName.get("D").getUserMembers(false).size(), is(0));
        assertThat("Group D should have member groups", newGroupsByName.get("D")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("A"))));

        Map<String, AcmGroup> modifiedGroupsByName = getGroupByName(unit.getModifiedGroups());

        assertThat("Changed groups should be:", modifiedGroupsByName.keySet(),
                everyItem(isIn(fromArray("A", "B", "C", "D"))));

        assertThat("Ascendants string for group A should be", modifiedGroupsByName.get("A").getAscendantsList(),
                is("B||C||D||X"));
        assertThat("Group A should have 0 member groups", modifiedGroupsByName.get("A").getMemberGroups().size(), is(0));
        assertThat("Group A should have user members", modifiedGroupsByName.get("A")
                .getUserMemberIds().collect(Collectors.toSet()), everyItem(isIn(fromArray("1", "2"))));

        assertThat("Ascendants string for group B should be", modifiedGroupsByName.get("B").getAscendantsList(),
                nullValue());
        assertThat("Group B should have member groups", modifiedGroupsByName.get("B")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("C", "X"))));
        assertThat("Group B should have user members", modifiedGroupsByName.get("B")
                .getUserMemberIds().collect(Collectors.toSet()), everyItem(isIn(fromArray("3"))));

        assertThat("Group C should have member groups", modifiedGroupsByName.get("C")
                .getGroupMemberNames().collect(Collectors.toSet()), everyItem(isIn(fromArray("A", "D"))));
        assertThat("Group C should have 0 user members", modifiedGroupsByName.get("C").getUserMembers(false).size(), is(0));
        assertThat("Ascendants string for group C should be", modifiedGroupsByName.get("C").getAscendantsList(), nullValue());
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

    private Map<String, AcmGroup> getGroupByName(List<AcmGroup> groups)
    {
        return groups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }
}
