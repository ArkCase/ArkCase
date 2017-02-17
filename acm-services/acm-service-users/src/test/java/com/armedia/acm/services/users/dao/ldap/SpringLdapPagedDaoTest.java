package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SpringLdapPagedDaoTest extends EasyMockSupport
{
    // TODO: will rework

    private LdapTemplate mockLdapTemplate;
    private SpringLdapPagedDao.PagedResultsDirContextProcessorBuilder mockBuilder;
    private PagedResultsDirContextProcessor mockPagedResultsDirContextProcessor;
    private PagedResultsCookie mockPagedResultsCookie;

    private AcmLdapSyncConfig syncConfig;

    private SpringLdapPagedDao unit;


    @Before
    public void setUp()
    {
        mockLdapTemplate = createMock(LdapTemplate.class);
        mockBuilder = createMock(SpringLdapPagedDao.PagedResultsDirContextProcessorBuilder.class);
        mockPagedResultsDirContextProcessor = createMock(PagedResultsDirContextProcessor.class);
        mockPagedResultsCookie = createMock(PagedResultsCookie.class);

        unit = new SpringLdapPagedDao();
        unit.setBuilder(mockBuilder);

        syncConfig = new AcmLdapSyncConfig();
        syncConfig.setUserIdAttributeName("samAccountName");
        syncConfig.setMailAttributeName("mail");
        syncConfig.setBaseDC("dc=dead,dc=net");
        syncConfig.setUserSearchBase("CN=bandMembers");
        syncConfig.setAllUsersFilter("allUsersFilter");
        syncConfig.setSyncPageSize(100);
    }

    @Test
    public void findUsersPaged_userDomainAppendedIfPresent() throws Exception
    {
        // since we set this property, all the user ids should end with it
        syncConfig.setUserDomain("dead.net");

        expect(mockBuilder.build(syncConfig.getSyncPageSize(), null)).andReturn(mockPagedResultsDirContextProcessor);
        expect(mockPagedResultsDirContextProcessor.getCookie()).andReturn(mockPagedResultsCookie);
        expect(mockPagedResultsCookie.getCookie()).andReturn(null);

        ArrayList<AcmUser> acmUsers = new ArrayList<>();

        AcmUser jgarcia = new AcmUser();
        jgarcia.setUserId("jgarcia");
        acmUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
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

        expect(mockBuilder.build(syncConfig.getSyncPageSize(), null)).andReturn(mockPagedResultsDirContextProcessor);
        expect(mockPagedResultsDirContextProcessor.getCookie()).andReturn(mockPagedResultsCookie);
        expect(mockPagedResultsCookie.getCookie()).andReturn(null);

        ArrayList<AcmUser> acmUsers = new ArrayList<>();

        AcmUser jgarcia = new AcmUser();
        jgarcia.setUserId("jgarcia");
        acmUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
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