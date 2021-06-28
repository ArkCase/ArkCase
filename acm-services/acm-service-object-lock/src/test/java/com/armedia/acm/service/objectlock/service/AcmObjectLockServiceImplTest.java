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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockEvent;
import com.armedia.acm.service.objectlock.model.AcmObjectUnlockEvent;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

import java.nio.file.Files;

/**
 * Created by nebojsha on 25.08.2015.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*", "javax.security.*" })
@PrepareForTest({ AuthenticationUtils.class })
public class AcmObjectLockServiceImplTest 
{
    private AcmObjectLockServiceImpl acmObjectLockService;

    private AcmObjectLockDao acmObjectLockDao;
    private Authentication authMock;
    private String authName = "auditUser";
    // private AuthenticationUtils authenticationUtils;

    private ExecuteSolrQuery executeSolrQueryMock;

    private ApplicationEventPublisher mockApplicationEventPublisher;

    private Object[] mocks;

    private HttpServletRequest mockRequest;

    @Before
    public void setUp()
    {
        acmObjectLockService = new AcmObjectLockServiceImpl();

        acmObjectLockDao = createMock(AcmObjectLockDao.class);
        acmObjectLockService.setAcmObjectLockDao(acmObjectLockDao);

        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        acmObjectLockService.setApplicationEventPublisher(mockApplicationEventPublisher);

        executeSolrQueryMock = createMock(ExecuteSolrQuery.class);
        acmObjectLockService.setExecuteSolrQuery(executeSolrQueryMock);

        mockStatic(AuthenticationUtils.class);

        authMock = createMock(Authentication.class);
        mockRequest = createMock(HttpServletRequest.class);
        expect(authMock.getName()).andReturn(authName).anyTimes();

        SecurityContextHolder.getContext().setAuthentication(authMock);

        mocks = new Object[] { executeSolrQueryMock, acmObjectLockDao, mockApplicationEventPublisher, authMock };
    }

    @Test
    public void testCreateExistingSameUserLock() throws Exception
    {
        Long objectId = 1L;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator(authName);

        Capture<AcmObjectLockEvent> event = Capture.newInstance();

        expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);
        expect(acmObjectLockDao.save(lock)).andReturn(lock);
        when(AuthenticationUtils.getUserIpAddress()).thenReturn("");
        mockApplicationEventPublisher.publishEvent(capture(event));

        replay(mocks);

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verify(mocks);

        AcmObjectLockEvent captured = event.getValue();
        assertEquals(objectId, captured.getParentObjectId());
        assertEquals(objectType, captured.getParentObjectType());
        assertEquals("OBJECT_LOCK", captured.getObjectType());        
    }

    @Test
    public void testCreateExistingDifferentUserLock() throws Exception
    {
        // 2019-03-18 it seems odd that the lock service's create method 
        // just automatically overrides any existing lock, but that is in fact 
        // what the implementation does.  Whether that's actually correct, 
        // probably not.

        Long objectId = 1L;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setCreator("differentUser");

        expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);
        expect(acmObjectLockDao.save(lock)).andReturn(lock);
        Capture<AcmObjectLockEvent> event = Capture.newInstance();
        mockApplicationEventPublisher.publishEvent(capture(event));

        replay(mocks);

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verify(mocks);

        AcmObjectLockEvent captured = event.getValue();
        assertEquals(objectId, captured.getParentObjectId());
        assertEquals(objectType, captured.getParentObjectType());
        assertEquals("OBJECT_LOCK", captured.getObjectType());        
        
    }

    @Test
    public void testCreateNotExistingLock() throws Exception
    {
        Long objectId = 1L;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setLockType(lockType);

        expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(null);
        Capture<AcmObjectLock> saved = Capture.newInstance();
        expect(acmObjectLockDao.save(capture(saved))).andReturn(lock);
        Capture<AcmObjectLockEvent> event = Capture.newInstance();
        mockApplicationEventPublisher.publishEvent(capture(event));

        replay(mocks);

        acmObjectLockService.createLock(objectId, objectType, lockType, 1000l, authMock);

        verify(mocks);

        AcmObjectLockEvent captured = event.getValue();
        assertEquals(objectId, captured.getParentObjectId());
        assertEquals(objectType, captured.getParentObjectType());
        assertEquals("OBJECT_LOCK", captured.getObjectType()); 
    }

    @Test
    public void testRemoveLock() throws Exception
    {
        Long objectId = 1L;
        String objectType = "CASE_FILE";
        String lockType = "OBJECT_LOCK";
        AcmObjectLock lock = new AcmObjectLock(objectId, objectType);
        lock.setLockType(lockType);
        lock.setCreator(authName);

        expect(acmObjectLockDao.findLock(objectId, objectType)).andReturn(lock);

        acmObjectLockDao.remove(lock);

        Capture<AcmObjectUnlockEvent> event = Capture.newInstance();
        mockApplicationEventPublisher.publishEvent(capture(event));

        replay(mocks);

        acmObjectLockService.removeLock(objectId, objectType, lockType, authMock);

        verify(mocks);

        AcmObjectUnlockEvent captured = event.getValue();
        assertEquals(objectId, captured.getParentObjectId());
        assertEquals(objectType, captured.getParentObjectType());
        assertEquals("OBJECT_LOCK", captured.getObjectType()); 
    }

    @Test
    public void testGetDocumentsWithoutLock() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK  AND parent_type_s:CASE_FILE AND creator_lcs:auditUser", 0, 1,
                "")).andReturn(jsonContent);

        replay(mocks);

        String result = acmObjectLockService.getDocumentsWithLock("CASE_FILE", authMock, authMock.getName(), 0, 1, "", null);

        verify(mocks);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
    }

    @Test
    public void testGetDocumentsWithoutLockWithFilter() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        String filter = "fq=status_lcs:OPEN";

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK  AND parent_type_s:CASE_FILE AND creator_lcs:auditUser", 0, 1, "",
                "fq=status_lcs:OPEN")).andReturn(jsonContent);

        replay(mocks);

        String result = acmObjectLockService.getDocumentsWithLock("CASE_FILE", authMock, authMock.getName(), 0, 1, "", filter);

        verify(mocks); 

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        
    }

    @Test
    public void testGetDocumentsLockedByUser() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "-({!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK) AND object_type_s:CASE_FILE", 0, 1, ""))
                        .andReturn(jsonContent);

        replay(mocks);

        String result = acmObjectLockService.getDocumentsWithoutLock("CASE_FILE", authMock, 0, 1, "", null);

        verify(mocks);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
    }

    @Test
    public void testGetDocumentsLockedByUserWithFilter() throws Exception
    {
        Resource resourceFile = new ClassPathResource("/solrResponseCaseFile.json");
        String jsonContent = new String(Files.readAllBytes(resourceFile.getFile().toPath()));

        String filter = "fq=status_lcs:OPEN";

        expect(executeSolrQueryMock.getResultsByPredefinedQuery(authMock, SolrCore.ADVANCED_SEARCH,
                "-({!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK) AND object_type_s:CASE_FILE", 0, 1, "", "fq=status_lcs:OPEN"))
                        .andReturn(jsonContent);

        replay(mocks);

        String result = acmObjectLockService.getDocumentsWithoutLock("CASE_FILE", authMock, 0, 1, "", filter);

        verify(mocks);

        SearchResults results = new SearchResults();
        assertEquals(248, results.getNumFound(result));
        assertEquals(1, results.getDocuments(result).length());
        
    }
}
