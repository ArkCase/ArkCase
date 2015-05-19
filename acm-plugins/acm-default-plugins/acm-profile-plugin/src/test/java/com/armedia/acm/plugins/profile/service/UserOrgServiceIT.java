package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-profile-plugin-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class UserOrgServiceIT extends EasyMockSupport {

    @Autowired
    UserOrgService userOrgService;

    @Autowired
    UserOrgDao userOrgDao;

    private Authentication authentication;

    @PersistenceContext
    private EntityManager em;

    private String userid;

    private String findFirstUserJpql = "SELECT o.user.userId FROM UserOrg o";

    @Before
    public void setUp() {
        authentication = createMock(Authentication.class);

        Query findFirstUserQuery = em.createQuery(findFirstUserJpql);
        findFirstUserQuery.setFirstResult(0);
        findFirstUserQuery.setMaxResults(1);

        List<String> users = findFirstUserQuery.getResultList();
        if ( users != null && !users.isEmpty())
        {
            userid = users.get(0);
        }
    }

    @Test
    @Transactional
    public void testSaveAndRetriveOutlookPassword() throws Exception {

        // if no userid, then there are no user profiles in the system, and we can't test the encryption.
        // we won't actually create a new user profile in this test.
        if ( userid == null )
        {
            return;
        }

        expect(authentication.getName()).andReturn(userid).times(3);
        expect(authentication.getCredentials()).andReturn("AcMd3v$").times(2);
        replayAll();

        OutlookDTO in = new OutlookDTO();
        in.setOutlookPassword("Armedia123");
        userOrgService.saveOutlookPassword(authentication, in);
        
        OutlookDTO outlookDTO = userOrgService.retrieveOutlookPassword(authentication);
        assertNotNull(outlookDTO);
        assertEquals("Armedia123", outlookDTO.getOutlookPassword());

        //get password from the dao which is encrypted and compare with decrypted
        OutlookDTO fromDaoOutlookDTO = userOrgDao.retrieveOutlookPassword(authentication);
        assertNotNull(fromDaoOutlookDTO.getOutlookPassword());
        assertNotEquals(fromDaoOutlookDTO.getOutlookPassword(), outlookDTO.getOutlookPassword());

        verifyAll();
    }

    @Test(expected = AcmEncryptionBadKeyOrDataException.class)
    @Transactional
    public void testSaveAndRetriveWrongOutlookPassword() throws Exception
    {

        // if no userid, then there are no user profiles in the system, and we can't test the encryption.
        // we won't actually create a new user profile in this test.
        if ( userid == null )
        {
            return;
        }

        expect(authentication.getName()).andReturn(userid).times(2);
        expect(authentication.getCredentials()).andReturn("AcMd3v$");
        expect(authentication.getCredentials()).andReturn("AcMd3v1");
        replayAll();

        OutlookDTO in = new OutlookDTO();
        in.setOutlookPassword("Armedia123");
        userOrgService.saveOutlookPassword(authentication, in);


        OutlookDTO outlookDTO = userOrgService.retrieveOutlookPassword(authentication);

        verifyAll();
    }
}