package com.armedia.acm.plugins.ecm.service;

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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrConfig;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;

/**
 * Created by maksud.sharif on 6/19/2017.
 */
public class EcmFileToSolrTransformerTest extends EasyMockSupport
{
    private EcmFile in;
    private AcmUser user;
    private SearchAccessControlFields mockSearchAccessControlFields;
    private UserDao mockUserDao;
    private EcmFileToSolrTransformer unit;

    private SolrConfig solrConfig;

    @Before
    public void setUp() throws Exception
    {
        in = new EcmFile();
        user = new AcmUser();
        user.setUserId("user");

        setupEcmFile(in);
        mockSearchAccessControlFields = createMock(SearchAccessControlFields.class);
        mockUserDao = createMock(UserDao.class);

        unit = new EcmFileToSolrTransformer();
        unit.setSearchAccessControlFields(mockSearchAccessControlFields);
        unit.setUserDao(mockUserDao);

        DataAccessControlConfig dacConfig = new DataAccessControlConfig();
        dacConfig.setEnableDocumentACL(true);
        unit.setDacConfig(dacConfig);

        solrConfig = new SolrConfig();
        unit.setSolrConfig(solrConfig);
    }

    private void setupEcmFile(EcmFile in)
    {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        Date now = Date.from(nowUTC.toInstant());

        String userId = "user";

        AcmContainer container = new AcmContainer();
        container.setId(-1L);
        container.setContainerObjectId(-1L);
        container.setContainerObjectType("CONTAINER");
        in.setContainer(container);

        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("101");
        folder.setCmisRepositoryId("101");
        folder.setCreated(now);
        folder.setCreator(userId);
        folder.setId(-1L);
        folder.setModified(now);
        folder.setModifier(userId);
        folder.setName("FOLDER_NAME");
        in.setFolder(folder);

        in.setCreated(now);
        in.setCreator(userId);

        in.setModifier(userId);
        in.setModified(now);

        in.setFileId(101L);
        in.setStatus("ACTIVE");
        in.setFileName("TEST_FILE");
        in.setActiveVersionTag("ACTIVE_TAG");
        in.setCategory("CATEGORY");
        in.setCmisRepositoryId("REPO_ID");
        in.setFileActiveVersionMimeType("text/plain");
        in.setDescription("Description");
        in.setFileActiveVersionNameExtension(".txt");
        in.setFileLang("en-US");
        in.setFileSource("SOURCE");
        in.setVersionSeriesId("1");

        EcmFileVersion version = new EcmFileVersion();
        version.setCreator(userId);
        version.setCmisObjectId("REPO_ID");
        version.setCreated(now);
        version.setFile(in);
        version.setModified(now);
        version.setModifier(userId);
        version.setVersionTag("VERSION_TAG");
        version.setVersionFileNameExtension(".txt");
        version.setVersionMimeType("text/plain");
        in.setVersions(Collections.singletonList(version));

        in.setFileType("FILE_TYPE");
        in.setPageCount(1);
        in.setLegacySystemId("LEGACY_ID");

        AcmAssociatedTag tag = new AcmAssociatedTag();
        tag.setCreated(now);
        tag.setCreator(userId);
        tag.setId(-101L);
        tag.setModified(now);
        tag.setModifier(userId);
        tag.setParentId(-1L);
        tag.setParentTitle(in.getFileName());

        AcmTag acmTag = new AcmTag();
        acmTag.setCreated(now);
        acmTag.setCreator(userId);
        acmTag.setModified(now);
        acmTag.setModifier(userId);
        acmTag.setTagDescription("TAG_DESCRIPTION");
        acmTag.setTagName("TAG_NAME");
        acmTag.setTagText("TAG_NEXT");
        acmTag.setTagToken("TAG_TOKEN");
        tag.setTag(acmTag);

        in.setTags(Collections.singletonList(tag));
    }

    @Test
    public void toContentFileIndex()
    {
        solrConfig.setEnableContentFileIndexing(true);

        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();

        expect(mockUserDao.quietFindByUserId(eq("user"))).andReturn(user).times(2);
        expect(mockUserDao.quietFindByUserId(null)).andReturn(null);

        replayAll();
        SolrContentDocument result = unit.toContentFileIndex(in);
        verifyAll();

        validateResult(result);

    }

