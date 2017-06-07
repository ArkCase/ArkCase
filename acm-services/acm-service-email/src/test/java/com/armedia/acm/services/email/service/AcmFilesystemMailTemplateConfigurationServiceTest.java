package com.armedia.acm.services.email.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmFilesystemMailTemplateConfigurationServiceTest
{

    /**
     *
     */
    private static final String TEMPLATES_FOLDER_NAME = "templates";

    private static final String EMAIL_PATTERN = "*";

    private static final String CASE_FILE = "CASE_FILE";

    private static final String COMPLAINT = "COMPLAINT";

    private static final String TEMPLATE_NAME = "testTemplate";

    private static final String SEND_AS_ATTACHMENTS = "sendAsAttachments";

    private static final String SEND_AS_LINKS = "sendAsLinks";

    @Mock
    private FileSystemResource templateConfigurations;

    @Mock
    private File mockedConfigurationsFile;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private AcmFilesystemMailTemplateConfigurationService service;

    @Before
    public void setUp() throws Exception
    {
        service.setTemplateFolderPath(TEMPLATES_FOLDER_NAME);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getTemplateConfigurations()}.
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Test
    public void testGetTemplateConfigurations() throws Exception
    {
        // given
        String fileName = getClass().getClassLoader().getResource("mailTemplatesConfiguration.json").getFile();
        when(templateConfigurations.getInputStream()).thenReturn(new FileInputStream(fileName));

        // when
        List<EmailTemplateConfiguration> configurations = service.getTemplateConfigurations();

        // then
        assertThat(configurations.size(), is(1));

        EmailTemplateConfiguration configuration = configurations.get(0);

        assertThat(configuration.getEmailPattern(), is(EMAIL_PATTERN));
        assertThat(configuration.getSource(), is(EmailSource.MANUAL));
        assertThat(configuration.getTemplateName(), is(TEMPLATE_NAME));
        assertThat(configuration.getObjectTypes().size(), is(2));
        assertThat(configuration.getObjectTypes().contains(CASE_FILE), is(true));
        assertThat(configuration.getObjectTypes().contains(COMPLAINT), is(true));
        assertThat(configuration.getActions().size(), is(2));
        assertThat(configuration.getActions().contains(SEND_AS_ATTACHMENTS), is(true));
        assertThat(configuration.getActions().contains(SEND_AS_LINKS), is(true));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getTemplateConfigurations()}.
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Test
    public void testGetTemplateConfigurations_InputStreamException() throws Exception
    {
        // given
        IOException ioException = new IOException();
        when(templateConfigurations.getInputStream()).thenThrow(ioException);
        when(templateConfigurations.getDescription()).thenReturn("absolute_path_to_resource");
        exception.expect(AcmEmailConfigurationException.class);
        exception.expectMessage(is(
                String.format("Error while reading email templates configuration from %s file.", templateConfigurations.getDescription())));
        exception.expectCause(is(ioException));

        // when
        service.getTemplateConfigurations();

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate_EmptyConfiguration() throws Exception
    {
        // given
        when(templateConfigurations.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {}));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(templateConfigurations.getFile()).thenReturn(mockedConfigurationsFile);
        when(mockedConfigurationsFile.length()).thenReturn(0l);
        when(templateConfigurations.getOutputStream()).thenReturn(outputStream);
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN, Arrays.asList(CASE_FILE, COMPLAINT),
                EmailSource.MANUAL, TEMPLATE_NAME, Arrays.asList(SEND_AS_ATTACHMENTS, SEND_AS_LINKS));
        MockMultipartFile template = spy(new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8")));

        // when
        service.updateEmailTemplate(configuration, template);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmailTemplateConfiguration> configurationList = objectMapper.readValue(outputStream.toByteArray(),
                objectMapper.getTypeFactory().constructParametricType(List.class, EmailTemplateConfiguration.class));

        assertThat(configurationList.size(), is(1));
        EmailTemplateConfiguration storedConfiguration = configurationList.get(0);
        assertThat(storedConfiguration.getEmailPattern(), is(EMAIL_PATTERN));
        assertThat(storedConfiguration.getObjectTypes().size(), is(2));
        assertThat(storedConfiguration.getObjectTypes().contains(CASE_FILE), is(true));
        assertThat(storedConfiguration.getObjectTypes().contains(COMPLAINT), is(true));
        assertThat(configuration.getActions().size(), is(2));
        assertThat(configuration.getActions().contains(SEND_AS_ATTACHMENTS), is(true));
        assertThat(configuration.getActions().contains(SEND_AS_LINKS), is(true));
        assertThat(configuration.getSource(), is(EmailSource.MANUAL));
        assertThat(configuration.getTemplateName(), is(TEMPLATE_NAME));

        ArgumentCaptor<File> templateFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(template).transferTo(templateFileCaptor.capture());

        File templateFileCaptured = templateFileCaptor.getValue();
        assertThat(templateFileCaptured.getPath(), is(System.getProperty("user.home") + File.separator + TEMPLATES_FOLDER_NAME
                + File.separator + configuration.getTemplateName()));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate_UpdateExisting() throws Exception
    {
        // given
        String fileName = getClass().getClassLoader().getResource("mailTemplatesConfiguration.json").getFile();
        when(templateConfigurations.getInputStream()).thenReturn(new FileInputStream(fileName));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(templateConfigurations.getFile()).thenReturn(mockedConfigurationsFile);
        when(mockedConfigurationsFile.length()).thenReturn(0l);
        when(templateConfigurations.getOutputStream()).thenReturn(outputStream);
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN + EMAIL_PATTERN, Arrays.asList(CASE_FILE),
                EmailSource.AUTOMATED, TEMPLATE_NAME, Arrays.asList(SEND_AS_LINKS));
        MockMultipartFile template = spy(new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8")));

        // when
        service.updateEmailTemplate(configuration, template);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmailTemplateConfiguration> configurationList = objectMapper.readValue(outputStream.toByteArray(),
                objectMapper.getTypeFactory().constructParametricType(List.class, EmailTemplateConfiguration.class));

        assertThat(configurationList.size(), is(1));
        EmailTemplateConfiguration storedConfiguration = configurationList.get(0);
        assertThat(storedConfiguration.getEmailPattern(), is(EMAIL_PATTERN + EMAIL_PATTERN));
        assertThat(storedConfiguration.getObjectTypes().size(), is(1));
        assertThat(storedConfiguration.getObjectTypes().contains(CASE_FILE), is(true));
        assertThat(configuration.getActions().size(), is(1));
        assertThat(configuration.getActions().contains(SEND_AS_LINKS), is(true));
        assertThat(configuration.getSource(), is(EmailSource.AUTOMATED));
        assertThat(configuration.getTemplateName(), is(TEMPLATE_NAME));

        ArgumentCaptor<File> templateFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(template).transferTo(templateFileCaptor.capture());

        File templateFileCaptured = templateFileCaptor.getValue();
        assertThat(templateFileCaptured.getPath(), is(System.getProperty("user.home") + File.separator + TEMPLATES_FOLDER_NAME
                + File.separator + configuration.getTemplateName()));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate_AddNew() throws Exception
    {
        // given
        String fileName = getClass().getClassLoader().getResource("mailTemplatesConfiguration.json").getFile();
        when(templateConfigurations.getInputStream()).thenReturn(new FileInputStream(fileName));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(templateConfigurations.getFile()).thenReturn(mockedConfigurationsFile);
        when(mockedConfigurationsFile.length()).thenReturn(0l);
        when(templateConfigurations.getOutputStream()).thenReturn(outputStream);
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN + EMAIL_PATTERN, Arrays.asList(CASE_FILE),
                EmailSource.AUTOMATED, TEMPLATE_NAME + "_2", Arrays.asList(SEND_AS_LINKS));
        MockMultipartFile template = spy(new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8")));

        // when
        service.updateEmailTemplate(configuration, template);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmailTemplateConfiguration> configurationList = objectMapper.readValue(outputStream.toByteArray(),
                objectMapper.getTypeFactory().constructParametricType(List.class, EmailTemplateConfiguration.class));

        assertThat(configurationList.size(), is(2));

        // we don't have to always check all the properties, but since it's a small size collection and simple POJOs, it
        // is doable/acceptable
        assertThat(configurationList,
                containsInAnyOrder(
                        allOf(hasProperty("emailPattern", is(EMAIL_PATTERN + EMAIL_PATTERN)),
                                hasProperty("source", is(EmailSource.AUTOMATED)), hasProperty("templateName", is(TEMPLATE_NAME + "_2")),
                                hasProperty("objectTypes", contains(CASE_FILE)), hasProperty("actions", contains(SEND_AS_LINKS))),
                        allOf(hasProperty("emailPattern", is(EMAIL_PATTERN)), hasProperty("source", is(EmailSource.MANUAL)),
                                hasProperty("templateName", is(TEMPLATE_NAME)),
                                hasProperty("objectTypes", containsInAnyOrder(CASE_FILE, COMPLAINT)),
                                hasProperty("actions", containsInAnyOrder(SEND_AS_LINKS, SEND_AS_ATTACHMENTS)))));

        ArgumentCaptor<File> templateFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(template).transferTo(templateFileCaptor.capture());

        File templateFileCaptured = templateFileCaptor.getValue();
        assertThat(templateFileCaptured.getPath(), is(System.getProperty("user.home") + File.separator + TEMPLATES_FOLDER_NAME
                + File.separator + configuration.getTemplateName()));

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate_ExceptionWhileReading() throws Exception
    {
        // given
        IOException ioException = new IOException();
        when(templateConfigurations.getInputStream()).thenThrow(ioException);
        when(templateConfigurations.getDescription()).thenReturn("absolute_path_to_resource");
        when(templateConfigurations.getFile()).thenReturn(mockedConfigurationsFile);
        when(mockedConfigurationsFile.length()).thenReturn(10l);
        exception.expect(AcmEmailConfigurationException.class);
        exception.expectMessage(is(
                String.format("Error while reading email templates configuration from %s file.", templateConfigurations.getDescription())));
        exception.expectCause(is(ioException));
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN, Arrays.asList(CASE_FILE, COMPLAINT),
                EmailSource.MANUAL, TEMPLATE_NAME, Arrays.asList(SEND_AS_ATTACHMENTS, SEND_AS_LINKS));
        MockMultipartFile template = new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8"));

        // when
        service.updateEmailTemplate(configuration, template);

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmailTemplate_ExceptionWhileWriting() throws Exception
    {
        // given
        String fileName = getClass().getClassLoader().getResource("mailTemplatesConfiguration.json").getFile();
        when(templateConfigurations.getInputStream()).thenReturn(new FileInputStream(fileName));
        IOException ioException = new IOException();
        when(templateConfigurations.getOutputStream()).thenThrow(ioException);
        when(templateConfigurations.getDescription()).thenReturn("absolute_path_to_resource");
        exception.expect(AcmEmailConfigurationException.class);
        exception.expectMessage(is(String.format(
                "Error while updating email template configuration for configuration with %s value for templateName.", TEMPLATE_NAME)));
        exception.expectCause(is(ioException));
        EmailTemplateConfiguration configuration = setupConfiguration(EMAIL_PATTERN, Arrays.asList(CASE_FILE, COMPLAINT),
                EmailSource.MANUAL, TEMPLATE_NAME, Arrays.asList(SEND_AS_ATTACHMENTS, SEND_AS_LINKS));
        MockMultipartFile template = new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, "template".getBytes("UTF-8"));

        // when
        service.updateEmailTemplate(configuration, template);

    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getTemplateCandidates(java.lang.String, java.lang.String, com.armedia.acm.services.email.service.EmailSource, java.util.List)}.
     */
    @Test
    @Ignore
    public void testGetTemplateCandidates()
    {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getTemplate(java.lang.String)}.
     */
    @Test
    @Ignore
    public void testGetTemplate()
    {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#deleteTemplate(java.lang.String)}.
     */
    @Test
    @Ignore
    public void testDeleteTemplate()
    {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getExceptionMapper(com.armedia.acm.services.email.service.AcmEmailServiceException)}.
     */
    @Test
    @Ignore
    public void testGetExceptionMapper()
    {
        fail("Not yet implemented");
    }

    /**
     * @param emailPattern
     * @param objectTypes
     * @param source
     * @param templateName
     * @param actions
     * @return
     */
    private EmailTemplateConfiguration setupConfiguration(String emailPattern, List<String> objectTypes, EmailSource source,
            String templateName, List<String> actions)
    {
        EmailTemplateConfiguration configuration = new EmailTemplateConfiguration();
        configuration.setEmailPattern(emailPattern);
        configuration.setObjectTypes(objectTypes);
        configuration.setSource(source);
        configuration.setTemplateName(templateName);
        configuration.setActions(actions);
        return configuration;
    }

}
