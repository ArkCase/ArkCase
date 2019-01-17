package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.AcmQueue;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RunWith(EasyMockRunner.class)
public class QueuePropertyFileChangeWatcherTest extends EasyMockSupport
{

    @TestSubject
    private QueuePropertyFileChangeWatcher watcher = new QueuePropertyFileChangeWatcher();

    @Mock
    private AuditPropertyEntityAdapter auditPropertyEntityAdapterMock;

    @Mock
    private PlatformTransactionManager txManagerMock;

    @Mock
    private AcmQueueDao acmQueueDaoMock;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TransactionStatus mockedTransactionStatus;

    @Test
    public void testLoadQueues() throws Exception
    {
        // setup the instance that is being tested
        watcher.setAuditPropertyEntityAdapter(auditPropertyEntityAdapterMock);
        watcher.setTxManager(txManagerMock);
        watcher.setAcmQueueDao(acmQueueDaoMock);

        // setup the environment of the tested instance
        auditPropertyEntityAdapterMock.setUserId("SYSTEM_USER");
        expectLastCall();

        expect(acmQueueDaoMock.getEm()).andReturn(entityManager);
        entityManager.flush();
        expectLastCall();

        expect(acmQueueDaoMock.findAll()).andReturn(createStoredQueues());
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManagerMock);
        PowerMock.expectNew(TransactionTemplate.class, txManagerMock).andReturn(transactionTemplate);
        expect(txManagerMock.getTransaction(transactionTemplate)).andReturn(mockedTransactionStatus);
        txManagerMock.commit(mockedTransactionStatus);
        expectLastCall();

        Capture<AcmQueue> capturedArgs = Capture.newInstance(CaptureType.ALL);

        expect(acmQueueDaoMock.save(capture(capturedArgs))).andReturn(null).anyTimes();

        replayAll();

        // execute the method being tested
        Properties properties = createLoadedProperties();
        watcher.loadQueues(properties);

        // verify the expected interaction with the AcmQueueDao
        List<AcmQueue> values = capturedArgs.getValues();

        assertTrue(values.stream().map(q -> q.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(3l)));
        assertTrue(
                values.stream().map(q -> q.getName()).collect(Collectors.toList()).containsAll(Arrays.asList("Intake", "Hold", "Suspend")));
        assertTrue(values.stream().map(q -> q.getDisplayOrder()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2, 4)));

    }

    private Properties createLoadedProperties()
    {
        Properties properties = new Properties();
        properties.setProperty("INTAKE_QUEUE_NAME_1", "Intake");
        properties.setProperty("HOLD_QUEUE_NAME_2", "Hold");
        properties.setProperty("SUSPEND_QUEUE_NAME_4", "Suspend");
        return properties;
    }

    private List<AcmQueue> createStoredQueues()
    {
        List<AcmQueue> storedQueues = new ArrayList<>();
        AcmQueue queue1 = new AcmQueue(3l, "Suspend", 3);
        storedQueues.add(queue1);
        AcmQueue queue2 = new AcmQueue(5l, "Fulfill", 5);
        storedQueues.add(queue2);
        AcmQueue queue3 = new AcmQueue(6l, "Approve", 6);
        storedQueues.add(queue3);
        return storedQueues;
    }

}
