package com.armedia.acm.service.objectlock.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by nebojsha on 25.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-lock.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ObjectLockDaoIT {

    @Autowired
    AcmObjectLockDao acmObjectLockDao;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void beforeEachTest() {
        auditAdapter.setUserId("auditUser");
        assertNotNull(acmObjectLockDao);
    }

    @Test
    @Transactional
    public void testGetAllLocksType() throws Exception {
        acmObjectLockDao.save(new AcmObjectLock(1l, "CASE_FILE"));
        acmObjectLockDao.save(new AcmObjectLock(1l, "COMPLAINT"));
        acmObjectLockDao.getEm().flush();

        assertEquals(1, acmObjectLockDao.getAllLocksType("CASE_FILE").size());
    }

    @Test
    @Transactional
    public void testFindLock() throws Exception {
        long objectId = 1l;
        String objectType = "COMPLAINT";
        acmObjectLockDao.save(new AcmObjectLock(objectId, objectType));

        assertNotNull(acmObjectLockDao.findLock(objectId, objectType));
    }

    @Test
    @Transactional
    public void testRemove() throws Exception {
        AcmObjectLock ol = acmObjectLockDao.save(new AcmObjectLock(1l, "CASE_FILE"));
    }
    @Test(expected = Exception.class)
    @Transactional
    public void testInsertLockForSameObjet() throws Exception {
        acmObjectLockDao.save(new AcmObjectLock(1l, "CASE_FILE"));
        acmObjectLockDao.getEm().flush();
        acmObjectLockDao.save(new AcmObjectLock(1l, "CASE_FILE"));
        acmObjectLockDao.getEm().flush();
    }
}