package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.exception.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertNotNull;

/**
 * Created by nebojsha on 25.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-object-lock-test.xml",
})
public class AcmObjectLockServiceImplTest extends EasyMockSupport {

    @Autowired
    private AcmObjectLockServiceImpl acmObjectLockService;
    private AcmObjectLockDao acmObjectLockDao;
    private Authentication auth;
    private String authName = "auditUser";

    @Before
    public void beforeEachTest() {
        auth = createMock(Authentication.class);
        acmObjectLockDao = createMock(AcmObjectLockDao.class);
        acmObjectLockService.setAcmObjectLockDao(acmObjectLockDao);

        assertNotNull(acmObjectLockService);
        EasyMock.expect(auth.getName()).andReturn(authName).anyTimes();
    }

    @Test
    public void testCreateExistingSameUserLock() throws Exception {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator(authName);
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, auth);

        verifyAll();
    }

    @Test(expected = AcmObjectLockException.class)
    public void testCreateExistingDifferentUserLock() throws Exception {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator("differentUser");
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, auth);

        verifyAll();
    }


    @Test
    public void testCreateNotExistingLock() throws Exception {
        long objectId = 1l;
        String objectType = "CASE_FILE";

        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(null);
        Capture<AcmObjectLock> objectLockCapture = EasyMock.newCapture();
        EasyMock.expect(acmObjectLockDao.save(capture(objectLockCapture))).andAnswer(new IAnswer<AcmObjectLock>() {
            @Override
            public AcmObjectLock answer() throws Throwable {
                objectLockCapture.getValue().setId(1l);
                return objectLockCapture.getValue();
            }
        });

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, auth);

        verifyAll();
    }


    @Test
    public void testRemoveLock() throws Exception {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator(authName);
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);

        acmObjectLockDao.remove(lock);
        EasyMock.expectLastCall();

        replayAll();

        acmObjectLockService.removeLock(objectId, objectType);


        verifyAll();
    }
}