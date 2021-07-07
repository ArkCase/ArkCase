package com.armedia.acm.correspondence.service;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.templateconfiguration.model.CorrespondenceMergeField;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceMergeFieldManager;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.templateconfiguration.service.TemplatingEngine;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CorrespondenceServiceImpl implements CorrespondenceService
{
    private static final String TEMP_FILE_PREFIX = "template-";
    private static final String TEMP_FILE_SUFFIX = ".docx";

    private transient final Logger log = LogManager.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private CorrespondenceGenerator correspondenceGenerator;
    private CorrespondenceEventPublisher eventPublisher;
    private CorrespondenceTemplateManager templateManager;
    private CorrespondenceMergeFieldManager mergeFieldManager;
    private NotificationService notificationService;
    private TemplatingEngine templatingEngine;
    private EcmFileVersionDao ecmFileVersionDao;

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
     * @throws AcmCreateObjectFailedException
     */
    @Override
    public EcmFile generate(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId) throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {

        Template template = templateManager.findTemplate(templateName);
        if (template.isEnabled())
        {
            return generateCorrespondence(authentication, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
        }
        else
        {
            throw new IOException("Failed to generate correspondence document for template with name: [" + templateName + "]");
        }
    }

    @Override
    public EcmFile generate(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId, Boolean isManual)
            throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        if (isManual)
        {
            return generateCorrespondence(authentication, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
        }
        else
        {
            throw new IOException("Failed to generate correspondence document for template with name: [" + templateName + "]");
        }
    }

    private EcmFile generateCorrespondence(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId)
            throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        Template template = templateManager.findTemplate(templateName);
        File file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

        try (FileInputStream fisForUploadToEcm = new FileInputStream(file);
                FileOutputStream fosToWriteFile = new FileOutputStream(file))
        {

            log.debug("writing correspondence to file: " + file.getCanonicalPath());

            EcmFile retval = getCorrespondenceGenerator().generateCorrespondence(authentication, parentObjectType, parentObjectId,
                    targetCmisFolderId, template, new Object[] { parentObjectId }, fosToWriteFile, fisForUploadToEcm);

            log.debug("Correspondence CMIS ID: " + retval.getVersionSeriesId());

            getEventPublisher().publishCorrespondenceAdded(retval, authentication, true);

            return retval;
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Helper method for use from Activiti and other clients with no direct access to an Authentication, but in the call
     * stack of a Spring MVC authentication... so there is an Authentication in the Spring Security context holder.
     */
    @Override
    public EcmFile generate(String templateName, String parentObjectType, Long parentObjectId, String targetCmisFolderId)
            throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException

    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return generate(currentUser, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
    }

    /**
     * @return
     */
    @Override
    public List<Template> getAllTemplates()
    {
        return templateManager.getAllTemplates();
    }

    /**
     * @return
     */
    @Override
    public List<Template> getActiveVersionTemplates()
    {
        return templateManager.getActiveVersionTemplates();
    }

    /**
     * @param objectType
     * @return
     */
    @Override
    public List<Template> getActivatedActiveVersionTemplatesByObjectType(String objectType)
    {
        return templateManager.getActivatedActiveVersionTemplatesByObjectType(objectType);
    }

    @Override
    public List<Template> getActiveVersionTemplatesByTemplateType(String templateType)
    {
        return templateManager.getActiveVersionTemplatesByTemplateType(templateType);
    }

    /**
     * @param templateId
     * @return
     */
    @Override
    public List<Template> getTemplateVersionsById(String templateId)
    {
        return templateManager.getTemplateVersionsById(templateId);
    }

    /**
     * @param templateId
     * @return
     */
    @Override
    public Optional<Template> getActiveTemplateById(String templateId)
    {
        return templateManager.getActiveTemplateById(templateId);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    @Override
    public Optional<Template> getTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return templateManager.getTemplateByIdAndVersion(templateId, templateVersion);
    }

    /**
     * @param templateId
     * @param templateFilename
     * @return
     */
    @Override
    public Optional<Template> getTemplateByIdAndFilename(String templateId, String templateFilename)
    {
        return templateManager.getTemplateByIdAndFilename(templateId, templateFilename);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     * @throws IOException
     */
    @Override
    public Optional<Template> deleteTemplateByIdAndVersion(String templateId, String templateVersion) throws IOException
    {
        return templateManager.deleteTemplateByIdAndVersion(templateId, templateVersion);
    }

    /**
     * @return
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFields()
    {
        return mergeFieldManager.getMergeFields();
    }

    /**
     * @param objectType
     * @return
     * @throws IOException,
     *             CorrespondenceMergeFieldVersionException
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFieldsByType(String objectType)
    {
        return mergeFieldManager.getMergeFieldsByType(objectType);
    }

    /**
     * @param mergeFieldId
     * @return
     * @throws IOException,
     *             CorrespondenceMergeFieldVersionException
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFieldByMergeFieldId(String mergeFieldId)
    {
        return mergeFieldManager.getMergeFieldByMergeFieldId(mergeFieldId);
    }

    @Override
    public void deleteMergeFields(String mergeFieldId) throws IOException
    {
        mergeFieldManager.deleteMergeFields(mergeFieldId);
    }

    @Override
    public void addMergeField(CorrespondenceMergeField newMergeField) throws IOException
    {
        mergeFieldManager.addMergeField(newMergeField);
    }

    /**
     * @param mergeFields
     * @param auth
     * @return
     * @throws IOException
     */
    @Override
    public List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> mergeFields, Authentication auth)
            throws IOException
    {
        return mergeFieldManager.saveMergeFieldsData(mergeFields, auth);
    }

    /**
     * @param template
     * @throws IOException
     */
    @Override
    public Optional<Template> updateTemplate(Template template) throws IOException
    {
        return templateManager.updateTemplate(template);
    }

    @Override
    public AcmAbstractDao<AcmEntity> getAcmAbstractDao(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmEntity> dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Map<String, String> listTemplateModelProviders()
    {
        Collection<TemplateModelProvider> templateModelProviders = getSpringContextHolder().getAllBeansOfType(TemplateModelProvider.class)
                .values();

        Map<String, String> mapTemplateModelProviders = new HashMap<>();
        for (TemplateModelProvider modelProvider : templateModelProviders)
        {
            String[] splittedTemplateProviderClassPath = modelProvider.getClass().getName().split("\\.");
            String templateModelProviderShortName = splittedTemplateProviderClassPath[splittedTemplateProviderClassPath.length - 1];

            mapTemplateModelProviders.put(modelProvider.getClass().getName(), templateModelProviderShortName);
        }
        return mapTemplateModelProviders;
    }

    @Override
    public String getTemplateModelProviderDeclaredFields(String classPath)
    {
        String jsonSchemaProperties = null;
        try
        {
            Class templateModelProviderClass = Class.forName(classPath);
            TemplateModelProvider instance = (TemplateModelProvider) templateModelProviderClass.newInstance();
            Class clazz = instance.getType();

            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

            JsonSchemaGenerator jsg = new JsonSchemaGenerator(mapper);
            JsonSchema jsonSchema = jsg.generateSchema(clazz);
            jsonSchemaProperties = writer.writeValueAsString(jsonSchema);

        }
        catch (Exception e)
        {
            log.error("The provided classpath is invalid. Because of: {}", e.getMessage());
        }
        return jsonSchemaProperties;
    }

    @Override
    public TemplateModelProvider getTemplateModelProvider(Class templateModelProviderClass)
    {
        Map<String, TemplateModelProvider> templateModelproviders = getSpringContextHolder().getAllBeansOfType(templateModelProviderClass);
        if (templateModelproviders.size() > 1)
        {
            for (TemplateModelProvider provider : templateModelproviders.values())
            {
                if (provider.getClass().equals(templateModelProviderClass))
                {
                    return provider;
                }
            }
        }
        return templateModelproviders.values().iterator().hasNext() ? templateModelproviders.values().iterator().next() : null;
    }

    @Override
    public Notification convertMergeTerms(String templateName, String templateContent, String objectType, String objectId,
            List<Long> fileIds)
    {
        String templateModelName = templateName.substring(0, templateName.indexOf("."));
        Template template = templateManager.findTemplate(templateName);
        String title = getNotificationService().setNotificationTitleForManualNotification(templateModelName);

        List<EcmFileVersion> ecmFileVersions = new ArrayList<>();
        if (fileIds != null)
        {
            ecmFileVersions = getEcmFileVersionDao().findByIds(fileIds);
        }
        Notification notification = getNotificationService().getNotificationBuilder()
                .newNotification(templateModelName, title, objectType,
                        Long.parseLong(objectId), null)
                .withNotificationType(NotificationConstants.TYPE_MANUAL)
                .withEmailContent(templateContent)
                .withFiles(ecmFileVersions)
                .build();

        String templateModelProvider = template != null ? template.getTemplateModelProvider() : "";

        Class templateModelProviderClass = null;
        try
        {
            templateModelProviderClass = Class.forName(templateModelProvider);
        }
        catch (Exception e)
        {
            log.error("Can not find class for provided classpath {}. Error: {}", templateModelProvider, e.getMessage());
        }

        TemplateModelProvider modelProvider = getTemplateModelProvider(templateModelProviderClass);
        if (modelProvider != null)
        {
            try
            {
                Object object = modelProvider.getModel(notification);
                String body = getTemplatingEngine().process(templateContent, templateModelName, object);
                notification.setEmailContent(body);
                String templateSubject = template.getEmailSubject() != null ? template.getEmailSubject() : "";
                String subject = getTemplatingEngine().process(templateSubject, templateModelName, object);
                notification.setSubject(subject);
                return notification;
            }
            catch (Exception ex)
            {
                log.error("Failed to process template {}! Error: {} ", templateName, ex.getMessage());
            }
        }
        return notification;
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

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }
}
