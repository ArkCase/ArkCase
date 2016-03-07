package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by bojan.mickoski on 3/4/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UnlockFileAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private UnlockFileAPIController unit;
    private EcmFileServiceImpl partialMockEcmFileService;
    private FileEventPublisher mockFileEventPublisher;
    private EcmFileDao mockEcmFileDao;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;
    private String mockIpAddress;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new UnlockFileAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();
        partialMockEcmFileService = createMockBuilder(EcmFileServiceImpl.class).addMockedMethod("findById").createMock();
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockFileEventPublisher = createMock(FileEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();
        mockIpAddress = "ip_address";

        mockHttpSession.setAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE, mockIpAddress);

        partialMockEcmFileService.setEcmFileDao(mockEcmFileDao);

        unit.setFileService(partialMockEcmFileService);
        unit.setFileEventPublisher(mockFileEventPublisher);
    }

    @Test
    public void unlockFile() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(1L);

        final String user = "user";

        expect(mockAuthentication.getName()).andReturn(user);
        expect(partialMockEcmFileService.findById(file.getFileId())).andReturn(file);
        expect(mockEcmFileDao.save(file)).andReturn(file);
        mockFileEventPublisher.publishFileUnLockEvent(file, mockAuthentication, mockIpAddress, true);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/file/unlock/{fileId}", file.getId())
                        .principal(mockAuthentication)
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        ObjectMapper om = new ObjectMapper();

        EcmFile ecmFileResult = om.readValue(result.getResponse().getContentAsString(), EcmFile.class);

        assertNotNull(ecmFileResult);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("false", ecmFileResult.getLocked());
    }

    @Test
    public void unlockFileFailed() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(1L);

        final String user = "user";

        expect(mockAuthentication.getName()).andReturn(user);
        expect(partialMockEcmFileService.findById(file.getFileId())).andReturn(null);

        replayAll();

        Exception exception = null;

        try {
            mockMvc.perform(
                    post("/api/latest/service/ecm/file/unlock/{fileId}", file.getId())
                            .principal(mockAuthentication)
                            .session(mockHttpSession)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        }
        catch (Exception e)
        {
            exception = e;
        }

        verifyAll();

        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof AcmObjectNotFoundException);
        assertTrue(exception.getCause().getMessage().contains("File not found"));
    }
}
