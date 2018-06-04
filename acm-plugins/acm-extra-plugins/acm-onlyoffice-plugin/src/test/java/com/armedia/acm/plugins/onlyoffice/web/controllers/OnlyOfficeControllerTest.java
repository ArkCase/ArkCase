package com.armedia.acm.plugins.onlyoffice.web.controllers;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.plugins.onlyoffice.model.CallbackResponseSuccess;
import com.armedia.acm.plugins.onlyoffice.model.callback.Action;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.model.callback.History;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;

import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-onlyoffice-plugin-test.xml",
})
public class OnlyOfficeControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private Authentication mockAuthentication;
    private CallbackService mockCallbackService;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
    @Autowired
    private OnlyOfficeController onlyOfficeController;

    @Before
    public void setUp()
    {
        mockAuthentication = createMock(Authentication.class);
        mockCallbackService = createMock(CallbackService.class);
        mockHttpSession = new MockHttpSession();
        onlyOfficeController.setCallbackService(mockCallbackService);
        mockMvc = MockMvcBuilders.standaloneSetup(onlyOfficeController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testCallbackRequestStatus2() throws Exception
    {

        EasyMock.expect(mockAuthentication.getName()).andReturn("ann-acm@armedia.com");
        Capture<CallBackData> callBackDataCapture = EasyMock.newCapture();

        EasyMock.expect(mockCallbackService.handleCallback(EasyMock.capture(callBackDataCapture), EasyMock.eq(mockAuthentication)))
                .andReturn(new CallbackResponseSuccess());
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/onlyoffice/callback")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(getFileAsBytes("test_request/callback_request_status_2.json"))
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();
        verifyAll();
        CallBackData callBackData = callBackDataCapture.getValue();

        assertEquals(Integer.valueOf(2), callBackData.getStatus());

        List<Action> actions = callBackData.getActions();
        assertTrue(actions.size() > 0);
        assertEquals("ann-acm@armedia.com", actions.get(0).getUserid());
        assertEquals(Integer.valueOf(0), actions.get(0).getType());

        History history = callBackData.getHistory();
        assertNotNull(history);
        assertTrue(history.getChanges().size() == 3);
        assertEquals("2018-05-31 15:00:33", history.getChanges().get(0).getCreated());
        assertEquals("ian-acm@armedia.com", history.getChanges().get(0).getUser().getId());
        assertEquals("Ian Investigator", history.getChanges().get(0).getUser().getName());

        assertEquals(
                "http://acm-onlyoffice/ds-vpath/cache/files/114-1.0_5308/changes.zip/changes.zip?md5=1d9vkskCn0M3p9Bo9kGzmQ==&expires=1527779861&disposition=attachment&ooname=output.zip",
                callBackData.getChangesUrl());
    }

    @Test
    public void testCallbackRequestStatus5Unknows() throws Exception
    {
        EasyMock.expect(mockAuthentication.getName()).andReturn("ann-acm@armedia.com");
        Capture<CallBackData> callBackDataCapture = EasyMock.newCapture();

        EasyMock.expect(mockCallbackService.handleCallback(EasyMock.capture(callBackDataCapture), EasyMock.eq(mockAuthentication)))
                .andReturn(new CallbackResponseSuccess());
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/onlyoffice/callback")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(getFileAsBytes("test_request/callback_request_status_5.json"))
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();
        verifyAll();
        CallBackData callBackData = callBackDataCapture.getValue();
        assertEquals("{\"error\":1}", result.getResponse().getContentAsString());
    }

    private byte[] getFileAsBytes(String path) throws IOException
    {
        Resource resource = new ClassPathResource(path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOUtils.copy(resource.getInputStream(), os);
        return os.toByteArray();
    }
}