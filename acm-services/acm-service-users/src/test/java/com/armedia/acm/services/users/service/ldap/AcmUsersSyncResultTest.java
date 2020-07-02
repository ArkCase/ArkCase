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
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AcmUsersSyncResultTest
{
    private static final String LANG = "en";
    private AcmUsersSyncResult unit;

    static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

    // @formatter:off
    /**
     * ldap state             db state
     *
     * LdapUser 1 ->         AcmUser 1 ->
     *       userId 1              userId 1
     *       state VALID           state VALID
     *       dn cn=1               dn cn=1
     * LdapUser 2 ->         AcmUser 5 ->
     *       userId 2              userId 5
     *       state VALID           state VALID
     *       dn cn=2               dn cn=5
     */
    // @formatter:on
    @Test
    public void fullSyncOneNewUserOneDeletedUserAndNoOtherChangedTest()
    {
        unit = new AcmUsersSyncResult(true);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setDistinguishedName("cn=1");
        ldapUser1.setState("VALID");

        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        ldapUser2.setDistinguishedName("cn=2");

        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setUserState(AcmUserState.VALID);
        user1.setDistinguishedName("cn=1");

        AcmUser user5 = new AcmUser();
        user5.setUserId("5");
        user5.setUserState(AcmUserState.VALID);
        user5.setDistinguishedName("cn=5");

        List<AcmUser> acmUsers = Arrays.asList(user1, user5);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, acmUsers, LANG);

        assertThat("There should be no changed users", unit.getModifiedUsers().size(), is(0));
        assertThat("There should be 1 new user", unit.getNewUsers().size(), is(1));
        assertThat("There should be 1 deleted user", unit.getDeletedUsers().size(), is(1));
        assertThat(unit.getNewUsers().get(0).getUserId(), is("2"));
        assertThat(unit.getDeletedUsers().get(0).getUserId(), is("5"));
        assertThat(unit.getDeletedUsers().get(0).getUserState(), is(AcmUserState.INVALID));
        assertThat(unit.getDeletedUsers().get(0).getDistinguishedName(), endsWith("=Deleted"));

        assertThat(actual.size(), is(3));
        assertThat("Keys should be synced user's ids", actual.keySet(),
                everyItem(isIn(fromArray("1", "2", "5"))));
    }

    // @formatter:off
    /**
     * ldap state             db state
     *
     * LdapUser 1 ->         AcmUser 1 ->
     *       userId 1              userId 1
     *       state VALID           state VALID
     *       dn cn=5               dn cn=1
     * LdapUser 2 ->         AcmUser 5 ->
     *       userId 2              userId 5
     *       state VALID           state VALID
     *       dn cn=2               dn cn=5
     */
    // @formatter:on
    @Test
    public void fullSyncOneNewUserOneChangedWithSameExistingDnAndOneDeletedUserTest()
    {
        unit = new AcmUsersSyncResult(true);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setDistinguishedName("cn=5");
        ldapUser1.setState("VALID");

        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        ldapUser2.setDistinguishedName("cn=2");

        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setUserState(AcmUserState.VALID);
        user1.setDistinguishedName("cn=1");

        AcmUser user5 = new AcmUser();
        user5.setUserId("5");
        user5.setUserState(AcmUserState.VALID);
        user5.setDistinguishedName("cn=5");

        List<AcmUser> acmUsers = Arrays.asList(user1, user5);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, acmUsers, LANG);

        assertThat("There should be 1 changed user", unit.getModifiedUsers().size(), is(1));
        assertThat("There should be 1 new user", unit.getNewUsers().size(), is(1));
        assertThat("There should be 1 deleted user", unit.getDeletedUsers().size(), is(1));
        assertThat(unit.getNewUsers().get(0).getUserId(), is("2"));
        assertThat(unit.getDeletedUsers().get(0).getUserId(), is("5"));
        assertThat(unit.getDeletedUsers().get(0).getUserState(), is(AcmUserState.INVALID));
        assertThat(unit.getDeletedUsers().get(0).getDistinguishedName(), endsWith("=Deleted"));
        assertThat(unit.getModifiedUsers().get(0).getUserId(), is("1"));
        assertThat(unit.getModifiedUsers().get(0).getDistinguishedName(), is("cn=5"));

        assertThat(actual.size(), is(3));
        assertThat("Keys should be synced user's ids", actual.keySet(),
                everyItem(isIn(fromArray("1", "2", "5"))));
    }

    // @formatter:off

    /**
     * ldap state             db state
     *
     * LdapUser 1 ->         AcmUser 1 ->
     *       userId 1              userId 1
     *       state VALID           state VALID
     * LdapUser 2 ->         AcmUser 2 ->
     *       userId 2              userId 2
     *       state VALID           state VALID
     */
    // @formatter:on
    @Test
    public void fullSyncNoChangesTest()
    {
        unit = new AcmUsersSyncResult(true);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        ldapUser1.setDistinguishedName("cn=1");

        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        ldapUser2.setDistinguishedName("cn=2");

        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setDistinguishedName("cn=1");
        user1.setUserState(AcmUserState.VALID);

        AcmUser user2 = new AcmUser();
        user2.setUserId("2");
        user2.setDistinguishedName("cn=2");
        user2.setUserState(AcmUserState.VALID);

        List<AcmUser> currentUsers = Arrays.asList(user1, user2);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, currentUsers, LANG);

        assertThat("There should be no changed users", unit.getModifiedUsers().size(), is(0));
        assertThat("There should be no new users", unit.getNewUsers().size(), is(0));
        assertThat("There should be no deleted users", unit.getDeletedUsers().size(), is(0));

        assertThat(actual.size(), is(2));
        assertThat(actual.get("1"), is(user1));
        assertThat(actual.get("2"), is(user2));
    }

    // @formatter:off

    /**
     * ldap state             db state
     *
     * LdapUser 1 ->               AcmUser 1 ->
     *       userId 1                   userId 1
     *       state VALID                state VALID
     *       email user1@arkcase        email user1@armedia
     *       dn cn=1                    dn cn=1
     * LdapUser 2 ->               AcmUser 2 ->
     *       userId 2                   userId 2
     *       state VALID                state VALID
     *       email user2@arkcase        email user2@armedia
     *       dn cn=2                    dn cn=2
     * LdapUser 3 ->               AcmUser 3 ->
     *       userId 3                   userId 3
     *       state VALID                state VALID
     *       email user3@armedia        email user3@armedia
     *       dn cn=3                    dn cn=3
     */
    // @formatter:on
    @Test
    public void fullSyncOnlyChangedUsersTest()
    {
        unit = new AcmUsersSyncResult(true);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        ldapUser1.setMail("user1@arkcase");
        ldapUser1.setDistinguishedName("cn=1");

        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        ldapUser2.setMail("user2@arkcase");
        ldapUser2.setDistinguishedName("cn=2");

        LdapUser ldapUser3 = new LdapUser();
        ldapUser3.setUserId("3");
        ldapUser3.setState("VALID");
        ldapUser3.setMail("user3@armedia");
        ldapUser3.setDistinguishedName("cn=3");

        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2, ldapUser3);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setMail("user1@armedia");
        user1.setUserState(AcmUserState.VALID);
        user1.setDistinguishedName("cn=1");

        AcmUser user2 = new AcmUser();
        user2.setUserId("2");
        user2.setMail("user2@armedia");
        user2.setUserState(AcmUserState.VALID);
        user2.setDistinguishedName("cn=2");

        AcmUser user3 = new AcmUser();
        user3.setUserId("3");
        user3.setMail("user3@armedia");
        user3.setUserState(AcmUserState.VALID);
        user3.setDistinguishedName("cn=3");

        List<AcmUser> acmUsers = Arrays.asList(user1, user2, user3);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, acmUsers, LANG);

        assertThat("There should be 2 changed users", unit.getModifiedUsers().size(), is(2));
        assertThat("There should be no new users", unit.getNewUsers().size(), is(0));
        assertThat("There should be no deleted users", unit.getDeletedUsers().size(), is(0));

        assertThat(unit.getModifiedUsers(), everyItem(isIn(Arrays.asList(user1, user2))));

        assertThat(actual.size(), is(3));
        user1.setMail("user1@arkcase");
        assertThat(actual.get("1"), is(user1));
        user2.setMail("user2@arkcase");
        assertThat(actual.get("2"), is(user2));
        assertThat(actual.get("3"), is(user3));
    }

    // @formatter:off
    /**
     * ldap state             db state
     *
     * LdapUser 1 ->
     *       userId 1
     *       state VALID
     *                         AcmUser 2 ->
     *                              userId 2
     *                              state VALID
     * LdapUser 3 ->           AcmUser 3 ->
     *       userId 3               userId 3
     *       state VALID            state VALID
     *       email user3@arkcase    email user3@armedia
     */
    // @formatter:on
    @Test
    public void partialSyncOneNewUserOneModifiedAndOneDeletedButNotDetectedTest()
    {
        unit = new AcmUsersSyncResult(false);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        ldapUser1.setDistinguishedName("cn=1");
        LdapUser ldapUser3 = new LdapUser();
        ldapUser3.setUserId("3");
        ldapUser3.setState("VALID");
        ldapUser3.setDistinguishedName("cn=3");
        ldapUser3.setMail("user3@arkcase");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser3);

        AcmUser acmUser2 = new AcmUser();
        acmUser2.setUserId("2");
        acmUser2.setUserState(AcmUserState.VALID);
        acmUser2.setDistinguishedName("cn=2");
        AcmUser acmUser3 = new AcmUser();
        acmUser3.setUserId("3");
        acmUser3.setUserState(AcmUserState.VALID);
        acmUser3.setDistinguishedName("cn=3");
        acmUser3.setMail("user3@armedia");

        List<AcmUser> acmUsers = Arrays.asList(acmUser2, acmUser3);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, acmUsers, LANG);

        assertThat("There should be 1 changed users", unit.getModifiedUsers().size(), is(1));
        assertThat("There should be 1 new user", unit.getNewUsers().size(), is(1));
        assertThat("Should not detect deleted users", unit.getDeletedUsers().size(), is(0));

        assertThat(actual.size(), is(3));
        assertThat(actual.get("1").getUserId(), is("1"));
        assertThat(actual.get("1").getUserState(), is(AcmUserState.VALID));
        assertThat(actual.get("2"), is(acmUser2));
        assertThat(actual.get("3").getUserId(), is("3"));
        assertThat(actual.get("3").getUserState(), is(AcmUserState.VALID));
        assertThat(actual.get("3").getMail(), is("user3@arkcase"));
    }

    // @formatter:off
    /**
     * ldap state             db state
     *
     * LdapUser 1 ->
     *       userId 1
     *       state VALID
     *       dn cn=2
     *                         AcmUser 2 ->
     *                              userId 2
     *                              state VALID
     *                              dn cn=2
     * LdapUser 3 ->           AcmUser 3 ->
     *       userId 3               userId 3
     *       state VALID            state VALID
     *       dn cn=3                dn cn=3
     *       email user3@arkcase    email user3@arkcase
     */
    // @formatter:on
    @Test
    public void partialSyncOneNewUserWithSameDnWithExistingUserAndOneDeletedNotDetectedTest()
    {
        unit = new AcmUsersSyncResult(false);

        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        ldapUser1.setDistinguishedName("cn=2");
        LdapUser ldapUser3 = new LdapUser();
        ldapUser3.setUserId("3");
        ldapUser3.setState("VALID");
        ldapUser3.setDistinguishedName("cn=3");
        ldapUser3.setMail("user3@arkcase");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser3);

        AcmUser acmUser2 = new AcmUser();
        acmUser2.setUserId("2");
        acmUser2.setUserState(AcmUserState.VALID);
        acmUser2.setDistinguishedName("cn=2");
        AcmUser acmUser3 = new AcmUser();
        acmUser3.setUserId("3");
        acmUser3.setUserState(AcmUserState.VALID);
        acmUser3.setDistinguishedName("cn=3");
        acmUser3.setMail("user3@arkcase");

        List<AcmUser> acmUsers = Arrays.asList(acmUser2, acmUser3);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, acmUsers, LANG);

        assertThat("There should be 0 changed users", unit.getModifiedUsers().size(), is(0));
        assertThat("There should be 1 new user", unit.getNewUsers().size(), is(1));

        assertThat("Since there is a new user with same dn, old user should be deleted.", unit.getDeletedUsers().size(), is(1));
        AcmUser deletedUser = unit.getDeletedUsers().get(0);
        assertThat(deletedUser.getDistinguishedName(), endsWith("=Deleted"));
        assertThat(deletedUser.getUserState(), is(AcmUserState.INVALID));

        assertThat(actual.size(), is(3));
        assertThat(actual.get("1").getUserId(), is("1"));
        assertThat(actual.get("1").getUserState(), is(AcmUserState.VALID));
        assertThat(actual.get("1").getDistinguishedName(), is("cn=2"));
        assertThat(actual.get("3"), is(acmUser3));
    }
}
