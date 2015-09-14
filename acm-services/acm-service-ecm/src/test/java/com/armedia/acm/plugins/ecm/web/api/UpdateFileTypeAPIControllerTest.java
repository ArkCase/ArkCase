package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UpdateFileTypeAPIControllerTest  extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
}
