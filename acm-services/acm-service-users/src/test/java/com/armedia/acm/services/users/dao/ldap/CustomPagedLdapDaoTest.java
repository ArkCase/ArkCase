package com.armedia.acm.services.users.dao.ldap;


import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
        syncConfig.setAllUsersSearchBase("CN=bandMembers");
        syncConfig.setAllUsersFilter("allUsersFilter");
        syncConfig.setSyncPageSize(100);
    }

    @Test
    public void findUsersPaged_userDomainAppendedIfPresent() throws Exception
    {
        // since we set this property, all the user ids should end with it
        syncConfig.setUserDomain("dead.net");

        ArrayList<AcmUser> acmUsers = new ArrayList<>();

        AcmUser jgarcia = new AcmUser();
        jgarcia.setUserId("jgarcia");
        acmUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getAllUsersSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserGroupsContextMapper.class),
                anyObject(PagedResultsDirContextProcessor.class))).andReturn(acmUsers);

        replayAll();

        List<AcmUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig);

        verifyAll();

        assertEquals(acmUsers.size(), found.size());

        for (AcmUser user : found)
        {
            assertTrue(user.getUserId().endsWith("@" + syncConfig.getUserDomain()));
        }

    }


    @Test
    public void findUsersPaged_userDomainNotAppendedIfAbsent() throws Exception
    {
        syncConfig.setUserDomain(null);

        ArrayList<AcmUser> acmUsers = new ArrayList<>();

        AcmUser jgarcia = new AcmUser();
        jgarcia.setUserId("jgarcia");
        acmUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getAllUsersSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserGroupsContextMapper.class),
                anyObject(PagedResultsDirContextProcessor.class))).andReturn(acmUsers);

        replayAll();

        List<AcmUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig);

        verifyAll();

        assertEquals(acmUsers.size(), found.size());

        for (AcmUser user : found)
        {
            assertFalse(user.getUserId().endsWith("@" + syncConfig.getUserDomain()));
        }
    }
}
