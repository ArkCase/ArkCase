package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class SpringLdapDaoTest extends EasyMockSupport
{

    AcmLdapEntity mockEntity;
    AcmLdapEntityContextMapper mockMapper;
    AcmLdapSyncConfig mockAcmLdapSyncConfig;
    LdapTemplate mockLdapTemplate;
    LdapGroup mockLdapGroup;
    SpringLdapDao springLdapDao;


    @Before
    public void setUp()
    {
        mockEntity = createMock(AcmLdapEntity.class);
        mockMapper = createMock(AcmLdapEntityContextMapper.class);
        mockAcmLdapSyncConfig = createMock(AcmLdapSyncConfig.class);
        mockLdapTemplate = createMock(LdapTemplate.class);
        mockLdapGroup = createMock(LdapGroup.class);

        springLdapDao = new SpringLdapDao();
        springLdapDao.setMapper(mockMapper);
    }

    @Test
    public void findExistingGroupMembers()
    {
        String[] memberDns = new String[]{"VALID NAME"};

        testGroupMemberExpectations(memberDns);

        expect(mockLdapTemplate.lookup(memberDns[0], mockMapper)).andReturn(mockEntity);
        mockEntity.setDistinguishedName(memberDns[0]);
        expectLastCall();

        replayAll();

        List<AcmLdapEntity> actual = springLdapDao.findGroupMembers(mockLdapTemplate, mockAcmLdapSyncConfig, mockLdapGroup);

        verifyAll();
        assertThat("List should not be null", actual, is(notNullValue()));
        assertThat("List should have 0 or more elements", actual.size(), is(equalTo(1)));

    }

    @Test
    public void findNotExistingGroupMembers()
    {
        String[] memberDns = new String[]{"Not existing"};

        testGroupMemberExpectations(memberDns);

        expect(mockLdapTemplate.lookup(memberDns[0], mockMapper)).andReturn(null);

        replayAll();
        List<AcmLdapEntity> actual = springLdapDao.findGroupMembers(mockLdapTemplate, mockAcmLdapSyncConfig, mockLdapGroup);

        verifyAll();
        assertThat("List should not be null", actual, is(notNullValue()));
        assertThat("List should have 0 or more elements", actual.size(), is(equalTo(0)));

    }

    @Test
    public void findGroupMembersWithInvalidName() throws Exception
    {
        String[] memberDns = new String[]{"NAME WITH FORWARD SLASH /"};

        testGroupMemberExpectations(memberDns);

        expect(mockLdapTemplate.lookup(memberDns[0], mockMapper))
                .andThrow(new UncategorizedLdapException("Uncategorized exception occurred during LDAP processing"));

        replayAll();

        List<AcmLdapEntity> actual = springLdapDao.findGroupMembers(mockLdapTemplate, mockAcmLdapSyncConfig, mockLdapGroup);

        verifyAll();
        assertThat("List should not be null", actual, is(notNullValue()));
        assertThat("List should have 0 or more elements", actual.size(), is(greaterThanOrEqualTo(0)));
    }

    void testGroupMemberExpectations(String [] memberDns)
    {
        expect(mockLdapGroup.getMemberDistinguishedNames()).andReturn(memberDns);
        expect(mockAcmLdapSyncConfig.getUserIdAttributeName()).andReturn("userIdAttributeName");
        expect(mockAcmLdapSyncConfig.getMailAttributeName()).andReturn("mailAttributeName");

        mockMapper.setUserIdAttributeName("userIdAttributeName");
        expectLastCall();

        mockMapper.setMailAttributeName("mailAttributeName");
        expectLastCall();
    }
}