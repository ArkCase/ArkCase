package com.armedia.acm.services.transcribe.service;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.EmailSource;
import com.armedia.acm.services.email.service.EmailTemplateConfiguration;
import com.armedia.acm.services.labels.service.LabelManagementService;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.exception.CompileTranscribeException;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessModel;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.model.TranscribeUserType;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessProcessRulesExecutor;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.spring.SpringContextHolder;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.NotImplementedException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONObject;
import org.mule.util.FileUtils;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class ArkCaseTranscribeServiceImpl implements ArkCaseTranscribeService
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private TranscribeDao transcribeDao;
    private EcmFileVersionDao ecmFileVersionDao;
    private TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService;
    private PipelineManager<Transcribe, TranscribePipelineContext> pipelineManager;
    private TranscribeBusinessProcessRulesExecutor transcribeBusinessProcessRulesExecutor;
    private RuntimeService activitiRuntimeService;
    private TranscribeServiceFactory transcribeServiceFactory;
    private ArkCaseBeanUtils transcribeArkCaseBeanUtils;
    private EcmFileService ecmFileService;
    private TranscribeEventPublisher transcribeEventPublisher;
    private AcmMailTemplateConfigurationService templateService;
    private AcmEmailSenderService emailSenderService;
    private SpringContextHolder springContextHolder;
    private UserDao userDao;
    private LabelManagementService labelManagementService;
    private AcmApplication acmApplication;

    @Override
    @Transactional
    public Transcribe create(Long versionId, TranscribeType type) throws CreateTranscribeException
    {
        EcmFileVersion ecmFileVersion = getEcmFileVersionDao().find(versionId);

        return create(ecmFileVersion, type);
    }

    @Override
    @Transactional
    public Transcribe create(EcmFileVersion ecmFileVersion, TranscribeType type) throws CreateTranscribeException
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setMediaEcmFileVersion(ecmFileVersion);
        transcribe.setType(type.toString());

        return create(transcribe);
    }

    @Override
    public Transcribe get(Long id) throws GetTranscribeException
    {
        return getTranscribeDao().find(id);
    }

    @Override
    public Transcribe getByMediaVersionId(Long mediaVersionId) throws GetTranscribeException
    {
        return getTranscribeDao().findByMediaVersionId(mediaVersionId);
    }

    @Override
    public Transcribe save(Transcribe transcribe) throws SaveTranscribeException
    {
        Transcribe saved = getTranscribeDao().save(transcribe);
        String action = transcribe.getId() == null ? TranscribeActionType.CREATED.toString() : TranscribeActionType.UPDATED.toString();
        getTranscribeEventPublisher().publish(saved, action);

        return saved;
    }

    @Override
    public Transcribe copy(Transcribe transcribe, EcmFileVersion ecmFileVersion) throws CreateTranscribeException
    {
        Transcribe copy = null;
        try
        {
            copy = new Transcribe();
            copy.setMediaEcmFileVersion(ecmFileVersion);
            getTranscribeArkCaseBeanUtils().copyProperties(copy, transcribe);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for Transcribe object with ID=[{}]. REASON=[{}]",
                    transcribe != null ? transcribe.getId() : null, e.getMessage());
        }

        if (copy != null)
        {
            Transcribe savedCopy = null;
            try
            {
                savedCopy = save(copy);
            }
            catch (SaveTranscribeException e)
            {
                throw new CreateTranscribeException(String.format("Could not create copy for Transcribe object with ID=[{}]. REASON=[%s]",
                        transcribe != null ? transcribe.getId() : null, e.getMessage()));
            }

            if (StringUtils.isNotEmpty(transcribe.getProcessId()))
            {
                ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                        .processInstanceId(transcribe.getProcessId()).singleResult();
                if (processInstance != null)
                {
                    List<Long> ids = (List<Long>) processInstance.getProcessVariables()
                            .get(TranscribeBusinessProcessVariableKey.IDS.toString());
                    if (ids == null)
                    {
                        ids = new ArrayList<>();
                    }

                    ids.add(savedCopy.getId());
                    getActivitiRuntimeService().setVariable(processInstance.getId(), TranscribeBusinessProcessVariableKey.IDS.toString(),
                            ids);
                }
            }

            return savedCopy;
        }

        throw new CreateTranscribeException(
                String.format("Could not create copy for Transcribe object with ID=[{}]", transcribe != null ? transcribe.getId() : null));
    }

    @Override
    public Transcribe complete(Long id) throws SaveTranscribeException
    {
        Transcribe transcribe = getTranscribeDao().find(id);
        if (transcribe != null && StringUtils.isNotEmpty(transcribe.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(transcribe.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, TranscribeStatusType.COMPLETED.toString(), TranscribeActionType.COMPLETED.toString());
                transcribe.setStatus(TranscribeStatusType.COMPLETED.toString());
                return transcribe;
            }
        }

        throw new SaveTranscribeException(String.format("Could not complete Transcribe object with ID=[%d]", id));
    }

    @Override
    public Transcribe cancel(Long id) throws SaveTranscribeException
    {
        Transcribe transcribe = getTranscribeDao().find(id);
        if (transcribe != null && StringUtils.isNotEmpty(transcribe.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(transcribe.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, TranscribeStatusType.DRAFT.toString(), TranscribeActionType.CANCELLED.toString());
                transcribe.setStatus(TranscribeStatusType.DRAFT.toString());
                return transcribe;
            }
        }

        throw new SaveTranscribeException(String.format("Could not cancel Transcribe object with ID=[%d]", id));
    }

    @Override
    public Transcribe fail(Long id) throws SaveTranscribeException
    {
        Transcribe transcribe = getTranscribeDao().find(id);
        if (transcribe != null && StringUtils.isNotEmpty(transcribe.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(transcribe.getProcessId()).singleResult();
            if (processInstance != null)
            {
                String statusKey = TranscribeBusinessProcessVariableKey.STATUS.toString();
                String actionKey = TranscribeBusinessProcessVariableKey.ACTION.toString();
                String status = TranscribeStatusType.FAILED.toString();
                String action = TranscribeActionType.FAILED.toString();

                getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
                getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);

                transcribe.setStatus(TranscribeStatusType.FAILED.toString());

                return transcribe;
            }
        }

        throw new SaveTranscribeException(String.format("Could not set as failed Transcribe object with ID=[%d]", id));
    }

    @Override
    public Transcribe changeStatus(Long id, String status) throws SaveTranscribeException
    {
        LOG.debug("Changing status of the Transcribe with ID=[{}]. New status will be STATUS=[{}]", id, status);
        if (id == null || StringUtils.isEmpty(status))
        {
            String message = String.format("Status of the Transcribe cannot be changed. ID=[%d], STATUS=[%s]", id, status);
            LOG.error(message);
            throw new SaveTranscribeException(message);
        }

        Transcribe transcribe = getTranscribeDao().find(id);

        if (transcribe == null)
        {
            String message = String.format("Transcribe with ID=[%d] cannot be found or retrieved from database.", id);
            LOG.error(message);
            throw new SaveTranscribeException(message);
        }

        transcribe.setStatus(status);

        try
        {
            return getTranscribeDao().save(transcribe);
        }
        catch (Exception e)
        {
            String message = String.format("Status of the Transcribe cannot be changed. ID=[%d], STATUS=[%s]. REASON=[%s]", id, status,
                    e.getMessage());
            LOG.error(message);
            throw new SaveTranscribeException(message, e);
        }
    }

    @Override
    public List<Transcribe> changeStatusMultiple(List<Long> ids, String status) throws SaveTranscribeException
    {
        if (ids != null)
        {
            List<Transcribe> changedTranscribes = new ArrayList<>();
            ids.forEach(id -> {
                try
                {
                    Transcribe changed = changeStatus(id, status);
                    changedTranscribes.add(changed);
                }
                catch (SaveTranscribeException e)
                {
                    LOG.warn("Changing status for Transcribe with ID=[{}] in bulk operation failed. REASON=[{}]", id, e.getMessage());
                }
            });

            return changedTranscribes;
        }

        String message = String.format("Status of multiple Transcribe objects cannot be changed. IDS=[null], STATUS=[%s]", status);
        LOG.error(message);
        throw new SaveTranscribeException(message);
    }

    @Override
    public void notify(Long id, String action)
    {
        if (id != null && StringUtils.isNotEmpty(action))
        {
            Transcribe transcribe = getTranscribeDao().find(id);
            if (transcribe != null)
            {
                // Take users: owner of the media file and owner of the parent object
                List<AcmUser> users = new ArrayList<>();
                String userIdOwnerOfMedia = getUserIdForGivenUserType(TranscribeUserType.OWNER_OF_MEDIA.toString(), transcribe);
                String userIdOwnerOfParentObject = getUserIdForGivenUserType(TranscribeUserType.OWNER_OF_PARENT_OBJECT.toString(),
                        transcribe);
                AcmUser userOwnerOfMedia = null;
                AcmUser userOwnerOfParentObject = null;
                if (StringUtils.isNotEmpty(userIdOwnerOfMedia))
                {
                    userOwnerOfMedia = getUserDao().findByUserId(userIdOwnerOfMedia);
                    if (userOwnerOfMedia != null && StringUtils.isNotEmpty(userOwnerOfMedia.getMail()))
                    {
                        users.add(userOwnerOfMedia);
                    }
                }
                if (StringUtils.isNotEmpty(userIdOwnerOfParentObject))
                {
                    userOwnerOfParentObject = getUserDao().findByUserId(userIdOwnerOfParentObject);
                    if (userOwnerOfParentObject != null && StringUtils.isNotEmpty(userOwnerOfParentObject.getMail()))
                    {
                        // If owner of the media and owner of the parent object have the same email, exclude this user,
                        // send email only once
                        final String email = userOwnerOfParentObject.getMail();
                        AcmUser found = users.stream().filter(user -> email.equalsIgnoreCase(user.getMail())).findFirst().orElse(null);
                        if (found == null)
                        {
                            users.add(userOwnerOfParentObject);
                        }
                    }
                }

                // Send email for all users in the list
                sendEmail(users, action, transcribe);
            }
        }
    }

    private void sendEmail(List<AcmUser> users, String action, Transcribe transcribe)
    {
        if (users != null && !users.isEmpty() && StringUtils.isNotEmpty(action) && transcribe != null)
        {
            users.forEach(user -> {
                try
                {
                    JSONObject jsonConfig = getLabelManagementService().getResource("document-details",
                            StringUtils.isNotEmpty(user.getLang()) ? user.getLang() : "en", false);
                    String subject = jsonConfig.getString("documentDetails.transcribe.email.subject");
                    String body = jsonConfig.getString("documentDetails.transcribe.email.body");
                    body = body.replace(":transcribeId", transcribe.getId().toString())
                            .replace(":action", action)
                            .replace(":name", transcribe.getMediaEcmFileVersion().getFile().getFileName())
                            .replace(":id", transcribe.getMediaEcmFileVersion().getFile().getId().toString())
                            .replace(":containerId",
                                    transcribe.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId().toString())
                            .replace(":containerType",
                                    transcribe.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType())
                            .replace(":host", getAcmApplication().getBaseUrl());

                    EmailWithAttachmentsDTO emailWithAttachmentsDTO = new EmailWithAttachmentsDTO();
                    emailWithAttachmentsDTO.setSubject(subject);
                    emailWithAttachmentsDTO.setBody(body);
                    emailWithAttachmentsDTO.setEmailAddresses(Arrays.asList(user.getMail()));

                    String objectType = transcribe.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType();
                    List<EmailTemplateConfiguration> configurations = getTemplateService().getMatchingTemplates(user.getMail(), objectType,
                            EmailSource.MANUAL, Arrays.asList("sendAsLinks"));

                    // We'll need only one template. Take the first one
                    if (configurations != null && configurations.size() > 0)
                    {
                        String template = getTemplateService().getTemplate(configurations.get(0).getTemplateName());
                        emailWithAttachmentsDTO.setTemplate(template);
                    }

                    getEmailSenderService().sendEmail(emailWithAttachmentsDTO, null, user);
                }
                catch (Exception e)
                {
                    LOG.error(
                            "Email notification was not sent to the USER=[{}] for Transcribe with ID=[{}] for the ACTION=[{}]. REASON=[{}]",
                            user, transcribe.getId(), action, e.getMessage());
                }
            });
        }
        else
        {
            LOG.error("Email notification was not sent for Transcribe with ID=[{}] for the ACTION=[{}]. Users not found.",
                    transcribe != null ? transcribe.getId() : null, action);
        }
    }

    private String getUserIdForGivenUserType(String userType, Transcribe transcribe)
    {
        String userId = null;
        TranscribeUserType type = TranscribeUserType.valueOf(userType);
        switch (type)
        {
        case OWNER_OF_MEDIA:
            // First try to find assignee
            userId = ParticipantUtils.getAssigneeIdFromParticipants(transcribe.getMediaEcmFileVersion().getFile().getParticipants());
            if (StringUtils.isEmpty(userId))
            {
                // If there is no assignee, take the creator
                userId = transcribe.getMediaEcmFileVersion().getFile().getCreator();
            }
            break;
        case OWNER_OF_PARENT_OBJECT:
            Long objectId = transcribe.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId();
            String objectType = transcribe.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType();

            // First try to find assignee
            AcmAbstractDao<AcmAssignedObject> acmAssignedObjectDao = getAssignedObjectDao(objectType);
            if (acmAssignedObjectDao != null)
            {
                AcmAssignedObject assignedObject = acmAssignedObjectDao.find(objectId);
                if (assignedObject != null)
                {
                    userId = ParticipantUtils.getAssigneeIdFromParticipants(assignedObject.getParticipants());
                }
            }

            // If there is no assignee, take the creator
            if (StringUtils.isEmpty(userId))
            {
                AcmAbstractDao<AcmEntity> acmEntityDao = getEntityDao(objectType);
                if (acmEntityDao != null)
                {
                    AcmEntity entity = acmEntityDao.find(objectId);
                    if (entity != null)
                    {
                        userId = entity.getCreator();
                    }
                }
            }
            break;
        }

        return userId;
    }

    private AcmAbstractDao<AcmEntity> getEntityDao(String objectType)
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

    private AcmAbstractDao<AcmAssignedObject> getAssignedObjectDao(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmAssignedObject> dao : daos.values())
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
    public void notifyMultiple(List<Long> ids, String action)
    {
        if (ids != null)
        {
            ids.forEach(id -> notify(id, action));
        }
    }

    @Override
    public void audit(Long id, String action)
    {
        if (id != null && action != null)
        {
            Transcribe transcribe = getTranscribeDao().find(id);
            if (transcribe != null)
            {
                getTranscribeEventPublisher().publish(transcribe, action);
            }
        }
    }

    @Override
    public void auditMultiple(List<Long> ids, String action)
    {
        if (ids != null)
        {
            ids.forEach(id -> audit(id, action));
        }
    }

    @Override
    public EcmFile compile(Long id) throws CompileTranscribeException
    {
        if (id != null)
        {
            Transcribe transcribe = getTranscribeDao().find(id);
            if (transcribe != null)
            {
                if (transcribe.getTranscribeItems() != null && transcribe.getTranscribeItems().size() > 0)
                {
                    File file = null;
                    try
                    {
                        file = File.createTempFile(TranscribeConstants.TEMP_FILE_PREFIX, TranscribeConstants.TEMP_FILE_SUFFIX);

                        try (InputStream in = new FileInputStream(file);
                                OutputStream out = new FileOutputStream(file);
                                XWPFDocument document = new XWPFDocument())
                        {
                            XWPFParagraph paragraph = document.createParagraph();
                            XWPFRun run = paragraph.createRun();
                            run.setText(TranscribeUtils.getText(transcribe.getTranscribeItems()));
                            document.write(out);

                            String fileName = transcribe.getMediaEcmFileVersion().getFile().getFileName() + "_v"
                                    + transcribe.getMediaEcmFileVersion().getFile().getActiveVersionTag();
                            AcmFolder folder = transcribe.getMediaEcmFileVersion().getFile().getFolder();
                            String parentObjectType = transcribe.getMediaEcmFileVersion().getFile().getParentObjectType();
                            Long parentObjectId = transcribe.getMediaEcmFileVersion().getFile().getParentObjectId();

                            Authentication authentication = SecurityContextHolder.getContext() != null
                                    ? SecurityContextHolder.getContext().getAuthentication()
                                    : null;

                            // Delete existing file first if exist
                            if (transcribe.getTranscribeEcmFile() != null && transcribe.getTranscribeEcmFile().getId() != null)
                            {
                                try
                                {

                                    getEcmFileService().deleteFile(transcribe.getTranscribeEcmFile().getId());
                                }
                                catch (AcmObjectNotFoundException e)
                                {
                                    LOG.debug("Silent debug. The file with ID=[{}] is already deleted. Proceed with execution.",
                                            transcribe.getTranscribeEcmFile().getId());
                                }
                            }

                            EcmFile ecmFile = getEcmFileService().upload(fileName, transcribe.getObjectType().toLowerCase(),
                                    TranscribeConstants.FILE_CATEGORY, in, TranscribeConstants.WORD_MIME_TYPE, fileName, authentication,
                                    folder.getCmisFolderId(), parentObjectType, parentObjectId);

                            if (ecmFile != null)
                            {
                                transcribe.setTranscribeEcmFile(ecmFile);
                                Transcribe saved = getTranscribeDao().save(transcribe);
                                getTranscribeEventPublisher().publish(saved, TranscribeActionType.COMPILED.toString());
                                return ecmFile;
                            }

                            throw new CompileTranscribeException(
                                    String.format("Could not compile Transcribe with ID=[%d]. Word file not generated.", id));
                        }
                    }
                    catch (IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
                    {
                        throw new CompileTranscribeException(
                                String.format("Could not compile Transcribe with ID=[%d]. REASON=[%s].", id, e.getMessage()));
                    }
                    finally
                    {
                        FileUtils.deleteQuietly(file);
                    }
                }

                throw new CompileTranscribeException(
                        String.format("Could not compile Transcribe with ID=[%d]. Transcribe items not found.", id));
            }

            throw new CompileTranscribeException(
                    String.format("Could not compile Transcribe with ID=[%d]. Transcribe object not found.", id));
        }

        throw new CompileTranscribeException("Could not compile Transcribe because ID is not provided");
    }

    @Override
    public ProcessInstance startBusinessProcess(Transcribe transcribe)
    {
        LOG.debug("Checking if starting business process is allowed for Transcribe Object [{}]", transcribe);
        ProcessInstance processInstance = null;
        if (transcribe != null)
        {
            // Check drools if we need to start workflow for provided Transcribe object
            TranscribeBusinessProcessModel transcribeBusinessProcessModel = new TranscribeBusinessProcessModel();
            transcribeBusinessProcessModel.setType(transcribe.getType());

            LOG.debug("Executing Drools Business rules for [{}] Transcribe with ID=[{}], MEDIA_FILE_ID=[{}] and MEDIA_FILE_VERSION_ID=[{}]",
                    transcribe.getType(), transcribe.getId(), transcribe.getMediaEcmFileVersion().getFile().getId(),
                    transcribe.getMediaEcmFileVersion().getId());

            transcribeBusinessProcessModel = getTranscribeBusinessProcessRulesExecutor().applyRules(transcribeBusinessProcessModel);

            LOG.debug("Start business process: [{}]", transcribeBusinessProcessModel.isStart());

            if (transcribeBusinessProcessModel.isStart())
            {
                // Check if there is already startes business process. This can be the case when we replace media file
                // and in the Transcribe Configuration (properties file) we have set "copy transcription" instead of
                // "new transcription".
                // In that case we have complete two copies of Transcribe object, just different IDS. So we should use
                // the same Process
                // for both Transcribe objects
                if (StringUtils.isNotEmpty(transcribe.getProcessId()))
                {
                    processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(transcribe.getProcessId())
                            .includeProcessVariables().singleResult();
                }

                if (processInstance == null)
                {
                    // When we don't have process instance, create it
                    processInstance = createProcessInstance(transcribe, transcribeBusinessProcessModel);
                }
                else
                {
                    // When we have process instance, just update the variable 'IDS'
                    updateProcessInstance(transcribe, processInstance);
                }
            }
        }

        LOG.debug("There is no Transcribe Object. It's [{}]", transcribe);

        return processInstance;
    }

    @Override
    public void signal(ProcessInstance processInstance, String status, String action)
    {
        if (processInstance != null && StringUtils.isNotEmpty(status) && StringUtils.isNotEmpty(action))
        {
            String statusKey = TranscribeBusinessProcessVariableKey.STATUS.toString();
            String actionKey = TranscribeBusinessProcessVariableKey.ACTION.toString();
            getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
            getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);
            getActivitiRuntimeService().signal(processInstance.getId());
        }
    }

    @Override
    public TranscribeServiceFactory getTranscribeServiceFactory()
    {
        return transcribeServiceFactory;
    }

    public void setTranscribeServiceFactory(TranscribeServiceFactory transcribeServiceFactory)
    {
        this.transcribeServiceFactory = transcribeServiceFactory;
    }

    @Override
    public TranscribeConfiguration getConfiguration() throws GetConfigurationException
    {
        return getTranscribeConfigurationPropertiesService().get();
    }

    @Override
    public TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveConfigurationException
    {
        return getTranscribeConfigurationPropertiesService().save(configuration);
    }

    @Override
    @Transactional
    public Transcribe create(Transcribe transcribe) throws CreateTranscribeException
    {
        // Here we need transcribe without id - new transcribe
        if (!allow(transcribe.getMediaEcmFileVersion()) || transcribe.getId() != null)
        {
            throw new CreateTranscribeException("Transcribe service is not allowed.");
        }

        Transcribe existingTranscribe = null;
        try
        {
            existingTranscribe = getByMediaVersionId(transcribe.getMediaEcmFileVersion().getId());
        }
        catch (GetTranscribeException e)
        {
            throw new CreateTranscribeException(String.format("Creating Transcribe job is aborted. REASON=[%s]", e.getMessage()), e);
        }

        if (existingTranscribe != null && (TranscribeStatusType.QUEUED.toString().equalsIgnoreCase(existingTranscribe.getStatus()) ||
                TranscribeStatusType.PROCESSING.toString().equalsIgnoreCase(existingTranscribe.getStatus())))
        {
            throw new CreateTranscribeException(
                    String.format("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[%d]",
                            transcribe.getMediaEcmFileVersion().getId()));
        }

        TranscribePipelineContext context = new TranscribePipelineContext();
        context.setEcmFileVersion(transcribe.getMediaEcmFileVersion());
        context.setType(TranscribeType.valueOf(transcribe.getType()));

        try
        {
            Transcribe transcribeForProcessing = existingTranscribe != null ? existingTranscribe : transcribe;
            if (transcribeForProcessing.getId() != null)
            {
                // Reset 'remoteId' for existing Transcriptions that we want to be transcribed again
                transcribeForProcessing.setRemoteId(null);
            }
            Transcribe created = getPipelineManager().executeOperation(transcribeForProcessing, context, () -> {
                try
                {
                    return save(transcribeForProcessing);
                }
                catch (SaveTranscribeException e)
                {
                    throw new PipelineProcessException(
                            String.format("Transcribe for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]",
                                    transcribeForProcessing.getMediaEcmFileVersion() != null
                                            ? transcribeForProcessing.getMediaEcmFileVersion().getId()
                                            : null,
                                    e.getMessage()));
                }
            });

            return created;
        }
        catch (PipelineProcessException e)
        {
            throw new CreateTranscribeException(String.format(
                    "Transcribe for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]",
                    transcribe.getMediaEcmFileVersion() != null ? transcribe.getMediaEcmFileVersion().getId() : null, e.getMessage()), e);
        }
    }

    @Override
    public Transcribe get(String remoteId) throws GetTranscribeException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<Transcribe> getAll() throws GetTranscribeException
    {
        return getTranscribeDao().findAll();
    }

    @Override
    public List<Transcribe> getAllByStatus(String status) throws GetTranscribeException
    {
        return getTranscribeDao().findAllByStatus(status);
    }

    @Override
    public List<Transcribe> getPage(int start, int n) throws GetTranscribeException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<Transcribe> getPageByStatus(int start, int n, String status) throws GetTranscribeException
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean purge(Transcribe transcribe)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean allow(EcmFileVersion ecmFileVersion)
    {
        return isFileVersionTranscribable(ecmFileVersion) &&
                isTranscribeOn() &&
                isMediaDurationAllowed(ecmFileVersion);
    }

    @Override
    public boolean isTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && configuration.isEnabled();

            if (!allow)
            {
                LOG.warn("Transcribe is not enabled. It will be terminated.");
            }

            return allow;
        }
        catch (GetConfigurationException e)
        {
            LOG.error("Failed to retrieve Transcribe configuration.", e);
            return false;
        }
    }

    @Override
    public boolean isAutomaticTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && configuration.isAutomaticEnabled();

            if (!allow)
            {
                LOG.warn("Automatic Transcribe is not enabled. It will be terminated.");
            }

            return allow;
        }
        catch (GetConfigurationException e)
        {
            LOG.warn("Failed to retrieve Transcribe configuration. Automatic Transcribe will be terminated.");
            return false;
        }
    }

    @Override
    public boolean isMediaDurationAllowed(EcmFileVersion ecmFileVersion)
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && ecmFileVersion != null
                    && ecmFileVersion.getDurationSeconds() <= configuration.getAllowedMediaDuration();

            if (!allow)
            {
                LOG.warn("The duration of the media file is more than allowed [{}] seconds. Automatic Transcription will be terminated.",
                        configuration.getAllowedMediaDuration());
            }

            return allow;
        }
        catch (GetConfigurationException e)
        {
            LOG.warn("Failed to retrieve Transcribe configuration. Automatic Transcribe will be terminated.");
            return false;
        }
    }

    @Override
    public boolean isFileVersionTranscribable(EcmFileVersion ecmFileVersion)
    {

        boolean allow = ecmFileVersion != null &&
                ecmFileVersion.getVersionMimeType() != null &&
                (ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_AUDIO_RECOGNITION_KEY) ||
                        ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_VIDEO_RECOGNITION_KEY));

        if (!allow)
        {
            LOG.warn("The media file is not transcribable. Automatic Transcription will be terminated.");
        }

        return allow;
    }

    private ProcessInstance createProcessInstance(Transcribe transcribe, TranscribeBusinessProcessModel transcribeBusinessProcessModel)
    {
        String status = TranscribeType.AUTOMATIC.toString().equalsIgnoreCase(transcribe.getType()) ? TranscribeStatusType.QUEUED.toString()
                : transcribe.getStatus();
        String action = TranscribeType.AUTOMATIC.toString().equalsIgnoreCase(transcribe.getType()) ? TranscribeActionType.QUEUED.toString()
                : transcribe.getStatus();

        List<Long> ids = new ArrayList<>();
        ids.add(transcribe.getId());

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(TranscribeBusinessProcessVariableKey.IDS.toString(), ids);
        processVariables.put(TranscribeBusinessProcessVariableKey.REMOTE_ID.toString(), transcribe.getRemoteId());
        processVariables.put(TranscribeBusinessProcessVariableKey.STATUS.toString(), status);
        processVariables.put(TranscribeBusinessProcessVariableKey.ACTION.toString(), action);
        processVariables.put(TranscribeBusinessProcessVariableKey.TYPE.toString(), transcribe.getType());
        processVariables.put(TranscribeBusinessProcessVariableKey.CREATED.toString(), new Date());

        ProcessInstance processInstance = getActivitiRuntimeService().startProcessInstanceByKey(transcribeBusinessProcessModel.getName(),
                processVariables);

        transcribe.setProcessId(processInstance.getId());

        return processInstance;
    }

    private void updateProcessInstance(Transcribe transcribe, ProcessInstance processInstance)
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get(TranscribeBusinessProcessVariableKey.IDS.toString());
        if (ids != null)
        {
            if (!ids.contains(transcribe.getId()))
            {
                ids.add(transcribe.getId());
            }

            getActivitiRuntimeService().setVariable(processInstance.getId(), TranscribeBusinessProcessVariableKey.IDS.toString(), ids);
        }
    }

    public TranscribeDao getTranscribeDao()
    {
        return transcribeDao;
    }

    public void setTranscribeDao(TranscribeDao transcribeDao)
    {
        this.transcribeDao = transcribeDao;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public TranscribeConfigurationPropertiesService getTranscribeConfigurationPropertiesService()
    {
        return transcribeConfigurationPropertiesService;
    }

    public void setTranscribeConfigurationPropertiesService(
            TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService)
    {
        this.transcribeConfigurationPropertiesService = transcribeConfigurationPropertiesService;
    }

    public PipelineManager<Transcribe, TranscribePipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<Transcribe, TranscribePipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public TranscribeBusinessProcessRulesExecutor getTranscribeBusinessProcessRulesExecutor()
    {
        return transcribeBusinessProcessRulesExecutor;
    }

    public void setTranscribeBusinessProcessRulesExecutor(TranscribeBusinessProcessRulesExecutor transcribeBusinessProcessRulesExecutor)
    {
        this.transcribeBusinessProcessRulesExecutor = transcribeBusinessProcessRulesExecutor;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public ArkCaseBeanUtils getTranscribeArkCaseBeanUtils()
    {
        return transcribeArkCaseBeanUtils;
    }

    public void setTranscribeArkCaseBeanUtils(ArkCaseBeanUtils transcribeArkCaseBeanUtils)
    {
        this.transcribeArkCaseBeanUtils = transcribeArkCaseBeanUtils;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public TranscribeEventPublisher getTranscribeEventPublisher()
    {
        return transcribeEventPublisher;
    }

    public void setTranscribeEventPublisher(TranscribeEventPublisher transcribeEventPublisher)
    {
        this.transcribeEventPublisher = transcribeEventPublisher;
    }

    public AcmMailTemplateConfigurationService getTemplateService()
    {
        return templateService;
    }

    public void setTemplateService(AcmMailTemplateConfigurationService templateService)
    {
        this.templateService = templateService;
    }

    public AcmEmailSenderService getEmailSenderService()
    {
        return emailSenderService;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LabelManagementService getLabelManagementService()
    {
        return labelManagementService;
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }
}
