package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpringLdapPagedDaoTest extends EasyMockSupport
{

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

        ArrayList<LdapUser> acmUsers = new ArrayList<>();

        LdapUser jgarcia = new LdapUser();
        jgarcia.setUserId("jgarcia");
        acmUsers.add(jgarcia);

        expect(mockLdapTemplate.search(
                eq(syncConfig.getUserSearchBase()),
                eq(syncConfig.getAllUsersFilter()),
                anyObject(SearchControls.class),
                anyObject(AcmUserContextMapper.class),
                anyObject(PagedResultsDirContextProcessor.class))).andReturn(acmUsers);

        replayAll();

        List<LdapUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig, null);

        verifyAll();

        assertEquals(acmUsers.size(), found.size());

        for (LdapUser user : found)
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

        List<LdapUser> found = unit.findUsersPaged(mockLdapTemplate, syncConfig, null);

        verifyAll();

        assertEquals(ldapUsers.size(), found.size());

        for (LdapUser user : found)
        {
            assertFalse(user.getUserId().endsWith("@" + syncConfig.getUserDomain()));
        }

    }

}