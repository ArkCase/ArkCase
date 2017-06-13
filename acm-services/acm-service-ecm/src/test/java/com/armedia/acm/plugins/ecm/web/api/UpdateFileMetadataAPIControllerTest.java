package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by manoj.dhungana on 04/10/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UpdateFileMetadataAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private UpdateFileMetadataAPIController unit;
    private EcmFileService mockEcmFileService;
    private Authentication mockAuthentication;
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new UpdateFileMetadataAPIController();

        mockEcmFileService = createMock(EcmFileService.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        unit.setEcmFileService(mockEcmFileService);
        unit.setApplicationEventPublisher(mockApplicationEventPublisher);
        mockAuthentication = createMock(Authentication.class);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
    }

    @Test
    public void updateFile() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        EcmFile out = new EcmFile();
        out.setFileId(in.getFileId());
        out.setStatus(in.getStatus());
        out.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();
        Capture<EcmFileUpdatedEvent> capturedEvent = Capture.newInstance();

        expect(mockEcmFileService.updateFile(capture(saved))).andReturn(out);
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));
        expectLastCall();

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/file/metadata/{fileId}", "100")
                        .content(new ObjectMapper().writeValueAsString(in))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();


        verifyAll();

        String returned = result.getResponse().getContentAsString();
        LOG.info("results: " + returned);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        assertEquals(in.getFileId(), saved.getValue().getFileId());
        assertEquals(in.getStatus(), saved.getValue().getStatus());

        EcmFileUpdatedEvent event = capturedEvent.getValue();
        assertEquals(in.getFileId(), event.getObjectId());
        assertEquals("FILE", event.getObjectType());
        assertTrue(event.isSucceeded());
    }


    @Test
    public void updateFile_exception() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();

        expect(mockEcmFileService.updateFile(capture(saved))).andReturn(null);
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();

        try
        {
            mockMvc.perform(
                    post("/api/latest/service/ecm/file/metadata/{fileId}", "100")
                            .content(new ObjectMapper().writeValueAsString(in))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockAuthentication))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        } catch (Exception e)
        {
            // do nothing, exception expected
        }

        verifyAll();
    }
}
