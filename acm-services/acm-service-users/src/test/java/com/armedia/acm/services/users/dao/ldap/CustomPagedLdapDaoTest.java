package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomPagedLdapDaoTest extends EasyMockSupport
{
    private LdapTemplate mockLdapTemplate;

    private AcmLdapSyncConfig syncConfig;

    private CustomPagedLdapDao unit;

    @Before
    public void setUp()
    {
        mockLdapTemplate = createMock(LdapTemplate.class);

        unit = new CustomPagedLdapDao();

        syncConfig = new AcmLdapSyncConfig();
        syncConfig.setUserIdAttributeName("samAccountName");
        syncConfig.setMailAttributeName("mail");
        syncConfig.setBaseDC("dc=dead,dc=net");
        syncConfig.setUserSearchBase("CN=bandMembers");
        syncConfig.setAllUsersFilter("allUsersFilter");
        syncConfig.setAllUsersPageFilter("allUsersPageFilter %s");
        syncConfig.setGroupSearchFilter("groupSearchFilter");
        syncConfig.setGroupSearchPageFilter("groupSearchPageFilter %s");
        syncConfig.setGroupSearchBase("CN=bands");
        syncConfig.setSyncPageSize(100);
    }

    /**
     * AFDP-3185 test case.  "LDAP SyncFails on Index out of Bounds"
     * @throws Exception
     */
    @Test
    public void findGroupsPaged_lastGroupFallsOnPageBoundary() throws Exception
    {
        List<LdapGroup> pageOne = new ArrayList<>();

        pageOne.add(buildGroup("grateful dead"));
        pageOne.add(buildGroup("allman brothers"));
        pageOne.add(buildGroup("eagles"));

        // second page starts with the last group from the first page, due the custom paging logic... since we have to
        // start from the last previously found user, to guarantee not to miss anybody
        List<LdapGroup> pageTwo = new ArrayList<>();
        pageTwo.add(buildGroup("eagles"));
        pageTwo.add(buildGroup("fleetwood mac"));
        pageTwo.add(buildGroup("cream"));

        List<LdapGroup> pageThree = new ArrayList<>();
        pageThree.add(buildGroup("cream"));

        syncConfig.setSyncPageSize(3);

        // first search returns a full page
        expect(mockLdapTemplate.search(
                eq(syncConfig.getGroupSearchBase()),
                eq(syncConfig.getGroupSearchFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmGroupContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(pageOne);

        // second search also returns a full page, but also returns the very last user
        expect(mockLdapTemplate.search(
                eq(syncConfig.getGroupSearchBase()),
                eq(String.format(syncConfig.getGroupSearchPageFilter(), pageOne.get(pageOne.size() - 1).getName())),
                anyObject(SearchControls.class),
                anyObject(AcmGroupContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(pageTwo);

        // last search returns an empty list... actually it shouldn't happen, it should always return at least the last
        // entry from the previous search, but let's be defensive.
        expect(mockLdapTemplate.search(
                eq(syncConfig.getGroupSearchBase()),
                eq(String.format(syncConfig.getGroupSearchPageFilter(), pageTwo.get(pageTwo.size() - 1).getName())),
                anyObject(SearchControls.class),
                anyObject(AcmGroupContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(new ArrayList<>());

        replayAll();

        List<LdapGroup> found = unit.findGroupsPaged(mockLdapTemplate, syncConfig, Optional.ofNullable(null));

        verifyAll();

        assertEquals(pageOne.size() + pageTwo.size() - 1, found.size());

        assertTrue(found.stream().anyMatch(u -> u.getName().equals("grateful dead")));
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("allman brothers")));
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("eagles")));
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("fleetwood mac")));
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("cream")));
    }

    /**
     * AFDP-3185 test case.  "LDAP SyncFails on Index out of Bounds"
     * @throws Exception
     */
    @Test
    public void findUsersPaged_lastUserFallsOnPageBoundary() throws Exception
    {
        List<LdapUser> pageOne = new ArrayList<>();

        pageOne.add(buildUser("jgarcia"));
        pageOne.add(buildUser("bweir"));
        pageOne.add(buildUser("plesh"));

        // second page starts with the last user from the first page, due the custom paging logic... since we have to
        // start from the last previously found user, to guarantee not to miss anybody
        List<LdapUser> pageTwo = new ArrayList<>();
        pageTwo.add(buildUser("plesh"));
        pageTwo.add(buildUser("bkreutzmann"));
        pageTwo.add(buildUser("rmckernan"));

        syncConfig.setSyncPageSize(3);

        // first search returns a full page
        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(pageOne);

        // second search also returns a full page, but also returns the very last user
        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(String.format(syncConfig.getAllUsersPageFilter(), pageOne.get(pageOne.size() - 1).getUserId())),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(pageTwo);

        // last search returns an empty list.. actually it shouldn't happen, it should always return at least the last
        // entry from the previous search, but let's be defensive.
        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(String.format(syncConfig.getAllUsersPageFilter(), pageTwo.get(pageTwo.size() - 1).getUserId())),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(AggregateDirContextProcessor.class))).andReturn(new ArrayList<>());

        replayAll();

        List<LdapUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig, Optional.ofNullable(null));

        verifyAll();

        assertEquals(pageOne.size() + pageTwo.size() - 1, found.size());

        assertTrue(found.stream().anyMatch(u -> u.getUserId().equals("jgarcia")));
        assertTrue(found.stream().anyMatch(u -> u.getUserId().equals("bweir")));
        assertTrue(found.stream().anyMatch(u -> u.getUserId().equals("plesh")));
        assertTrue(found.stream().anyMatch(u -> u.getUserId().equals("bkreutzmann")));
        assertTrue(found.stream().anyMatch(u -> u.getUserId().equals("rmckernan")));
    }

    @Test
    public void findUsersPaged_userDomainAppendedIfPresent() throws Exception
    {
        // since we set this property, all the user ids should end with it
        syncConfig.setUserDomain("dead.net");

        ArrayList<LdapUser> ldapUsers = new ArrayList<>();

        LdapUser jgarcia = new LdapUser();
        jgarcia.setUserId("jgarcia");
        ldapUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(PagedResultsDirContextProcessor.class))).andReturn(ldapUsers);

        replayAll();

        List<LdapUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig, Optional.ofNullable(null));

        verifyAll();

        assertEquals(ldapUsers.size(), found.size());

        for (LdapUser user : found)
        {
            assertTrue(user.getUserId().endsWith("@" + syncConfig.getUserDomain()));
        }

    }

    @Test
    public void findUsersPaged_userDomainNotAppendedIfAbsent() throws Exception
    {
        syncConfig.setUserDomain(null);

        ArrayList<LdapUser> ldapUsers = new ArrayList<>();

        LdapUser jgarcia = new LdapUser();
        jgarcia.setUserId("jgarcia");
        ldapUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(PagedResultsDirContextProcessor.class))).andReturn(ldapUsers);

        replayAll();

        List<LdapUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig, Optional.ofNullable(null));

        verifyAll();

        assertEquals(ldapUsers.size(), found.size());

        for (LdapUser user : found)
        {
            assertFalse(user.getUserId().endsWith("@" + syncConfig.getUserDomain()));
        }
    }

    private LdapUser buildUser(String userid)
    {
        LdapUser user = new LdapUser();
        user.setUserId(userid);
        user.setDistinguishedName("dn: " + userid);
        user.setSortableValue(userid);
        return user;
    }

    private LdapGroup buildGroup(String groupName)
    {
        LdapGroup group = new LdapGroup();
        group.setName(groupName);
        group.setSortableValue(groupName);
        return group;
    }
}
