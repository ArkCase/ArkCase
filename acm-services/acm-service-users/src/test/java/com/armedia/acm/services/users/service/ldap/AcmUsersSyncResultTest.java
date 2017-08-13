package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.LdapUser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AcmUsersSyncResultTest
{
    private AcmUsersSyncResult unit;

    @Before
    public void setup()
    {
        unit = new AcmUsersSyncResult();
    }

    @Test
    public void syncTest()
    {
        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setUserState(AcmUserState.VALID);
        List<AcmUser> currentUsers = Arrays.asList(user1);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, currentUsers);

        assertThat("There should be no changed users", unit.getChangedUsers().size(), is(0));
        assertThat("There should be 1 new user", unit.getNewUsers().size(), is(1));
        assertThat(unit.getNewUsers().get(0).getUserId(), is("2"));

        assertThat(actual.size(), is(2));
        assertThat(actual.get("1"), is(user1));
        assertThat(actual.get("2").getUserId(), is("2"));
    }

    @Test
    public void syncNoNewUsersTest()
    {
        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setUserState(AcmUserState.VALID);
        AcmUser user2 = new AcmUser();
        user2.setUserId("2");
        user2.setUserState(AcmUserState.VALID);
        List<AcmUser> currentUsers = Arrays.asList(user1, user2);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, currentUsers);

        assertThat("There should be no changed users", unit.getChangedUsers().size(), is(0));
        assertThat("There should be no new users", unit.getNewUsers().size(), is(0));

        assertThat(actual.size(), is(2));
        assertThat(actual.get("1"), is(user1));
        assertThat(actual.get("2"), is(user2));
    }

    @Test
    public void syncWithAllNewUsersTest()
    {
        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        List<AcmUser> currentUsers = new ArrayList<>();

        Map<String, AcmUser> actual = unit.sync(ldapUsers, currentUsers);

        assertThat("There should be no changed users", unit.getChangedUsers().size(), is(0));
        assertThat("There should be 2 new users", unit.getNewUsers().size(), is(2));

        assertThat(actual.size(), is(2));
        assertThat(actual.get("1").getUserId(), is("1"));
        assertThat(actual.get("2").getUserId(), is("2"));
    }

    @Test
    public void syncWithChangedUsersTest()
    {
        LdapUser ldapUser1 = new LdapUser();
        ldapUser1.setUserId("1");
        ldapUser1.setState("VALID");
        ldapUser1.setMail("email_changed");
        LdapUser ldapUser2 = new LdapUser();
        ldapUser2.setUserId("2");
        ldapUser2.setState("VALID");
        List<LdapUser> ldapUsers = Arrays.asList(ldapUser1, ldapUser2);

        AcmUser user1 = new AcmUser();
        user1.setUserId("1");
        user1.setMail("email");
        user1.setUserState(AcmUserState.VALID);
        AcmUser user2 = new AcmUser();
        user2.setUserId("2");
        user2.setUserState(AcmUserState.VALID);
        List<AcmUser> currentUsers = Arrays.asList(user1, user2);

        Map<String, AcmUser> actual = unit.sync(ldapUsers, currentUsers);

        assertThat("There should be 1 changed user", unit.getChangedUsers().size(), is(1));
        assertThat("There should be no new users", unit.getNewUsers().size(), is(0));

        assertThat(unit.getChangedUsers().get(0), is(user1));

        assertThat(actual.size(), is(2));
        assertThat(actual.get("1"), is(user1));
        assertThat(actual.get("2"), is(user2));
    }

}
