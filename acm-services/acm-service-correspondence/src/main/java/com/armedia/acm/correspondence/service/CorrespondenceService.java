package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.QueryType;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CorrespondenceService
{
    private SpringContextHolder springContextHolder;
    private CorrespondenceGenerator correspondenceGenerator;
    private CorrespondenceEventPublisher eventPublisher;

    private CorrespondenceTemplateManager templateManager;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TEMP_FILE_PREFIX = "template-";
    private static final String TEMP_FILE_SUFFIX = ".docx";

    /**
     * For use from MVC controllers and any other client with an Authentication object.
     *
     * @param authentication
     * @param templateName
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws AcmCreateObjectFailedException
     */
    public EcmFile generate(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        CorrespondenceTemplate template = findTemplate(templateName);

        File file = null;

        try
        {
            file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

            log.debug("writing correspondence to file: " + file.getCanonicalPath());

            FileOutputStream fosToWriteFile = new FileOutputStream(file);
            FileInputStream fisForUploadToEcm = new FileInputStream(file);

            EcmFile retval = getCorrespondenceGenerator().generateCorrespondence(authentication, parentObjectType, parentObjectId,
                    targetCmisFolderId, template, new Object[] {parentObjectId}, fosToWriteFile, fisForUploadToEcm);

            log.debug("Correspondence CMIS ID: " + retval.getVersionSeriesId());

            getEventPublisher().publishCorrespondenceAdded(retval, authentication, true);

            return retval;
        } finally
        {
            if (file != null)
            {
                FileUtils.deleteQuietly(file);
            }
        }

    }

    private CorrespondenceTemplate findTemplate(String templateName)
    {
        Collection<CorrespondenceTemplate> templates = templateManager.getTemplates();
        for (CorrespondenceTemplate template : templates)
        {
            if (templateName.equalsIgnoreCase(template.getTemplateFilename()))
            {
                return template;
            }
        }

        throw new IllegalArgumentException("Template '" + templateName + "' is not a registered template name!");
    }

    /**
     * Helper method for use from Activiti and other clients with no direct access to an Authentication, but in the call
     * stack of a Spring MVC authentication... so there is an Authentication in the Spring Security context holder.
     */
    public EcmFile generate(String templateName, String parentObjectType, Long parentObjectId, String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return generate(currentUser, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
    }

    /**
     * @return
     */
    public Map<String, CorrespondenceQuery> getAllQueries()
    {
        return springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);
    }

    /**
     * @param queryType
     * @return
     */
    public Map<String, CorrespondenceQuery> getQueriesByType(QueryType queryType)
    {
        return getAllQueries().entrySet().stream().filter(entry -> entry.getValue().getType().equals(queryType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * @param queryBeanId
     * @return
     */
    public Optional<CorrespondenceQuery> getQueryByBeanId(String queryBeanId)
    {
        return Optional.ofNullable(springContextHolder.getAllBeansOfType(CorrespondenceQuery.class).get(queryBeanId));
    }

    /**
     * @param query
     * @return
     */
    public String getQueryId(CorrespondenceQuery query)
    {
        return templateManager.getQueryId(query);
    }

    /**
     * @param templateFileName
     * @return
     */
    public Optional<CorrespondenceTemplate> getTemplateByFileName(String templateFileName)
    {
        return templateManager.getTemplateByFileName(templateFileName);
    }

    /**
     * @param templateFileName
     * @return
     * @throws IOException
     */
    public Optional<CorrespondenceTemplate> deleteTemplate(String templateFileName) throws IOException
    {
        return templateManager.deleteTemplate(templateFileName);
    }

    /**
     * @param mapRequestToTemplate
     * @throws IOException
     */
    public Optional<CorrespondenceTemplate> updateTemplate(CorrespondenceTemplate template) throws IOException
    {
        return templateManager.updateTemplate(template);
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public CorrespondenceGenerator getCorrespondenceGenerator()
    {
        return correspondenceGenerator;
    }

    public void setCorrespondenceGenerator(CorrespondenceGenerator correspondenceGenerator)
    {
        this.correspondenceGenerator = correspondenceGenerator;
    }

    public CorrespondenceEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(CorrespondenceEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    /**
     * @param templateManager the templateManager to set
     */
    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }

}
