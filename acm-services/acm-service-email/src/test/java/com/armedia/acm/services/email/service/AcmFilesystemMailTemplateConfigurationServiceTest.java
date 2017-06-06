package com.armedia.acm.services.email.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmFilesystemMailTemplateConfigurationServiceTest
{

    private static final String EMAIL_PATTERN = "*";

    private static final String CASE_FILE = "CASE_FILE";

    private static final String COMPLAINT = "COMPLAINT";

    private static final String TEMPLATE_NAME = "testTemplate";

    private static final String SEND_AS_ATTACHMENTS = "sendAsAttachments";

    private static final String SEND_AS_LINKS = "sendAsLinks";

    @Mock
    private Resource templateConfigurations;

    @InjectMocks
    private AcmFilesystemMailTemplateConfigurationService service;

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
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)}.
     */
    @Test
    @Ignore
    public void testUpdateEmailTemplate()
    {
        fail("Not yet implemented");
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

}
