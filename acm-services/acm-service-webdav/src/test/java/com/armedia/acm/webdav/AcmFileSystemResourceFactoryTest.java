package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.milton.resource.Resource;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Created by dmiller on 7/20/16.
 */
public class AcmFileSystemResourceFactoryTest extends EasyMockSupport
{
    private AcmFileSystemResourceFactory unit;
    private EcmFileDao mockEcmFileDao;

    private AcmWebDAVSecurityManager mockAcmWebDAVSecurityManager;
    private EhCacheCacheManager webDAVContainerIdCacheManager;
    private CacheManager cacheManager;
    private String documentUrlPattern = "^.*\\/(.*)\\/(.*)\\/([\\d]*)\\/.*\\/([\\d]*)\\/([\\d]*)\\/(.*\\.[doc|docx|xlsx|xls|ppt|pptx|pdf|tmp]*)$";

    @Before
    public void setUp() throws Exception
    {
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockAcmWebDAVSecurityManager = createMock(AcmWebDAVSecurityManager.class);
        webDAVContainerIdCacheManager = new EhCacheCacheManager();
        cacheManager = new CacheManager();
        webDAVContainerIdCacheManager.setCacheManager(cacheManager);
        Cache webdavCache = new Cache("webdav_container_id_cache", 100, false, false, 300, 300);
        webDAVContainerIdCacheManager.getCacheManager().addCache(webdavCache);

        unit = new AcmFileSystemResourceFactory();
        unit.setFileDao(mockEcmFileDao);
        unit.setSecurityManager(mockAcmWebDAVSecurityManager);
        unit.setRealDocumentUrlPattern(Pattern.compile(documentUrlPattern));
        unit.setWebDAVContainerIdCacheManager(webDAVContainerIdCacheManager);
    }

    @After
    public void tearDown() throws Exception
    {
        cacheManager.shutdown();
    }

    @Test
    public void getResource_returnsOptionsResourceForNonFileRequests() throws Exception
    {
        String host = "www.dead.net";
        String path = "/arkcase/webdav/arkcase-admin@arkcase.org/DOC_REPO/109/Root/108/119";

        Resource resource = unit.getResource(host, path);

        assertTrue(resource instanceof AcmRootResource);
    }

    @Test
    public void getResource_returnsFileResourceForAPdfRequest() throws Exception
    {
        String host = "www.dead.net";
        String path = "/arkcase/webdav/arkcase-admin@arkcase.org/DOC_REPO/109/Root/108/119/Support Team - Product Requirements - Meeting Notes.pdf";

        Matcher matcher = Pattern.compile(documentUrlPattern).matcher(path);
        assertTrue("path should be a good file path", matcher.matches());

        Long docId = Long.valueOf(matcher.group(5));

        expect(mockEcmFileDao.find(docId)).andReturn(new EcmFile());

        replayAll();

        Resource resource = unit.getResource(host, path);

        verifyAll();

        assertTrue("resource should be a regular file  resource", resource instanceof AcmFileResource);
    }

    @Test
    public void getResource_returnsTempResourceForTmpFileRequest() throws Exception
    {
        String host = "www.dead.net";
        String randomUuid = UUID.randomUUID().toString();
        String path = "/arkcase/webdav/arkcase-admin@arkcase.org/DOC_REPO/109/Root/108/119/" + randomUuid + ".tmp";

        assertTrue("path should be a good file path", Pattern.compile(documentUrlPattern).matcher(path).matches());

        // we expect null the first time, since we didn't ask about this temp file yet; and we expect a resource
        // the second time, since the first request should have created it.

        Resource resource = unit.getResource(host, path);
        assertNull(resource);

        resource = unit.getResource(host, path);
        assertNotNull(resource);

        System.out.println("Resource class: " + resource.getClass().getName());
        assertTrue(resource instanceof AcmTempFileResource);
        AcmTempFileResource tempFileResource = (AcmTempFileResource) resource;
        assertEquals(Long.valueOf(119), tempFileResource.getTargetFileId());
        assertEquals("arkcase-admin@arkcase.org", tempFileResource.getUserId());
        assertEquals("109", tempFileResource.getContainerObjectId());
        assertEquals("DOC_REPO", tempFileResource.getContainerObjectType());

    }
}
