package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.plugins.profile.model.OutlookDTO;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-profile-plugin-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class UserOrgDaoIT extends EasyMockSupport {

    @Autowired
    UserOrgDao userOrgDao;
    private Authentication authentication;

    @Before
    public void setUp() {
        authentication = createMock(Authentication.class);
    }

    @Test
    @Transactional
    public void testSaveAndRetriveOutlookPassword() throws Exception {

        expect(authentication.getName()).andReturn("ann-acm").times(2);
        expect(authentication.getCredentials()).andReturn("AcMd3v$").times(2);
        replayAll();

        OutlookDTO in = new OutlookDTO();
        in.setOutlookPassword("Armedia123");
        userOrgDao.saveOutlookPassword(authentication, in);

        OutlookDTO outlookDTO = userOrgDao.retrieveOutlookPassword(authentication);
        assertNotNull(outlookDTO);
        assertEquals("Armedia123", outlookDTO.getOutlookPassword());

        verifyAll();
    }
}