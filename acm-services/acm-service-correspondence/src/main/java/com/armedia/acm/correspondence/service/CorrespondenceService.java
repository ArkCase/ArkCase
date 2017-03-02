package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersion;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CorrespondenceService
{
    private SpringContextHolder springContextHolder;
    private CorrespondenceGenerator correspondenceGenerator;
    private CorrespondenceEventPublisher eventPublisher;

    private CorrespondenceTemplateManager templateManager;
    private CorrespondenceMergeFieldManager mergeFieldManager;

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
                    targetCmisFolderId, template, new Object[] { parentObjectId }, fosToWriteFile, fisForUploadToEcm);

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
        Collection<CorrespondenceTemplate> templates = templateManager.getActiveVersionTemplates();
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
     * @param queryType
     * @return
     */
    public CorrespondenceQuery getQueryByType(String queryType)
    {
        for (CorrespondenceQuery correspondenceQuery : getAllQueries().values())
        {
            if (correspondenceQuery.getType().toString().equals(queryType))
            {
                return correspondenceQuery;
            }
        }
        return null;
        // return getAllQueries().values().stream().filter(entry ->
        // entry.getType().equals(queryType)).collect(Collectors.toList());
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
        return "";// templateManager.getQueryId(query);
    }

    /**
     * @return
     */
    public List<CorrespondenceTemplate> getAllTemplates()
    {
        return templateManager.getAllTemplates();
    }

    /**
     * @return
     */
    public List<CorrespondenceTemplate> getActiveVersionTemplates()
    {
        return templateManager.getActiveVersionTemplates();
    }

    /**
     * @param objectType
     * @return
     */
    public List<CorrespondenceTemplate> getActivatedActiveVersionTemplatesByObjectType(String objectType)
    {
        return templateManager.getActivatedActiveVersionTemplatesByObjectType(objectType);
    }

    /**
     * @param templateId
     * @return
     */
    public List<CorrespondenceTemplate> getTemplateVersionsById(String templateId)
    {
        return templateManager.getTemplateVersionsById(templateId);
    }

    /**
     * @param templateId
     * @return
     */
    public Optional<CorrespondenceTemplate> getActiveTemplateById(String templateId)
    {
        return templateManager.getActiveTemplateById(templateId);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    public Optional<CorrespondenceTemplate> getTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return templateManager.getTemplateByIdAndVersion(templateId, templateVersion);
    }

    /**
     * @param templateId
     * @param templateFilename
     * @return
     */

    public Optional<CorrespondenceTemplate> getTemplateByIdAndFilename(String templateId, String templateFilename)
    {
        return templateManager.getTemplateByIdAndFilename(templateId, templateFilename);
    }

    /**
     * @param templateId
     * @return
     * @throws IOException
     */
    public Optional<CorrespondenceTemplate> deleteActiveVersionTemplate(String templateId) throws IOException
    {
        return templateManager.deleteActiveVersionTemplate(templateId);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     * @throws IOException
     */
    public Optional<CorrespondenceTemplate> deleteTemplateByIdAndVersion(String templateId, String templateVersion) throws IOException
    {
        return templateManager.deleteTemplateByIdAndVersion(templateId, templateVersion);
    }

    public List<CorrespondenceMergeField> getMergeFields()
    {
        return mergeFieldManager.getMergeFields();
    }

    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersions()
    {
        return mergeFieldManager.getMergeFieldVersions();
    }

    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersionsByType(String objectType)
    {
        return mergeFieldManager.getMergeFieldVersionsByType(objectType);
    }

    public List<CorrespondenceMergeField> getActiveVersionMergeFieldsByType(String objectType) throws IOException
    {
        return mergeFieldManager.getActiveVersionMergeFieldsByType(objectType);
    }

    public List<CorrespondenceMergeField> getMergeFieldsByType(String objectType) throws IOException
    {
        return mergeFieldManager.getActiveVersionMergeFieldsByType(objectType);
    }

    public CorrespondenceMergeFieldVersion getActiveMergingVersion(String objectType) throws IOException
    {
        return mergeFieldManager.getActiveMergingVersionByType(objectType);
    }

    public List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> mergeFields, Authentication auth)
            throws IOException
    {
        return mergeFieldManager.saveMergeFieldsData(mergeFields, auth);
    }

    public CorrespondenceMergeFieldVersion setActiveMergingVersion(CorrespondenceMergeFieldVersion mergeFieldVersion, Authentication auth)
            throws IOException
    {
        return mergeFieldManager.setActiveMergingVersion(mergeFieldVersion, auth);
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
     * @param templateManager
     *            the templateManager to set
     */
    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }

    /**
     * @param mergeFieldManager
     *            the mergeFieldManager to set
     */
    public void setMergeFieldManager(CorrespondenceMergeFieldManager mergeFieldManager)
    {
        this.mergeFieldManager = mergeFieldManager;
    }

}
