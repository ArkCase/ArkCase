package com.armedia.acm.service.objectlock.service;

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
public class ObjectLockServiceImplIT {

    @Autowired
    AcmObjectLockService acmObjectLockService;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void beforeEachTest() {
        auditAdapter.setUserId("auditUser");
        assertNotNull(acmObjectLockService);
    }

    @Test
    @Transactional
    public void testCreateLock() throws Exception {
        AcmObjectLock ol = acmObjectLockService.createLock(1l, "CASE_FILE");
        assertNotNull(ol);
        assertNotNull(ol.getId());
    }

    @Test
    @Transactional
    public void testRemoveLock() throws Exception {
        AcmObjectLock ol = acmObjectLockService.createLock(1l, "CASE_FILE");
        assertNotNull(ol);
        assertNotNull(ol.getId());

        acmObjectLockService.removeLock(1l, "CASE_FILE");
    }

    @Test
    @Transactional
    public void testGetAllLocksByType() throws Exception {
        acmObjectLockService.createLock(1l, "CASE_FILE");
        acmObjectLockService.createLock(2l, "CASE_FILE");
        acmObjectLockService.createLock(3l, "CASE_FILE");
        acmObjectLockService.createLock(4l, "CASE_FILE");
        acmObjectLockService.createLock(5l, "CASE_FILE");

        assertEquals(5, acmObjectLockService.getAllLocksByType("CASE_FILE").size());

    }
}