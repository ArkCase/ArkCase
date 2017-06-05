package com.armedia.acm.services.email.config.web.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.email.service.AcmMailService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 5, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/spring-email-service-api-test.xml" })
public class AcmMailConfigurationServiceAPIControllerTest
{

    private MockMvc mockMvc;

    @Mock
    private AcmMailService mailService;

    private AcmMailConfigurationServiceAPIController controller;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        controller = new AcmMailConfigurationServiceAPIController();
        controller.setMailService(mailService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailConfigurationServiceAPIController#getTemplateConfigurations()}.
     *
     * @throws Exception
     */
    @Test
    public void testGetTemplateConfigurations() throws Exception
    {

        MvcResult result = mockMvc
                .perform(get("/api/v1/service/email/configure/template").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String responseString = result.getResponse().getContentAsString();

        log.info("results: " + responseString);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailConfigurationServiceAPIController#updateEmailTemplate(javax.servlet.http.HttpSession, org.springframework.security.core.Authentication, com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     */
    @Test
    public void testUpdateEmailTemplate()
    {
        fail("Not yet implemented");
    }

}
