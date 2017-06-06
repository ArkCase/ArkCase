package com.armedia.acm.services.email.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
public class AcmFilesystemMailTemplateConfigurationService implements AcmMailTemplateConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource templateConfigurations;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateConfigurations()
     */
    @Override
    public List<EmailTemplateConfiguration> getTemplateConfigurations() throws AcmEmailConfigurationException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            List<EmailTemplateConfiguration> readConfigurationList = objectMapper.readValue(templateConfigurations.getInputStream(),
                    objectMapper.getTypeFactory().constructParametricType(List.class, EmailTemplateConfiguration.class));
            return readConfigurationList;
        } catch (IOException e)
        {
            log.warn("Error while reading email templates configuration from {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationException(String.format("Error while reading email templates configuration from %s file.",
                    templateConfigurations.getDescription()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.
     * services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)
     */
    @Override
    public void updateEmailTemplate(EmailTemplateConfiguration templateData, MultipartFile template)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateCandidates(java.lang.
     * String, java.lang.String, com.armedia.acm.services.email.service.EmailSource, java.util.List)
     */
    @Override
    public List<EmailTemplateConfiguration> getTemplateCandidates(String email, String objectType, EmailSource source, List<String> actions)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String templateName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#deleteTemplate(java.lang.String)
     */
    @Override
    public void deleteTemplate(String templateName)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getExceptionMapper(com.armedia.acm.
     * services.email.service.AcmEmailServiceException)
     */
    @Override
    public <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(
            AcmEmailServiceException e)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param templateConfigurations
     *            the templateConfigurations to set
     */
    public void setTemplateConfigurations(Resource templateConfigurations)
    {
        this.templateConfigurations = templateConfigurations;
    }

}
