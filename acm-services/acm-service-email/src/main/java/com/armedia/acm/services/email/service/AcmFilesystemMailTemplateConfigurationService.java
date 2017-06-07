package com.armedia.acm.services.email.service;

import static java.util.regex.Pattern.matches;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
public class AcmFilesystemMailTemplateConfigurationService implements AcmMailTemplateConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private FileSystemResource templateConfigurations;

    private String templateFolderPath;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateConfigurations()
     */
    @Override
    public List<EmailTemplateConfiguration> getTemplateConfigurations() throws AcmEmailConfigurationException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = templateConfigurations.getInputStream())
        {
            List<EmailTemplateConfiguration> readConfigurationList = objectMapper.readValue(is,
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
    public void updateEmailTemplate(EmailTemplateConfiguration templateData, MultipartFile template) throws AcmEmailConfigurationException
    {
        File templateFolder = getTemplateFolder();

        List<EmailTemplateConfiguration> configurations;
        try
        {
            configurations = getTemplateConfigurations();
        } catch (AcmEmailConfigurationException e)
        {
            if (templateConfigurations.getFile().length() == 0)
            {
                configurations = new ArrayList<>();
            } else
            {
                // just re-throw here, if the error was during reading of the configuration it is already logged in the
                // 'getTemplateConfigurations()' method.
                throw e;
            }
        }

        Optional<EmailTemplateConfiguration> existingConfiguration = configurations.stream()
                .filter(c -> c.getTemplateName().equals(templateData.getTemplateName())).findFirst();
        if (existingConfiguration.isPresent())
        {
            try
            {
                BeanUtils.copyProperties(existingConfiguration.get(), templateData);
            } catch (IllegalAccessException | InvocationTargetException e)
            {
                log.warn("Error while updating email template configuration for configuration with {} value for templateName.",
                        templateData.getTemplateName(), e);
                throw new AcmEmailConfigurationException(
                        String.format("Error while updating email template configuration for configuration with %s value for templateName.",
                                templateData.getTemplateName()),
                        e);
            }
        } else
        {
            configurations.add(templateData);
        }

        ObjectMapper mapper = new ObjectMapper();

        try (OutputStream os = templateConfigurations.getOutputStream())
        {
            mapper.writeValue(os, configurations);
            File templateFile = new File(templateFolder, templateData.getTemplateName());
            template.transferTo(templateFile);
        } catch (IOException e)
        {
            log.warn("Error while updating email template configuration for configuration with {} value for templateName.",
                    templateData.getTemplateName(), e);
            throw new AcmEmailConfigurationException(
                    String.format("Error while updating email template configuration for configuration with %s value for templateName.",
                            templateData.getTemplateName()),
                    e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateCandidates(java.lang.
     * String, java.lang.String, com.armedia.acm.services.email.service.EmailSource, java.util.List)
     */
    @Override
    public List<EmailTemplateConfiguration> getMatchingTemplates(String email, String objectType, EmailSource source, String action)
            throws AcmEmailConfigurationException
    {
        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations();
        Stream<EmailTemplateConfiguration> filteredConfigurations = configurations.stream()
                .filter(c -> c.getObjectTypes().contains(objectType)).filter(c -> c.getActions().contains(action))
                .filter(c -> c.getSource().equals(source)).filter(c -> matches(c.getEmailPattern(), email));

        return filteredConfigurations.collect(Collectors.toList());
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
    public <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return
     */
    private File getTemplateFolder()
    {
        return new File(System.getProperty("user.home") + File.separator + templateFolderPath);
    }

    /**
     * @param templateConfigurations
     *            the templateConfigurations to set
     */
    public void setTemplateConfigurations(FileSystemResource templateConfigurations)
    {
        this.templateConfigurations = templateConfigurations;
    }

    /**
     * @param templateFolderPath
     *            the templateFolderPath to set
     */
    public void setTemplateFolderPath(String templateFolderPath)
    {
        this.templateFolderPath = templateFolderPath;
    }

}