    @Test
    public void toSolrAdvancedSearch()
    {
        solrConfig.setEnableContentFileIndexing(false);

        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();

        expect(mockUserDao.quietFindByUserId(eq("user"))).andReturn(user).times(2);
        expect(mockUserDao.quietFindByUserId(null)).andReturn(null);

        replayAll();
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        validateResult(result);
    }

    @Test
    public void toSolrQuickSearch()
    {
        solrConfig.setEnableContentFileIndexing(false);

        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();

        replayAll();
        SolrDocument result = unit.toSolrQuickSearch(in);
        verifyAll();

        validateResult(result);

    }

    private void validateResult(SolrAdvancedSearchDocument result)
    {
        assertNotNull(result);
        assertEquals(result.getEcmFileId(), String.valueOf(in.getVersionSeriesId()));
        assertEquals("101-FILE", result.getId());
        assertEquals(String.valueOf(in.getFileId()), result.getObject_id_s());
        assertEquals(in.getObjectType(), result.getObject_type_s());
        assertEquals(in.getFileName(), result.getName());
        assertEquals(in.getFileActiveVersionNameExtension(), result.getExt_s());
        assertEquals(in.getFileActiveVersionMimeType(), result.getMime_type_s());
        assertEquals(in.getCreated(), result.getCreate_date_tdt());
        assertEquals(in.getCreator(), result.getCreator_lcs());
        assertEquals(in.getModified(), result.getModified_date_tdt());
        assertEquals(in.getModifier(), result.getModifier_lcs());
        assertEquals(in.getFileName(), result.getTitle_parseable());
        assertEquals(in.getFileName(), result.getTitle_parseable_lcs());
        assertEquals(in.getStatus(), result.getStatus_lcs());
        assertEquals(in.getFileType(), result.getType_lcs());
        assertEquals(String.valueOf(in.getParentObjectId()), result.getParent_id_s());
        assertEquals(in.getParentObjectType(), result.getParent_type_s());
        assertEquals(10, result.getAdditionalProperties().size());
    }

    private void validateResult(SolrDocument result)
    {
        assertNotNull(result);
        assertEquals("101-FILE", result.getId());
        assertEquals(String.valueOf(in.getFileId()), result.getObject_id_s());
        assertEquals(in.getObjectType(), result.getObject_type_s());
        assertEquals(in.getFileName(), result.getName());
        assertEquals(in.getFileActiveVersionNameExtension(), result.getExt_s());
        assertEquals(in.getFileActiveVersionMimeType(), result.getMime_type_s());
        assertEquals(in.getFileName(), result.getTitle_parseable());
        assertEquals(in.getFileName(), result.getTitle_parseable_lcs());
        assertEquals(6, result.getAdditionalProperties().size());
    }

    private void validateResult(SolrContentDocument result)
    {
        assertNotNull(result);
        assertEquals(result.getEcmFileId(), String.valueOf(in.getVersionSeriesId()));
        assertEquals("101-FILE", result.getId());
        assertEquals(String.valueOf(in.getFileId()), result.getObject_id_s());
        assertEquals(in.getObjectType(), result.getObject_type_s());
        assertEquals(in.getFileName(), result.getName());
        assertEquals(in.getFileActiveVersionNameExtension(), result.getExt_s());
        assertEquals(in.getFileActiveVersionMimeType(), result.getMime_type_s());
        assertEquals(in.getCreated(), result.getCreate_date_tdt());
        assertEquals(in.getCreator(), result.getCreator_lcs());
        assertEquals(in.getModified(), result.getModified_date_tdt());
        assertEquals(in.getModifier(), result.getModifier_lcs());
        assertEquals(in.getFileName(), result.getTitle_parseable());
        assertEquals(in.getFileName(), result.getTitle_parseable_lcs());
        assertEquals(in.getStatus(), result.getStatus_lcs());
        assertEquals(in.getFileType(), result.getType_lcs());
        assertEquals(String.valueOf(in.getParentObjectId()), result.getParent_id_s());
        assertEquals(in.getParentObjectType(), result.getParent_type_s());
        assertEquals(10, result.getAdditionalProperties().size());
    }

}
