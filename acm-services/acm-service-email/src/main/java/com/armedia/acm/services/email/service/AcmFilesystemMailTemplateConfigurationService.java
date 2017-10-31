package com.armedia.acm.services.email.service;

import static java.util.regex.Pattern.matches;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2017
     *
     */
    private class FilesystemMailTemplateConfigurationExceptionMapper<ME extends AcmEmailServiceException>
            implements AcmEmailServiceExceptionMapper<ME>
    {

        /*
         * (non-Javadoc)
         *
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
         *
         * @see com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource templateConfigurations;

    private String templateFolderPath;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /*
     * (non-Javadoc)
     *
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
            return readConfigurationList;
        }
        catch (JsonParseException | JsonMappingException e)
        {
            log.warn("Error while deserializing email templates configuration from {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationJsonException(String.format(
                    "Error while deserializing email templates configuration from %s file.", templateConfigurations.getDescription()), e);
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

        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations(templateData);

        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try (OutputStream os = getTemplateResourceOutputStream())
        {
            String configurationsString = getObjectConverter().getJsonMarshaller().marshal(configurations);
            os.write(configurationsString.getBytes(StandardCharsets.UTF_8));
            if (template != null)
            {
                File templateFile = new File(templateFolder, templateData.getTemplateName());
                template.transferTo(templateFile);
            }
        }
        catch (JsonParseException | JsonMappingException e)
        {
            log.warn("Error while serializing email templates configuration to {} file.", templateConfigurations.getDescription(), e);
            throw new AcmEmailConfigurationJsonException(String.format("Error while serializing email templates configuration to %s file.",
                    templateConfigurations.getDescription()), e);
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
     *
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplateCandidates(java.lang.
     * String, java.lang.String, com.armedia.acm.services.email.service.EmailSource, java.util.List)
     */
    @Override
    public List<EmailTemplateConfiguration> getMatchingTemplates(String email, String objectType, EmailSource source, List<String> actions)
            throws AcmEmailConfigurationException
    {
        List<EmailTemplateConfiguration> configurations = getTemplateConfigurations();
        Stream<EmailTemplateConfiguration> filteredConfigurations = configurations.stream()
                .filter(c -> c.getObjectTypes().contains(objectType))
                .filter(c -> c.getActions().containsAll(actions) && actions.containsAll(c.getActions()))
                .filter(c -> c.getSource().equals(source)).filter(c -> matches(c.getEmailPattern(), email));

        return filteredConfigurations.collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
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
     *
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
                os.write(configurationsString.getBytes(StandardCharsets.UTF_8));
                Files.deleteIfExists(templateFile.toPath());
            }
            catch (JsonParseException | JsonMappingException e)
            {
                log.warn("Error while deleting email templates configuration from {} file.", templateConfigurations.getDescription(), e);
                throw new AcmEmailConfigurationJsonException(String.format(
                        "Error while deleting email templates configuration from %s file.", templateConfigurations.getDescription()), e);
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
     *
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
     * @param templateConfigurations
     *            the templateConfigurations to set
     */
    public void setTemplateConfigurations(Resource templateConfigurations)
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
