package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.model.DeleteFileResult;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    private Logger log = LoggerFactory.getLogger(getClass());

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
            } catch (IOException e)
            {
                log.warn("Cannot close input stream in integration test");
            }

        }
    }
}
