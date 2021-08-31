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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import io.milton.resource.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Created by dmiller on 7/20/16.
 */
public class AcmFileSystemResourceFactoryTest extends EasyMockSupport
{
    private AcmFileSystemResourceFactory unit;
    private EcmFileDao mockEcmFileDao;
    private AcmFolderDao mockFolderDao;
    private Cache cache;
    private AcmWebDAVSecurityManager mockAcmWebDAVSecurityManager;

    @Before
    public void setUp() throws Exception
    {
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockFolderDao = createMock(AcmFolderDao.class);
        mockAcmWebDAVSecurityManager = createMock(AcmWebDAVSecurityManager.class);

        unit = new AcmFileSystemResourceFactory();
        unit.setFileDao(mockEcmFileDao);
        unit.setFolderDao(mockFolderDao);
        unit.setSecurityManager(mockAcmWebDAVSecurityManager);

        Pattern urlPattern = Pattern.compile("^.*\\/(.*)\\/(.*)\\/([\\d]*)\\/.*\\/([\\d]*)\\/([\\d]*)\\/(.*\\.[doc|docx|xlsx|xls|ppt|pptx|pdf|tmp]*)$");
        unit.setRealDocumentUrlPattern(urlPattern);

        cache = createMock(Cache.class);
        EhCacheCacheManager cacheManager = createMock(EhCacheCacheManager.class);
        unit.setWebDAVContainerIdCacheManager(cacheManager);

    }

    @Test
    public void getResource_returnsOptionsResourceForNonFileRequests() throws Exception
    {
        String host = "www.dead.net";
        String path = "/123-456-78-90/FILE/EDIT_WORD_LOCK";

        expect(unit.getCache()).andReturn(cache);
        expect(cache.get(anyString())).andReturn(null);
        replayAll();

        Resource resource = unit.getResource(host, path);

        assertTrue(resource instanceof AcmRootResource);
    }

    @Test
    public void getResource_returnsOptionsForUrlThatHasAStringBeforeTheNumber() throws Exception
    {
        String host = "www.dead.net";
        String path = "/123-456-78-90/FILE/EDIT_WORD_LOCK/jgarcia12345.docx";

        expect(unit.getCache()).andReturn(cache);
        expect(cache.get(anyString())).andReturn(null);
        replayAll();

        Resource resource = unit.getResource(host, path);

        assertTrue(resource instanceof AcmRootResource);
    }

    @Test
    public void getResource_returnsFileResourceForFileRequest() throws Exception
    {
        String host = "www.dead.net";
        String path = "/arkcase/webdav/arkcase-admin@arkcase.org/CASE_FILE/102/01 Request/109/110/Test.docx";

        unit.setFileExtensionPattern(Pattern.compile("\\.(doc|dot|docx|dotx|docm|dotm|docb)$"));
        unit.setFilterMapping("webdav");
        expect(unit.getCache()).andReturn(cache);
        cache.put(anyString(), anyObject());

        expect(mockEcmFileDao.find(110L)).andReturn(new EcmFile());

        replayAll();

        Resource resource = unit.getResource(host, path);

        verifyAll();

        assertTrue(resource instanceof AcmFileResource);
    }
}
