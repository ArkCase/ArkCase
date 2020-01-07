package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static java.util.regex.Pattern.matches;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.email.model.EmailTemplateValidationResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
public class AcmFilesystemMailTemplateConfigurationService implements AcmMailTemplateConfigurationService
{
    private ObjectConverter objectConverter;
    private Logger log = LogManager.getLogger(getClass());
    private Resource templateConfigurations;
    private String templateFolderPath;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateConfigurations()
     */
    @Override
    public List<EmailTemplateConfiguration> getTemplateConfigurations() throws AcmEmailConfigurationException
    {
        Lock readLock = lock.readLock();
        readLock.lock();
        try (InputStream is = templateConfigurations.getInputStream())
        {
            List<EmailTemplateConfiguration> readConfigurationList = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(IOUtils.toString(is, StandardCharsets.UTF_8), List.class, EmailTemplateConfiguration.class);
            if (readConfigurationList == null)
            {
                log.warn("Error while deserializing email templates configuration from {} file.", templateConfigurations.getDescription(),
                        null);
                throw new AcmEmailConfigurationJsonException(
                        String.format("Error while deserializing email templates configuration from %s file.",
                                templateConfigurations.getDescription()),
                        null);

            }
            return readConfigurationList;
        }
        catch (IOException e)
        {
            log.warn("Error while reading email templates configuration from {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationIOException(String.format("Error while reading email templates configuration from %s file.",
                    templateConfigurations.getDescription()), e);
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * @param templateConfigurations
     *            the templateConfigurations to set
     */
    public void setTemplateConfigurations(Resource templateConfigurations)
    {
        this.templateConfigurations = templateConfigurations;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#validateEmailTemplate()
     */
    @Override
    public EmailTemplateValidationResponse validateEmailTemplate(EmailTemplateConfiguration templateData)
            throws AcmEmailConfigurationException
    {

        List<EmailTemplateConfiguration> readConfigurationList = getTemplateConfigurations();

        Map<String, Map<String, List<String>>> templateConfigurationsMap = getTemplateConfigurationsMap(templateData,
                readConfigurationList);

        EmailTemplateValidationResponse response = getCollideConfiguration(templateData, templateConfigurationsMap);

        return response;
    }

    /**
     * @param templateData
     * @param configurationList
     * @return
     */
    protected Map<String, Map<String, List<String>>> getTemplateConfigurationsMap(EmailTemplateConfiguration templateData,
            List<EmailTemplateConfiguration> configurationList)
    {
        // Map<objectType, Map<action, List<emailPattern>>>
        Map<String, Map<String, List<String>>> templateConfigurations = new HashMap<>();
        for (EmailTemplateConfiguration configuration : configurationList)
        {
            if (templateData.getTemplateName() != null && !templateData.getTemplateName().equals(configuration.getTemplateName()))
            {
                for (String objectType : configuration.getObjectTypes())
                {
                    Map<String, List<String>> actionMap = templateConfigurations.computeIfAbsent(objectType, (key) -> new HashMap<>());
                    for (String action : configuration.getActions())
                    {
                        List<String> patternList = actionMap.computeIfAbsent(action, (key) -> new ArrayList<>());
                        if (!patternList.contains(configuration.getEmailPattern()))
                        {
                            patternList.add(configuration.getEmailPattern());
                        }
                    }
                }
            }
        }
        return templateConfigurations;
    }

    /**
     * @param templateData
     * @param templateConfigurationsMap
     * @return
     */
    protected EmailTemplateValidationResponse getCollideConfiguration(EmailTemplateConfiguration templateData,
            Map<String, Map<String, List<String>>> templateConfigurationsMap)
    {
        EmailTemplateValidationResponse response = new EmailTemplateValidationResponse();

        for (String objectType : templateData.getObjectTypes())
        {
            for (String action : templateData.getActions())
            {
                if (templateConfigurationsMap.containsKey(objectType) && templateConfigurationsMap.get(objectType).containsKey(action)
                        && templateConfigurationsMap.get(objectType).get(action).contains(templateData.getEmailPattern()))
                {
                    response.setObjectType(objectType);
                    response.setAction(action);
                    response.setEmailPattern(templateData.getEmailPattern());
                    response.setValidTemplate(false);
                    return response;
                }
            }
        }
        return response;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#updateEmailTemplate(com.armedia.acm.
     * services.email.service.EmailTemplateConfiguration, org.springframework.web.multipart.MultipartFile)
     */
    @Override
    public void updateEmailTemplate(EmailTemplateConfiguration templateData, MultipartFile template) throws AcmEmailConfigurationException
    {

        File templateFolder = getTemplateFolder();

        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations(templateData);

        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try (OutputStream os = getTemplateResourceOutputStream())
        {
            String configurationsString = getObjectConverter().getJsonMarshaller().marshal(configurations);
            if (configurationsString == null)
            {
                log.warn("Error while serializing email templates configuration to {} file.", templateConfigurations.getDescription(),
                        null);
                throw new AcmEmailConfigurationJsonException(
                        String.format("Error while serializing email templates configuration to %s file.",
                                templateConfigurations.getDescription()),
                        null);
            }
            os.write(configurationsString.getBytes(StandardCharsets.UTF_8));
            if (template != null)
            {
                File templateFile = new File(templateFolder, templateData.getTemplateName());
                template.transferTo(templateFile);
            }
        }
        catch (IOException e)
        {
            log.warn("Error while updating email template configuration for configuration with {} value for templateName.",
                    templateData.getTemplateName(), e);
            throw new AcmEmailConfigurationIOException(
                    String.format("Error while updating email template configuration for configuration with %s value for templateName.",
                            templateData.getTemplateName()),
                    e);
        }
        finally
        {
            writeLock.unlock();
        }

    }

    /**
     * @param templateData
     * @return
     * @throws AcmEmailConfigurationException
     */
    private List<EmailTemplateConfiguration> getTemplateConfigurations(EmailTemplateConfiguration templateData)
            throws AcmEmailConfigurationException
    {
        List<EmailTemplateConfiguration> configurations;
        try
        {
            configurations = getTemplateConfigurations();
        }
        catch (AcmEmailConfigurationException e)
        {
            if (isTemplateConfigurationFileEmpty())
            {
                configurations = new ArrayList<>();
            }
            else
            {
                // just re-throw here, if the error was during reading of the configuration it is already logged in
                // the 'getTemplateConfigurations()' method.
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
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                log.warn("Error while updating email template configuration for configuration with {} value for templateName.",
                        templateData.getTemplateName(), e);
                throw new AcmEmailConfigurationException(
                        String.format("Error while updating email template configuration for configuration with %s value for templateName.",
                                templateData.getTemplateName()),
                        e);
            }
        }
        else
        {
            configurations.add(templateData);
        }
        return configurations;
    }

    /**
     * @return
     * @throws IOException
     */
    private boolean isTemplateConfigurationFileEmpty() throws AcmEmailConfigurationException
    {
        try
        {
            return templateConfigurations.contentLength() == 0;
        }
        catch (IOException e)
        {
            log.warn("Error while reading email templates configuration from {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationIOException(String.format("Error while reading email templates configuration from %s file.",
                    templateConfigurations.getDescription()), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateCandidates(java.lang.
     * String, java.lang.String, com.armedia.acm.services.email.service.EmailSource, java.util.List)
     */
    @Override
    public List<EmailTemplateConfiguration> getMatchingTemplates(String email, String objectType, EmailSource source, List<String> actions)
            throws AcmEmailConfigurationException
    {
        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations();
        Stream<EmailTemplateConfiguration> filteredConfigurations = configurations.stream()
                .filter(c -> c.getObjectTypes().contains(objectType)).filter(c -> c.getActions().containsAll(actions))
                .filter(c -> c.getSource().equals(source)).filter(c -> matches(c.getEmailPattern(), email));

        return filteredConfigurations.collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String templateName) throws AcmEmailConfigurationException
    {
        File templateFolder = getTemplateFolder();
        File templateFile = new File(templateFolder, templateName);
        Lock readLock = lock.readLock();
        try
        {
            readLock.lock();
            if (!templateFile.exists())
            {
                log.warn("Email template {} does not exist.", templateName);
                throw new AcmEmailConfigurationIOException(String.format("Email template %s does not exist.", templateName));
            }
            return FileUtils.readFileToString(templateFile, "UTF-8");
        }
        catch (IOException e)
        {
            log.warn("Error while reading contents of {} email template.", templateName, e);
            throw new AcmEmailConfigurationIOException(String.format("Error while reading contents of %s email template.", templateName),
                    e);
        }
        finally
        {
            readLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#deleteTemplate(java.lang.String)
     */
    @Override
    public void deleteTemplate(String templateName) throws AcmEmailConfigurationException
    {
        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations();
        if (configurations.removeIf(c -> c.getTemplateName().equals(templateName)))
        {
            File templateFolder = getTemplateFolder();
            File templateFile = new File(templateFolder, templateName);
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try (OutputStream os = getTemplateResourceOutputStream())
            {
                String configurationsString = getObjectConverter().getJsonMarshaller().marshal(configurations);
                if (configurationsString == null)
                {
                    log.warn("Error while deleting email templates configuration from {} file.", templateConfigurations.getDescription(),
                            null);
                    throw new AcmEmailConfigurationJsonException(
                            String.format("Error while deleting email templates configuration from %s file.",
                                    templateConfigurations.getDescription()),
                            null);
                }
                os.write(configurationsString.getBytes(StandardCharsets.UTF_8));
                Files.deleteIfExists(templateFile.toPath());
            }
            catch (IOException e)
            {
                log.warn("Error while deleting {} email template from the file system.", templateName, e);
                throw new AcmEmailConfigurationIOException(
                        String.format("Error while deleting %s email template from the file system.", templateName), e);
            }
            finally
            {
                writeLock.unlock();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getExceptionMapper(com.armedia.acm.
     * services.email.service.AcmEmailServiceException)
     */
    @Override
    public <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e)
    {
        return new FilesystemMailTemplateConfigurationExceptionMapper<>();
    }

    /**
     * @return
     */
    private File getTemplateFolder()
    {
        return new File(templateFolderPath);
    }

    // TODO: TECHNICAL DEBT, this method should be private. Was made package access since inclusion of powermock for
    // testing was problematic. The access restriction was relaxed to enable stubbing with mockito without using the
    // powermock functionality.
    OutputStream getTemplateResourceOutputStream() throws AcmEmailConfigurationIOException
    {
        try
        {
            return Files.newOutputStream(templateConfigurations.getFile().toPath(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e)
        {
            log.warn("Error while opening configuration {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationIOException(
                    String.format("Error while opening configuration %s file.", templateConfigurations.getDescription()), e);
        }
    }

    /**
     * @param templateFolderPath
     *            the templateFolderPath to set
     */
    public void setTemplateFolderPath(String templateFolderPath)
    {
        this.templateFolderPath = templateFolderPath;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2017
     *
     */
    private class FilesystemMailTemplateConfigurationExceptionMapper<ME extends AcmEmailServiceException>
            implements AcmEmailServiceExceptionMapper<ME>
    {

        /*
         * (non-Javadoc)
         * @see
         * com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper#mapException(com.armedia.acm.services.
         * email.service.AcmEmailServiceException)
         */
        @Override
        public Object mapException(ME me)
        {
            Map<String, Object> errorDetails = new HashMap<>();
            if (me instanceof AcmEmailConfigurationIOException)
            {
                errorDetails.put("error_cause", "READ_WRITE_ERROR.");
            }
            else if (me instanceof AcmEmailConfigurationJsonException)
            {
                errorDetails.put("error_cause", "JSON_PARSING_ERROR.");
            }
            else if (me instanceof AcmEmailConfigurationException)
            {
                errorDetails.put("error_cause", "INTERENAL_SERVER_ERROR.");
            }
            else
            {
                errorDetails.put("error_cause", "UNKOWN_ERROR.");
            }
            errorDetails.put("error_message", me.getMessage());
            return errorDetails;
        }

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
