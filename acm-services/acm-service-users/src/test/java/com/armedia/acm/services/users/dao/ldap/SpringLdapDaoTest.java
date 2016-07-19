package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SpringLdapDaoTest extends EasyMockSupport
{
    // TODO: will rework

    AcmLdapSyncConfig mockAcmLdapSyncConfig;
    LdapTemplate mockLdapTemplate;
    LdapGroup mockLdapGroup;
    SpringLdapDao springLdapDao;


    @Before
    public void setUp()
    {
        mockAcmLdapSyncConfig = createMock(AcmLdapSyncConfig.class);
        mockLdapTemplate = createMock(LdapTemplate.class);
        mockLdapGroup = createMock(LdapGroup.class);

        springLdapDao = new SpringLdapDao();
    }

}