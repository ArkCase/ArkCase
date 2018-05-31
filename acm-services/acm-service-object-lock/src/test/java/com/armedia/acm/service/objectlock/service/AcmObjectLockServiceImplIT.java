package com.armedia.acm.service.objectlock.service;

/*-
 * #%L
 * ACM Service: Object lock
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockEvent;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Files;

/**
 * Created by nebojsha on 25.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-object-lock-test.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-lock-mule-test.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-object-converter.xml"
})
public class AcmObjectLockServiceImplIT extends EasyMockSupport
{

    @Autowired
    private AcmObjectLockServiceImpl acmObjectLockService;
    private AcmObjectLockDao acmObjectLockDao;
    private Authentication authMock;
    private String authName = "auditUser";
    private ExecuteSolrQuery executeSolrQueryMock;
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Before
    public void beforeEachTest()
    {
        authMock = createMock(Authentication.class);
        acmObjectLockDao = createMock(AcmObjectLockDao.class);
        acmObjectLockService.setAcmObjectLockDao(acmObjectLockDao);

        executeSolrQueryMock = createMock(ExecuteSolrQuery.class);
        acmObjectLockService.setExecuteSolrQuery(executeSolrQueryMock);

        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        acmObjectLockService.setApplicationEventPublisher(mockApplicationEventPublisher);

        assertNotNull(acmObjectLockService);
        EasyMock.expect(authMock.getName()).andReturn(authName).anyTimes();

        SecurityContextHolder.getContext().setAuthentication(authMock);
    }

    @Test
    public void testCreateExistingSameUserLock() throws Exception
    {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator(authName);
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);
        Capture<AcmObjectLock> objectLockCapture = EasyMock.newCapture();
        EasyMock.expect(acmObjectLockDao.save(capture(objectLockCapture))).andAnswer(new IAnswer<AcmObjectLock>()
        {
            @Override
            public AcmObjectLock answer() throws Throwable
            {
                objectLockCapture.getValue().setId(1l);
                return objectLockCapture.getValue();
            }
        });
        Capture<AcmObjectLockEvent> capturedEvent = EasyMock.newCapture();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verifyAll();
    }

    @Test
    public void testCreateExistingDifferentUserLock() throws Exception
    {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator("differentUser");
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);
        Capture<AcmObjectLock> objectLockCapture = EasyMock.newCapture();
        EasyMock.expect(acmObjectLockDao.save(capture(objectLockCapture))).andAnswer(new IAnswer<AcmObjectLock>()
        {
            @Override
            public AcmObjectLock answer() throws Throwable
            {
                objectLockCapture.getValue().setId(1l);
                return objectLockCapture.getValue();
            }
        });
        Capture<AcmObjectLockEvent> capturedEvent = EasyMock.newCapture();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verifyAll();
    }

    @Test
    public void testCreateNotExistingLock() throws Exception
    {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";

        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(null);
        Capture<AcmObjectLock> objectLockCapture = EasyMock.newCapture();
        EasyMock.expect(acmObjectLockDao.save(capture(objectLockCapture))).andAnswer(new IAnswer<AcmObjectLock>()
        {
            @Override
            public AcmObjectLock answer() throws Throwable
            {
                objectLockCapture.getValue().setId(1l);
                return objectLockCapture.getValue();
            }
        });
        Capture<AcmObjectLockEvent> capturedEvent = EasyMock.newCapture();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verifyAll();
    }

    @Test
    public void testRemoveLock() throws Exception
    {
        long objectId = 1l;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setLockType(lockType);
        lock.setCreator(authName);
        EasyMock.expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);

        acmObjectLockDao.remove(lock);
        EasyMock.expectLastCall();

        Capture<AcmObjectLockEvent> capturedEvent = EasyMock.newCapture();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        acmObjectLockService.removeLock(objectId, objectType, lockType, authMock);

        verifyAll();
    }

    @Test
    public void testGetDocumentsWithoutLock() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK  AND parent_type_s:CASE_FILE AND creator_lcs:auditUser", 0, 1,
                "")).andReturn(jsonContent);
        replayAll();
        String result = acmObjectLockService.getDocumentsWithLock("CASE_FILE", authMock, authMock.getName(), 0, 1, "", null);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        verifyAll();
    }

    @Test
    public void testGetDocumentsWithoutLockWithFilter() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        String filter = "fq=status_s:OPEN";

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK  AND parent_type_s:CASE_FILE AND creator_lcs:auditUser", 0, 1, "",
                "fq=status_s:OPEN")).andReturn(jsonContent);
        replayAll();
        String result = acmObjectLockService.getDocumentsWithLock("CASE_FILE", authMock, authMock.getName(), 0, 1, "", filter);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        verifyAll();
    }

    @Test
    public void testGetDocumentsLockedByUser() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "-({!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK) AND object_type_s:CASE_FILE", 0, 1, ""))
                        .andReturn(jsonContent);
        replayAll();
        String result = acmObjectLockService.getDocumentsWithoutLock("CASE_FILE", authMock, 0, 1, "", null);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        verifyAll();
    }

    @Test
    public void testGetDocumentsLockedByUserWithFilter() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        String filter = "fq=status_s:OPEN";

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "-({!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK) AND object_type_s:CASE_FILE", 0, 1, "", "fq=status_s:OPEN"))
                        .andReturn(jsonContent);
        replayAll();
        String result = acmObjectLockService.getDocumentsWithoutLock("CASE_FILE", authMock, 0, 1, "", filter);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        verifyAll();
    }
}
