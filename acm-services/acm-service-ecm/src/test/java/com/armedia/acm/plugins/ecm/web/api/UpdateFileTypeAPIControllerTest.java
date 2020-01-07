package com.armedia.acm.plugins.ecm.web.api;

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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UpdateFileTypeAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private UpdateFileTypeAPIController unit;
    private EcmFileServiceImpl ecmFileService;
    private EcmFileDao mockEcmFileDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new UpdateFileTypeAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();
        ecmFileService = new EcmFileServiceImpl();
        mockEcmFileDao = createMock(EcmFileDao.class);
        ecmFileService.setEcmFileDao(mockEcmFileDao);
        unit.setEcmFileService(ecmFileService);
        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void updateFileType() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(100L);
        file.setFileType("file_type");

        String newFileType = "new_file_type";

        Capture<EcmFile> saved = new Capture<>();
        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockEcmFileDao.find(file.getId())).andReturn(file);
        expect(mockEcmFileDao.save(capture(saved))).andReturn(file);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/file/{fileId}/type/{fileType}", file.getId(), newFileType)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        LOG.info("Results: {}", result.getResponse().getContentAsString());

        ObjectMapper objectMapper = new ObjectMapper();
        EcmFile resultEcmFile = objectMapper.readValue(result.getResponse().getContentAsString(), EcmFile.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(newFileType, saved.getValue().getFileType());
        assertEquals(newFileType, resultEcmFile.getFileType());
    }

    @Test
    public void updateFileTypeError() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(100L);
        file.setFileType("file_type");

        String newFileType = "new_file_type";

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockEcmFileDao.find(file.getId())).andReturn(null);

        replayAll();

        Exception exception = null;
        try
        {
            MvcResult result = mockMvc.perform(
                    post("/api/latest/service/ecm/file/{fileId}/type/{fileType}", file.getId(), newFileType)
                            .principal(mockAuthentication)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        }
        catch (Exception e)
        {
            exception = e;
        }

        verifyAll();

        assertNotNull(exception);
        assertTrue("", exception.getCause() instanceof AcmObjectNotFoundException);
    }

    @Test
    public void bulkUpdateFileType() throws Exception
    {
        EcmFile file1 = new EcmFile();
        file1.setFileId(100L);
        file1.setFileType("file_type");

        EcmFile file2 = new EcmFile();
        file2.setFileId(101L);
        file2.setFileType("file_type");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(Arrays.asList(100, 101));

        String newFileType = "new_file_type";

        Capture<EcmFile> saved1 = EasyMock.newCapture();
        Capture<EcmFile> saved2 = EasyMock.newCapture();
        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockEcmFileDao.find(file1.getId())).andReturn(file1);
        expect(mockEcmFileDao.find(file2.getId())).andReturn(file2);
        expect(mockEcmFileDao.save(capture(saved1))).andReturn(file1);
        expect(mockEcmFileDao.save(capture(saved2))).andReturn(file2);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/file/bulk/type/{fileType}", newFileType)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();

        verifyAll();

        LOG.info("Results: {}", result.getResponse().getContentAsString());

        List<EcmFile> resultList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<EcmFile>>()
        {
        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(resultList);
        assertTrue(resultList.size() == 2);
        assertEquals(newFileType, saved1.getValue().getFileType());
        assertEquals(newFileType, saved2.getValue().getFileType());
        assertEquals(newFileType, resultList.get(0).getFileType());
        assertEquals(newFileType, resultList.get(1).getFileType());
    }
}
