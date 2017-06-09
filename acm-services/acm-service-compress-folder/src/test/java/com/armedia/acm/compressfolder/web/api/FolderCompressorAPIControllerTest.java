package com.armedia.acm.compressfolder.web.api;

import com.armedia.acm.compressfolder.DefaultFolderCompressor;
import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.compressfolder.FolderCompressorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.File;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 16, 2016
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-compress-folder-service-test.xml"})
public class FolderCompressorAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private File mockZipFile;

    private FolderCompressor mockedFolderCompressor;

    private FolderCompressorAPIController controller;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockedFolderCompressor = createMock(DefaultFolderCompressor.class);

        controller = new FolderCompressorAPIController();
        controller.setFolderCompressor(mockedFolderCompressor);

        mockHttpSession = new MockHttpSession();
        mockZipFile = new File(getClass().getResource("/acm-101-ROOT.zip").getFile());
        assertNotNull("Unable to load test zip file.", mockZipFile.getPath());

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

        expect(mockedFolderCompressor.compressFolder(folderId)).andThrow(new FolderCompressorException(folderId));

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
}
