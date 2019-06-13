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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.plugins.ecm.model.DeleteFileResult;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UploadTempFilesAPIControllerTest extends EasyMockSupport
{

    private EcmFileService mockFileFolderServiceImpl;

    private MockMvc mockMvc;
    private UploadTempFilesAPIController unit = new UploadTempFilesAPIController();
    private Authentication mockAuthentication;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockFileFolderServiceImpl = createMock(EcmFileService.class);
        mockAuthentication = createMock(Authentication.class);
        unit.setFileFolderService(mockFileFolderServiceImpl);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void uploadToTempFolder() throws Exception
    {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/simple.json");
        MultipartFile multipartFile = new MockMultipartFile("file", "json/simple.json", "application/json", inputStream);
        ArrayList<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(multipartFile);
        MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<>();
        multipartFiles.put(multipartFile.getName(), multipartFileList);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileId(123L);
        ecmFile.setFileName("simple.json");
        ArrayList<EcmFile> ecmFileList = new ArrayList<>();
        ecmFileList.add(ecmFile);

        expect(mockAuthentication.getName()).andReturn("user").times(2);
        expect(mockFileFolderServiceImpl.saveFilesToTempDirectory(multipartFiles)).andReturn(ecmFileList);
        replayAll();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/api/v1/plugin/ecm/temp/upload")
                        .file((MockMultipartFile) multipartFile)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
        closeInputStream(inputStream);
        log.info("Results: " + result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void deleteTempFile() throws Exception
    {

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileId(104L);
        ecmFile.setFileName("simple.json");
        ArrayList<EcmFile> ecmFileList = new ArrayList<>();
        ecmFileList.add(ecmFile);

        expect(mockAuthentication.getName()).andReturn("user").times(2);
        expect(mockFileFolderServiceImpl.deleteTempFile(ecmFile.getFileName())).andReturn(true);
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/ecm/temp/delete")
                        .param("fileName", ecmFile.getFileName())
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        log.info("Results: " + result.getResponse().getContentAsString());
        ObjectMapper objectMapper = new ObjectMapper();
        DeleteFileResult deleteFileResult = objectMapper.readValue(result.getResponse().getContentAsString(), DeleteFileResult.class);
        assertTrue(deleteFileResult.isSuccess());
    }

    private void closeInputStream(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                log.warn("Cannot close input stream in integration test");
            }

        }
    }
}
