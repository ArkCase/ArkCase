package com.armedia.acm.plugins.ecm.service.lock;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockEvent;
import com.armedia.acm.service.objectlock.model.AcmObjectUnlockEvent;
import com.armedia.acm.service.objectlock.service.AcmObjectLockServiceImpl;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by bojan.milenkoski on 08/05/2018.
 */
public class FileLockingProviderTest extends EasyMockSupport
{
    private FileLockingProvider fileObjectLockingProvider;
    private AcmObjectLockServiceImpl objectLockService;
    private AcmObjectLockDao objectLockDaoMock;
    private ApplicationEventPublisher applicationEventPublisherMock;
    private UserDao mockUserDao;
    private AcmUser acmUser;
    List<AcmUser> acmUsers = new ArrayList<>();

    @Before
    public void setup()
    {
        objectLockDaoMock = createMock(AcmObjectLockDao.class);
        applicationEventPublisherMock = createMock(ApplicationEventPublisher.class);
        mockUserDao = createMock(UserDao.class);

        objectLockService = new AcmObjectLockServiceImpl();
        objectLockService.setAcmObjectLockDao(objectLockDaoMock);
        objectLockService.setApplicationEventPublisher(applicationEventPublisherMock);

        acmUser = new AcmUser();
        acmUser.setUserId("userId");

        fileObjectLockingProvider = new FileLockingProvider();
        fileObjectLockingProvider.setObjectLockService(objectLockService);
        fileObjectLockingProvider.setExpiryTimeInMilliseconds(10_000L);
        fileObjectLockingProvider.setUserDao(mockUserDao);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test(expected = AcmObjectLockException.class)
    public void acquireUnknownLockThrowsException() throws AcmObjectLockException
    {
        // given
        Long objectId = 1L;
        String objectType = "FILE";
        String lockType = "UNKNOWN";
        String userId = "userId";

        // when
        fileObjectLockingProvider.acquireObjectLock(objectId, objectType, lockType, null, false, userId);

        // then
        fail("AcmObjectLockException should have been thrown for unknown lock type!");
    }

    @Test
    public void acquireLocksWhenNoExistingLockReturnsNewAcquiredLockAndSavesToDb() throws AcmObjectLockException
    {
        // given
        Long objectId = 1L;
        String[] lockTypes = { "READ", "WRITE", "SHARED_WRITE", "DELETE" };
        String objectType = "FILE";
        String userId = "userId";

        for (String lockType : lockTypes)
        {
            EasyMock.reset(objectLockDaoMock, applicationEventPublisherMock, mockUserDao);
            expect(objectLockDaoMock.findLock(objectId, objectType)).andReturn(null).anyTimes();
            expect(objectLockDaoMock.save(anyObject(AcmObjectLock.class)))
                    .andAnswer(() -> (AcmObjectLock) EasyMock.getCurrentArguments()[0]);
            expect(mockUserDao.findByUserId(userId)).andReturn(acmUser).anyTimes();
            applicationEventPublisherMock.publishEvent(anyObject(AcmObjectLockEvent.class));
            expectLastCall();

            // when
            replayAll();
            AcmObjectLock objectLock = fileObjectLockingProvider.acquireObjectLock(objectId, objectType, lockType, null, false, userId);

            verifyAll();
            // then
            assertEquals(objectId, objectLock.getObjectId());
            assertEquals(objectType, objectLock.getObjectType());
            assertEquals(lockType, objectLock.getLockType());
            assertEquals(userId, objectLock.getCreator());
            assertTrue("Created date should be set now!",
                    Math.abs(new Date().getTime() - objectLock.getCreated().getTime()) < 100000);
            assertEquals(fileObjectLockingProvider.getExpiryTimeInMilliseconds().longValue(),
                    objectLock.getExpiry().getTime() - objectLock.getCreated().getTime());
        }
    }

    @Test
    public void acquireLocksWhenExistingLocks()
    {
        // testData object: objectId, acquiredLockObjectType, acquiredLockType, acquiredLockUserId, existingLockType,
        // existingLockUserId, changeLockType, changeUser, saveLockToDatabase, expectException
        Object[][] testData = {
                { 1L, "FILE", "READ", "userId", "READ", "userId", true, true, true, false },
                { 1L, "FILE", "READ", "userId", "READ", "anotherUserId", true, true, true, false },
                { 1L, "FILE", "READ", "userId", "WRITE", "userId", true, true, false, false },
                { 1L, "FILE", "READ", "userId", "WRITE", "anotherUserId", true, true, false, false },
                { 1L, "FILE", "READ", "userId", "SHARED_WRITE", "userId", true, true, false, false },
                { 1L, "FILE", "READ", "userId", "SHARED_WRITE", "anotherUserId", true, true, false, false },
                { 1L, "FILE", "READ", "userId", "DELETE", "userId", true, true, false, false },
                { 1L, "FILE", "READ", "userId", "DELETE", "anotherUserId", true, true, false, false },
                { 1L, "FILE", "WRITE", "userId", "READ", "userId", true, true, true, false },
                { 1L, "FILE", "WRITE", "userId", "READ", "anotherUserId", true, true, true, false },
                { 1L, "FILE", "WRITE", "userId", "WRITE", "userId", false, false, true, false },
                { 1L, "FILE", "WRITE", "userId", "WRITE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "WRITE", "userId", "SHARED_WRITE", "userId", false, false, false, true },
                { 1L, "FILE", "WRITE", "userId", "SHARED_WRITE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "WRITE", "userId", "DELETE", "userId", false, false, false, true },
                { 1L, "FILE", "WRITE", "userId", "DELETE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "READ", "userId", true, false, true, false },
                { 1L, "FILE", "DELETE", "userId", "READ", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "DELETE", "userId", true, true, true, false },
                { 1L, "FILE", "DELETE", "userId", "DELETE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "WRITE", "userId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "WRITE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "SHARED_WRITE", "userId", false, false, false, true },
                { 1L, "FILE", "DELETE", "userId", "SHARED_WRITE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "READ", "userId", true, true, true, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "READ", "anotherUserId", true, true, true, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "userId", false, false, true, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "anotherUserId", false, false, true, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "WRITE", "userId", false, false, false, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "WRITE", "anotherUserId", false, false, false, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "DELETE", "userId", false, false, false, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "DELETE", "anotherUserId", false, false, false, true }
        };

        for (Object[] data : testData)
        {
            EasyMock.reset(objectLockDaoMock, applicationEventPublisherMock, mockUserDao);

            AcmObjectLock existingObjectLock = new AcmObjectLock();
            existingObjectLock.setObjectId((Long) data[0]);
            existingObjectLock.setObjectType((String) data[1]);
            existingObjectLock.setLockType((String) data[4]);
            existingObjectLock.setCreator((String) data[5]);
            Date existingLockCreated = new DateTime(new Date()).minusMinutes(5).toDate();
            existingObjectLock.setCreated(existingLockCreated);

            acmUsers.add(acmUser);

            expect(objectLockDaoMock.findLock((Long) data[0], (String) data[1])).andReturn(existingObjectLock).anyTimes();
            expect(mockUserDao.findByPrefix(existingObjectLock.getCreator())).andReturn(acmUsers).anyTimes();

            if ((Boolean) data[8])
            {
                expect(objectLockDaoMock.save(anyObject(AcmObjectLock.class)))
                        .andAnswer(() -> (AcmObjectLock) EasyMock.getCurrentArguments()[0]);
                applicationEventPublisherMock.publishEvent(anyObject(AcmObjectLockEvent.class));
                expectLastCall();
            }

            // when
            replayAll();
            AcmObjectLock objectLock = null;
            try
            {
                System.out.println("Acquiring lock with test data: " + Arrays.toString(data));
                objectLock = fileObjectLockingProvider.acquireObjectLock((Long) data[0], (String) data[1], (String) data[2],
                        null, false, (String) data[3]);

                if ((Boolean) data[9])
                {
                    fail("Exception should have been thrown at testData: " + Arrays.toString(data) + "!");
                }
            }
            catch (AcmObjectLockException e)
            {
                if (!(Boolean) data[9])
                {
                    fail("Exception should not have been thrown at testData: " + Arrays.toString(data) + "!");
                }
            }
            // then
            verifyAll();
            if (!(Boolean) data[9])
            {
                assertEquals("Error at test data: " + Arrays.toString(data), data[0], objectLock.getObjectId());
                assertEquals("Error at test data: " + Arrays.toString(data), data[1], objectLock.getObjectType());
                if ((Boolean) data[6])
                {
                    assertEquals("Error at test data: " + Arrays.toString(data), data[2], objectLock.getLockType());
                }
                else
                {
                    assertEquals("Error at test data: " + Arrays.toString(data), data[4], objectLock.getLockType());
                }
                if ((Boolean) data[7])
                {
                    assertEquals("Error at test data: " + Arrays.toString(data), data[3], objectLock.getCreator());
                }
                else
                {
                    assertEquals("Error at test data: " + Arrays.toString(data), data[5], objectLock.getCreator());
                }
                assertTrue("Created date should be set now! Test data: " + Arrays.toString(data),
                        Math.abs(new Date().getTime() - objectLock.getCreated().getTime()) < 1000);
                assertEquals(fileObjectLockingProvider.getExpiryTimeInMilliseconds().longValue(),
                        objectLock.getExpiry().getTime() - objectLock.getCreated().getTime());
            }
        }
    }

    @Test(expected = AcmObjectLockException.class)
    public void releaseUnknownLockThrowsException() throws AcmObjectLockException
    {
        // given
        Long objectId = 1L;
        String objectType = "FILE";
        String lockType = "UNKNOWN";
        String userId = "userId";

        // when
        fileObjectLockingProvider.releaseObjectLock(objectId, objectType, lockType, false, userId, null);

        // then
        fail("AcmObjectLockException should have been thrown for unknown lock type!");
    }

    @Test
    public void releaseLocksWhenNoExistinLocks() throws AcmObjectLockException
    {
        // given
        Long objectId = 1L;
        String[] lockTypes = { "READ", "WRITE", "SHARED_WRITE", "DELETE" };
        String objectType = "FILE";
        String userId = "userId";

        for (String lockType : lockTypes)
        {
            EasyMock.reset(objectLockDaoMock, applicationEventPublisherMock, mockUserDao);
            expect(objectLockDaoMock.findLock(objectId, objectType)).andReturn(null).anyTimes();
            expect(mockUserDao.findByUserId(userId)).andReturn(acmUser).anyTimes();

            // when
            replayAll();
            fileObjectLockingProvider.releaseObjectLock(objectId, objectType, lockType, false, userId, null);

            // then
            verifyAll();
        }
    }

    @Test
    public void releaseLocksWhenExistingLocks()
    {
        // testData object: objectId, releasedLockObjectType, releasedLockType, releasedLockUserId, existingLockId,
        // existingLockType, existingLockUserId, deleteLockFromDatabase, lockIdToRelease, expectException
        Object[][] testData = {
                { 1L, "FILE", "READ", "userId", "READ", "userId", 1l, true, null, false },
                { 1L, "FILE", "READ", "userId", "READ", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "WRITE", "userId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "WRITE", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "SHARED_WRITE", "userId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "SHARED_WRITE", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "DELETE", "userId", 1l, false, null, false },
                { 1L, "FILE", "READ", "userId", "DELETE", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "WRITE", "userId", "READ", "userId", 1l, false, null, false },
                { 1L, "FILE", "WRITE", "userId", "READ", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "WRITE", "userId", "WRITE", "userId", 1l, true, null, false },
                { 1L, "FILE", "WRITE", "userId", "WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "WRITE", "userId", "SHARED_WRITE", "userId", 1l, false, null, true },
                { 1L, "FILE", "WRITE", "userId", "SHARED_WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "WRITE", "userId", "DELETE", "userId", 1l, false, null, true },
                { 1L, "FILE", "WRITE", "userId", "DELETE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "READ", "userId", 1l, false, null, false },
                { 1L, "FILE", "DELETE", "userId", "READ", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "DELETE", "userId", 1l, true, null, false },
                { 1L, "FILE", "DELETE", "userId", "DELETE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "WRITE", "userId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "SHARED_WRITE", "userId", 1l, false, null, true },
                { 1L, "FILE", "DELETE", "userId", "SHARED_WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "READ", "userId", 1l, false, null, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "READ", "anotherUserId", 1l, false, null, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "userId", 1l, true, 1l, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "anotherUserId", 1l, true, 1l, false },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "userId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "SHARED_WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "WRITE", "userId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "WRITE", "anotherUserId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "DELETE", "userId", 1l, false, null, true },
                { 1L, "FILE", "SHARED_WRITE", "userId", "DELETE", "anotherUserId", 1l, false, null, true }
        };

        for (Object[] data : testData)
        {
            EasyMock.reset(objectLockDaoMock, applicationEventPublisherMock, mockUserDao);

            AcmObjectLock existingObjectLock = new AcmObjectLock();
            existingObjectLock.setId((Long) data[6]);
            existingObjectLock.setObjectId((Long) data[0]);
            existingObjectLock.setObjectType((String) data[1]);
            existingObjectLock.setLockType((String) data[4]);
            existingObjectLock.setCreator((String) data[5]);
            Date existingLockCreated = new DateTime(new Date()).minusMinutes(5).toDate();
            existingObjectLock.setCreated(existingLockCreated);

            acmUsers.add(acmUser);

            expect(objectLockDaoMock.findLock((Long) data[0], (String) data[1])).andReturn(existingObjectLock).anyTimes();
            expect(mockUserDao.findByPrefix(existingObjectLock.getCreator())).andReturn(acmUsers).anyTimes();
            if ((Boolean) data[7])
            {
                objectLockDaoMock.remove(anyObject(AcmObjectLock.class));
                expectLastCall();
                applicationEventPublisherMock.publishEvent(anyObject(AcmObjectUnlockEvent.class));
                expectLastCall();
            }

            // when
            replayAll();
            try
            {
                System.out.println("Releasing lock with test data: " + Arrays.toString(data));
                fileObjectLockingProvider.releaseObjectLock((Long) data[0], (String) data[1], (String) data[2], false,
                        (String) data[3], (Long) data[8]);

                if ((Boolean) data[9])
                {
                    fail("Exception should have been thrown at testData: " + Arrays.toString(data) + "!");
                }
            }
            catch (AcmObjectLockException e)
            {
                if (!(Boolean) data[9])
                {
                    fail("Exception should not have been thrown at testData: " + Arrays.toString(data) + "!");
                }
            }

            // then
            verifyAll();
        }
    }
}
