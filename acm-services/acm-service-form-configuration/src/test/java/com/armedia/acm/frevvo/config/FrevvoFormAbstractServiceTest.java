package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.frevvo.config.service.FrevvoTestService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 11/3/14.
 */
public class FrevvoFormAbstractServiceTest extends EasyMockSupport
{
    private FrevvoTestService unit;
    private InputStream mockIs;
    private EcmFileService mockFileService;
    private Authentication mockAuthentication;
    private HttpServletRequest mockRequest;
    private EcmFileDao mockEcmFileDao;

    @Before
    public void setUp() throws Exception
    {
        mockIs = createMock(InputStream.class);
        mockAuthentication = createMock(Authentication.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockEcmFileDao = createMock(EcmFileDao.class);

        unit = new FrevvoTestService();

        mockFileService = createMock(EcmFileService.class);
        unit.setEcmFileService(mockFileService);

        unit.setAuthentication(mockAuthentication);
        unit.setRequest(mockRequest);
        unit.setEcmFileDao(mockEcmFileDao);
    }

    @Test
    public void updateXml_whenFormFileNotFound_thenDoNotUpdateEcmFile() throws Exception
    {
        AcmContainerEntity entity = createMock(AcmContainerEntity.class);
        AcmContainer container = new AcmContainer();
        container.setId(500L);
        container.setAttachmentFolder(new AcmFolder());
        container.getAttachmentFolder().setId(250L);

        expect(entity.getContainer()).andReturn(container).atLeastOnce();
        expect(mockEcmFileDao.findForContainerAttachmentFolderAndFileType(container.getId(),
                container.getAttachmentFolder().getId(), unit.getFormName() + "_xml")).andReturn(null);

        // this test is just to verify there are no further calls to the ECM service or ECM dao.
        // so we don't need any assertions.

        replayAll();

        unit.updateXML(entity, mockAuthentication, entity.getClass());

        verifyAll();
    }

    @Test
    public void saveAttachments_formXml() throws Exception
    {

        MultiValueMap<String, MultipartFile> attachments = new LinkedMultiValueMap<>();
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile formXml = new AcmMultipartFile(
                "form.xml",
                "form-orig.xml",
                "text/xml",
                false,
                250L,
                "xml string".getBytes(),
                mockIs,
                true);
        files.add(formXml);
        attachments.put("form_" + unit.getFormName(), files);

        Capture<MultipartFile> capturedFile = EasyMock.newCapture();
        Capture<String> fileName = EasyMock.newCapture();

        expect(mockRequest.getParameter("mode")).andReturn("create");
        expect(mockRequest.getParameter("containerId")).andReturn("501");
        expect(mockRequest.getParameter("folderId")).andReturn("502");
        expect(mockFileService.upload(
                capture(fileName),
                eq(unit.getFormName() + "_xml"),
                capture(capturedFile),
                eq(unit.getAuthentication()),
                eq("cmisId"),
                eq("parentType"),
                eq(500L))).andReturn(null);

        replayAll();

        unit.saveAttachments(attachments, "cmisId", "parentType", 500L);

        verifyAll();

        MultipartFile found = capturedFile.getValue();

        assertEquals(found.getSize(), formXml.getSize());

        String actualFilename = fileName.getValue();
        assertTrue(actualFilename.startsWith("form-orig"));
    }
}
