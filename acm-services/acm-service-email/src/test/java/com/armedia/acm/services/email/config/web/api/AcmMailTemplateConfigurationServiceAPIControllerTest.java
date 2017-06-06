package com.armedia.acm.services.email.config.web.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.EmailSource;
import com.armedia.acm.services.email.service.EmailTemplateConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 5, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/spring-email-service-api-test.xml" })
public class AcmMailTemplateConfigurationServiceAPIControllerTest
{

    private static final String CONTROLLER_PATH = "/api/v1/service/email/configure/template";

    private static final String EMAIL_PATTERN = "*";

    private static final String CASE_FILE = "CASE_FILE";

    private static final String COMPLAINT = "COMPLAINT";

    private static final String TEMPLATE_NAME = "testTemplate";

    private static final String SEND_AS_ATTACHMENTS = "sendAsAttachments";

    private static final String SEND_AS_LINKS = "sendAsLinks";

    private MockMvc mockMvc;

    @Mock
    private AcmMailTemplateConfigurationService mailService;

    @InjectMocks
    private AcmMailTemplateConfigurationServiceAPIController controller;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailTemplateConfigurationServiceAPIController#getTemplateConfigurations()}.
     *
     * @throws Exception
     */
    @Test
    public void testGetTemplateConfigurations() throws Exception
    {
        // given
        List<EmailTemplateConfiguration> configurationList = new ArrayList<>();
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN, Arrays.asList(CASE_FILE, COMPLAINT),
                EmailSource.MANUAL, TEMPLATE_NAME, Arrays.asList(SEND_AS_ATTACHMENTS, SEND_AS_LINKS));
        configurationList.add(configuration);

        when(mailService.getTemplateConfigurations()).thenReturn(configurationList);

        // when
        MvcResult result = mockMvc.perform(get(CONTROLLER_PATH).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String responseString = result.getResponse().getContentAsString();

        log.info("results: " + responseString);

        ObjectMapper objectMapper = new ObjectMapper();

        List<EmailTemplateConfiguration> readConfigurationList = objectMapper.readValue(responseString,
                objectMapper.getTypeFactory().constructParametricType(List.class, EmailTemplateConfiguration.class));

        assertThat(readConfigurationList.size(), is(1));

        EmailTemplateConfiguration readConfiguration = readConfigurationList.get(0);

        assertThat(readConfiguration.getEmailPattern(), is(EMAIL_PATTERN));
        assertThat(readConfiguration.getSource(), is(EmailSource.MANUAL));
        assertThat(readConfiguration.getTemplateName(), is(TEMPLATE_NAME));
        assertThat(readConfiguration.getObjectTypes().size(), is(2));
        assertThat(readConfiguration.getObjectTypes().contains(CASE_FILE), is(true));
        assertThat(readConfiguration.getObjectTypes().contains(COMPLAINT), is(true));
        assertThat(readConfiguration.getActions().size(), is(2));
        assertThat(readConfiguration.getActions().contains(SEND_AS_ATTACHMENTS), is(true));
        assertThat(readConfiguration.getActions().contains(SEND_AS_LINKS), is(true));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailTemplateConfigurationServiceAPIController#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate() throws Exception
    {
        // given
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN, Arrays.asList(CASE_FILE, COMPLAINT),
                EmailSource.MANUAL, TEMPLATE_NAME, Arrays.asList(SEND_AS_ATTACHMENTS, SEND_AS_LINKS));
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(configuration);
        MockMultipartFile templateConfiguration = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE,
                content.getBytes("UTF-8"));
        MockMultipartFile template = new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8"));

        // when
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(CONTROLLER_PATH);
        builder.with(new RequestPostProcessor()
        {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request)
            {
                request.setMethod("PUT");
                return request;
            }
        });

        MvcResult result = mockMvc.perform(builder.file(templateConfiguration).file(template)).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        ArgumentCaptor<EmailTemplateConfiguration> configurationCaptor = ArgumentCaptor.forClass(EmailTemplateConfiguration.class);
        ArgumentCaptor<MultipartFile> templateCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        verify(mailService).updateEmailTemplate(configurationCaptor.capture(), templateCaptor.capture());

        EmailTemplateConfiguration configurationCaptured = configurationCaptor.getValue();
        assertEquals(configuration.getEmailPattern(), configurationCaptured.getEmailPattern());
        assertThat(configurationCaptured.getSource(), is(EmailSource.MANUAL));
        assertThat(configurationCaptured.getTemplateName(), is(TEMPLATE_NAME));
        assertThat(configurationCaptured.getObjectTypes().size(), is(2));
        assertThat(configurationCaptured.getObjectTypes().contains(CASE_FILE), is(true));
        assertThat(configurationCaptured.getObjectTypes().contains(COMPLAINT), is(true));
        assertThat(configurationCaptured.getActions().size(), is(2));
        assertThat(configurationCaptured.getActions().contains(SEND_AS_ATTACHMENTS), is(true));
        assertThat(configurationCaptured.getActions().contains(SEND_AS_LINKS), is(true));

        MultipartFile templateCaptured = templateCaptor.getValue();
        assertThat(templateCaptured.getContentType(), is(MediaType.TEXT_PLAIN_VALUE));
        assertEquals("template", new String(templateCaptured.getBytes()));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailTemplateConfigurationServiceAPIController#deleteEmailTemplate(String)}.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteEmailTemplate() throws Exception
    {
        // when
        MvcResult result = mockMvc.perform(delete(CONTROLLER_PATH + "/template_name")).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        ArgumentCaptor<String> templateNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).deleteTemplate(templateNameCaptor.capture());
        assertThat(templateNameCaptor.getValue(), is("template_name"));
    }

    /**
     * @param emailPattern
     *            TODO
     * @param objectTypes
     *            TODO
     * @param source
     *            TODO
     * @param templateName
     *            TODO
     * @param actions
     *            TODO
     * @return
     */
    private EmailTemplateConfiguration setupConfiguration(String emailPattern, List<String> objectTypes, EmailSource source,
            String templateName, List<String> actions)
    {
        EmailTemplateConfiguration configuration = new EmailTemplateConfiguration();
        configuration.setEmailPattern(emailPattern);
        configuration.setObjectTypes(objectTypes);
        configuration.setSource(EmailSource.MANUAL);
        configuration.setTemplateName(templateName);
        configuration.setActions(actions);
        return configuration;
    }

}
