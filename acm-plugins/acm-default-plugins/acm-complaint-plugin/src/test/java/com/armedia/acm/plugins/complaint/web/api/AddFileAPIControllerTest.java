package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-test.xml"
})
public class AddFileAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ComplaintDao mockComplaintDao;
    private EcmFileService mockEcmFileService;
    private MockMultipartFile mockMultipartFile;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private AddFileAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new AddFileAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockComplaintDao = createMock(ComplaintDao.class);
        mockEcmFileService = createMock(EcmFileService.class);

        // first argument below must match the request param name expected by the controller.
        mockMultipartFile = new MockMultipartFile("files[]", "test.txt", "text/plain", "test me".getBytes());

        unit.setComplaintDao(mockComplaintDao);
        unit.setEcmFileService(mockEcmFileService);
    }

    @Test
    public void uploadFile() throws Exception
    {
        String complaintId = "500";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        // use an empty context path since the controller actually gets the servletContext's context path; which
        // we can't set through Spring MVC.
        String contextPath = "";

        Complaint complaint = new Complaint();
        complaint.setComplaintId(Long.valueOf(complaintId));
        complaint.setEcmFolderId("cmisFolderId");
        complaint.setComplaintNumber("complaintNumber");

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockComplaintDao.find(complaint.getComplaintId())).andReturn(complaint);

        expect(mockEcmFileService.upload(
                mockMultipartFile,
                acceptHeader,
                contextPath,
                mockAuthentication,
                complaint.getEcmFolderId(),
                "COMPLAINT",
                complaint.getComplaintId(),
                complaint.getComplaintNumber()
        )).andReturn(null);

        replayAll();

        mockMvc.perform(
                fileUpload(contextPath + "/api/v1/plugin/complaint/file")
                        .file(mockMultipartFile)
                        .contextPath(contextPath)
                        .principal(mockAuthentication)
                        .header("Accept", acceptHeader)
                        .param("complaintId", complaintId))
                .andExpect(status().isOk());

        verifyAll();

    }

    @Test
    public void uploadFile_exception() throws Exception
    {
        String complaintId = "500";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        // use an empty context path since the controller actually gets the servletContext's context path; which
        // we can't set through Spring MVC.
        String contextPath = "";

        Complaint complaint = new Complaint();
        complaint.setComplaintId(Long.valueOf(complaintId));
        complaint.setEcmFolderId("cmisFolderId");
        complaint.setComplaintNumber("complaintNumber");

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockComplaintDao.find(complaint.getComplaintId())).andReturn(complaint);

        expect(mockEcmFileService.upload(
                mockMultipartFile,
                acceptHeader,
                contextPath,
                mockAuthentication,
                complaint.getEcmFolderId(),
                "COMPLAINT",
                complaint.getComplaintId(),
                complaint.getComplaintNumber()
        )).andThrow(new AcmCreateObjectFailedException("test.txt", "testMessage", null));

        replayAll();

        mockMvc.perform(
                fileUpload(contextPath + "/api/v1/plugin/complaint/file")
                        .file(mockMultipartFile)
                        .contextPath(contextPath)
                        .principal(mockAuthentication)
                        .header("Accept", acceptHeader)
                        .param("complaintId", complaintId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }


    @Test
    public void uploadFile_complaintNotFound() throws Exception
    {
        String complaintId = "500";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        // use an empty context path since the controller actually gets the servletContext's context path; which
        // we can't set through Spring MVC.
        String contextPath = "";

         // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockComplaintDao.find(Long.valueOf(complaintId))).andReturn(null);

        replayAll();

        mockMvc.perform(
                fileUpload(contextPath + "/api/v1/plugin/complaint/file")
                        .file(mockMultipartFile)
                        .contextPath(contextPath)
                        .principal(mockAuthentication)
                        .header("Accept", acceptHeader)
                        .param("complaintId", complaintId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }



}
