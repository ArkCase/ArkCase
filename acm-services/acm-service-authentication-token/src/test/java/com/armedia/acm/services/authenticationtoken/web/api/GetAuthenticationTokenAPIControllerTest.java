package com.armedia.acm.services.authenticationtoken.web.api;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-authenticationtoken-test.xml"
})
public class GetAuthenticationTokenAPIControllerTest extends EasyMockSupport
{
    private AuthenticationTokenService mockAuthenticationTokenService;
    private MockMvc mockMvc;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private GetAuthenticationTokenAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new GetAuthenticationTokenAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockAuthenticationTokenService = createMock(AuthenticationTokenService.class);

        unit.setAuthenticationTokenService(mockAuthenticationTokenService);
    }

    @Test
    public void authenticationtoken() throws Exception
    {
        String token = "token";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockAuthenticationTokenService.getTokenForAuthentication(mockAuthentication)).andReturn(token);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/authenticationtoken")
                        .principal(mockAuthentication)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        String found = result.getResponse().getContentAsString();

        assertEquals(token, found);
    }

}
