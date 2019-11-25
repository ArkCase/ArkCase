package com.armedia.acm.compressfolder.web.api;

/*-
 * #%L
 * ACM Service: Folder Compressing Service
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
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.compressfolder.DefaultFolderCompressor;
import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.compressfolder.model.FileFolderNode;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 16, 2016
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-compress-folder-service-test.xml" })
public class FolderCompressorAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private File mockZipFile;

    private FolderCompressor mockedFolderCompressor;

    private FolderCompressorAPIController controller;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Authentication mockAuthentication;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockedFolderCompressor = createMock(DefaultFolderCompressor.class);

        controller = new FolderCompressorAPIController();
        controller.setFolderCompressor(mockedFolderCompressor);

        mockHttpSession = new MockHttpSession();
        String ipAddress = "ip-address";
        mockHttpSession.setAttribute("acm_ip_address", ipAddress);
        mockZipFile = new File(getClass().getResource("/acm-101-ROOT.zip").getFile());
        assertNotNull("Unable to load test zip file.", mockZipFile.getPath());
        mockAuthentication = createMock(Authentication.class);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    /**
     * Test method for {@link com.armedia.acm.compressfolder.web.api.FolderCompressorAPIController#compressFolder(long)}
     * .
     *
     * @throws Exception
     */
    @Test
    public void testCompressFolder() throws Exception
    {
        long folderId = 101l;
        String fileName = "path/tocompressed_folder.zip";

        expect(mockedFolderCompressor.compressFolder(folderId)).andReturn(fileName);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/compressor/{folderId}", folderId).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String responseString = result.getResponse().getContentAsString();

        log.info("results: " + responseString);

        ObjectMapper objectMapper = new ObjectMapper();

        FolderCompressorResponse response = objectMapper.readValue(responseString,
                objectMapper.getTypeFactory().constructType(FolderCompressorResponse.class));

        assertEquals(response.getFileName(), fileName);

    }

    @Test
    public void testCompressFolderWithException() throws Exception
    {

        long folderId = 101l;

        expect(mockedFolderCompressor.compressFolder(folderId)).andThrow(new AcmFolderException(folderId));

        replayAll();

        MvcResult response = mockMvc.perform(
                get("/api/v1/service/compressor/{folderId}", folderId).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(response.getResponse().getStatus(), 500);

    }

    @Test
    public void testDownloadFolder() throws Exception
    {
        long folderId = 101l;
        String fileName = mockZipFile.getPath();

        expect(mockedFolderCompressor.compressFolder(folderId)).andReturn(fileName);

        replayAll();

        MvcResult response = mockMvc.perform(
                get("/api/v1/service/compressor/download/{folderId}", folderId).session(mockHttpSession))
                .andReturn();

        verifyAll();

        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
        assertEquals(response.getResponse().getContentType(), "application/zip");
        assertEquals(response.getResponse().getHeader("Content-Disposition"), "attachment; filename=\"acm-101-ROOT.zip\"");

    }

    @Test
    public void testDownloadSelectedFolderFiles() throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();

        Long rootFolderId = 101L;
        FileFolderNode file1 = new FileFolderNode();
        FileFolderNode file2 = new FileFolderNode();
        FileFolderNode rootFolder = new FileFolderNode();

        file1.setFolder(false);
        file1.setObjectId(111L);

        file2.setFolder(false);
        file2.setObjectId(222L);

        rootFolder.setFolder(true);
        rootFolder.setObjectId(rootFolderId);

        List<FileFolderNode> selectedFoldersFiles = new ArrayList<>(Arrays.asList(file1, file2, rootFolder));

        CompressNode compressNode = new CompressNode();
        compressNode.setRootFolderId(rootFolderId);
        compressNode.setSelectedNodes(selectedFoldersFiles);

        String fileName = mockZipFile.getPath();

        expect(mockedFolderCompressor.compressFolder(isA(CompressNode.class), isA(Authentication.class), isA(String.class)))
                .andReturn(fileName);

        expect(mockAuthentication.getName()).andReturn("ann-acm@armedia.com");
        replayAll();

        MvcResult response = mockMvc.perform(
                post("/api/v1/service/compressor/download")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(objectMapper.writeValueAsString(compressNode))
                        .principal(mockAuthentication)
                        .accept("application/zip")
                        .session(mockHttpSession))
                .andReturn();

        verifyAll();

        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }
}
