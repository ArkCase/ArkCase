package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ecm-plugin-test.xml",
        "/spring/spring-web-acm-web.xml"
})
public class FileDownloadAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private FileDownloadAPIController unit;

    private EcmFileDao mockFileDao;
    private Authentication mockAuthentication;
    private ApplicationEventPublisher mockEventPublisher;
    private MuleClient mockMuleClient;
    private MuleMessage mockMuleMessage;
    private ContentStream mockContentStream;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockFileDao = createMock(EcmFileDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockEventPublisher = createMock(ApplicationEventPublisher.class);
        mockMuleClient = createMock(MuleClient.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockContentStream = createMock(ContentStream.class);

        unit = new FileDownloadAPIController();

        unit.setFileDao(mockFileDao);
        unit.setApplicationEventPublisher(mockEventPublisher);
        unit.setMuleClient(mockMuleClient);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void downloadFileById_successful() throws Exception
    {
        Long ecmFileId = 500L;
        String user = "user";
        String cmisId = "cmisId";
        String mimeType = "mimeType";
        String fileName = "fileName";


        Resource log4j = new ClassPathResource("log4j.properties");
        long log4jsize = log4j.getFile().length();
        InputStream log4jis = log4j.getInputStream();


        EcmFile fromDb = new EcmFile();
        fromDb.setFileId(ecmFileId);
        fromDb.setEcmFileId(cmisId);

        Capture<EcmFileDownloadedEvent> capturedEvent = new Capture<>();

        expect(mockAuthentication.getName()).andReturn(user).atLeastOnce();
        expect(mockFileDao.find(EcmFile.class, ecmFileId)).andReturn(fromDb);
        expect(mockMuleClient.send("vm://downloadFileFlow.in", cmisId, null)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(mockContentStream).atLeastOnce();
        expect(mockContentStream.getMimeType()).andReturn(mimeType);
        expect(mockContentStream.getFileName()).andReturn(fileName);
        expect(mockContentStream.getStream()).andReturn(log4jis);
        mockEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/ecm/download/byId/{ecmFileId}", ecmFileId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();

        verifyAll();

        EcmFileDownloadedEvent foundEvent = capturedEvent.getValue();
        assertEquals(ecmFileId, foundEvent.getObjectId());
        assertEquals("FILE", foundEvent.getObjectType());
        assertEquals(user, foundEvent.getUserId());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(mimeType));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        assertEquals(log4jsize, returned.length());
    }

    @Test
    public void downloadFileById_fileNotFoundInDb() throws Exception
    {
        Long ecmFileId = 500L;
        String user = "user";

        expect(mockAuthentication.getName()).andReturn(user).atLeastOnce();
        expect(mockFileDao.find(EcmFile.class, ecmFileId)).andReturn(null);


        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/ecm/download/byId/{ecmFileId}", ecmFileId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
    }
}
