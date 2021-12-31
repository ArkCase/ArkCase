package com.armedia.acm.services.comprehendmedical.sevice;

/*-
 * #%L
 * ACM Service: Comprehend Medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.comprehendmedical.dao.ComprehendMedicalDao;
import com.armedia.acm.services.comprehendmedical.factory.ComprehendMedicalProviderFactory;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedicalConfiguration;
import com.armedia.acm.services.comprehendmedical.utils.ComprehendMedicalUtils;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.exception.SaveConfigurationException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessModel;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.model.MediaEngineUserType;
import com.armedia.acm.services.mediaengine.service.ArkCaseMediaEngineServiceImpl;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;
import com.armedia.acm.services.tag.service.TagService;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicineDTO;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
public class ArkCaseComprehendMedicalServiceImpl extends ArkCaseMediaEngineServiceImpl<ComprehendMedical>
        implements ArkCaseComprehendMedicalService
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private ComprehendMedicalProviderFactory comprehendMedicalProviderFactory;
    private ComprehendMedicalConfigurationService comprehendMedicalConfigurationService;
    private ComprehendMedicalDao comprehendMedicalDao;
    private UserDao userDao;
    private TagService tagService;
    private AssociatedTagService associatedTagService;
    private NotificationService notificationService;
    private CorrespondenceTemplateManager templateManager;

    @Override
    public MediaEngine getExisting(MediaEngine mediaEngine) throws GetMediaEngineException
    {
        return getByMediaVersionId(mediaEngine.getMediaEcmFileVersion().getId());
    }

    @Override
    public EcmFileVersion getExistingMediaVersionId(MediaEngine mediaEngine) throws CreateMediaEngineException
    {
        throw new CreateMediaEngineException(
                String.format(
                        "Creating ComprehendMedical job is aborted. There is already ComprehendMedical object for MEDIA_FILE_VERSION_ID=[%d]",
                        mediaEngine.getMediaEcmFileVersion().getId()));
    }

    @Override
    public String resetRemoteId(MediaEngine mediaEngine)
    {
        if (mediaEngine.getId() != null)
        {
            // Reset 'remoteId' for existing Transcriptions that we want to be transcribed again
            return null;
        }
        return mediaEngine.getRemoteId();
    }

    @Override
    protected MediaEngine createEntity()
    {
        return new ComprehendMedical();
    }

    @Override
    protected MediaEngineBusinessProcessModel createMediaEngineBusinessProcessModelEntity()
    {
        return new MediaEngineBusinessProcessModel();
    }

    @Override
    public boolean purge(MediaEngine mediaEngine) throws GetConfigurationException, MediaEngineProviderNotFound
    {
        ComprehendMedicalConfiguration configuration = getComprehendMedicalConfigurationService().loadProperties();
        String providerName = configuration.getProvider();
        MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
        mediaEngineDTO.setMediaEcmFileVersion(
                new File((String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), "UPLOADED_TMP")));
        return getComprehendMedicalProviderFactory().getProvider(providerName).purge(mediaEngineDTO);
    }

    @Override
    public void notify(Long id, String action)
    {
        if (id != null && StringUtils.isNotEmpty(action))
        {
            ComprehendMedical comprehendMedical = getComprehendMedicalDao().find(id);
            if (comprehendMedical != null)
            {
                // Take users: owner of the media file and owner of the parent object
                List<AcmUser> users = new ArrayList<>();

                getUsersToNotify(users, comprehendMedical);

                String templateName;
                Template template;
                String emailSubject = "";
                if (!action.equals("QUEUED"))
                {
                    templateName = "transcribeStatus";
                    template = templateManager.findTemplate("transcribeStatus.html");
                }
                else
                {
                    templateName = "transcribeQueued";
                    template = templateManager.findTemplate("transcribeQueued.html");
                }
                if (template != null)
                {
                    emailSubject = template.getEmailSubject();
                }

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification(templateName, NotificationConstants.STATUS_COMPREHEND_MEDICAL, comprehendMedical.getObjectType(),
                                comprehendMedical.getMediaEcmFileVersion().getId(), null)
                        .withNote(action)
                        .withData(comprehendMedical.getMediaEcmFileVersion().getFile().getFileName())
                        .withEmailAddressesForUsers(users)
                        .withSubject(emailSubject)
                        .build();

                notificationService.saveNotification(notification);
            }
        }
    }

    private void getUsersToNotify(List<AcmUser> users, ComprehendMedical comprehendMedical)
    {
        String userIdOwnerOfMedia = getUserIdForGivenUserType(MediaEngineUserType.OWNER_OF_MEDIA.toString(), comprehendMedical);
        String userIdOwnerOfParentObject = getUserIdForGivenUserType(MediaEngineUserType.OWNER_OF_PARENT_OBJECT.toString(),
                comprehendMedical);

        getUserOwnerOfMediaToNotify(users, userIdOwnerOfMedia);

        getUserOwnerOfParentObjectToNotify(users, userIdOwnerOfParentObject);
    }

    private String getUserIdForGivenUserType(String userType, ComprehendMedical comprehendMedical)
    {
        String userId = null;
        MediaEngineUserType type = MediaEngineUserType.valueOf(userType);
        switch (type)
        {
        case OWNER_OF_MEDIA:
            // First try to find assignee
            userId = getOwnerOfMedia(comprehendMedical);
            break;
        case OWNER_OF_PARENT_OBJECT:
            userId = getOwnerOfParentObject(comprehendMedical, userId);
            break;
        }

        return userId;
    }

    private String getOwnerOfParentObject(ComprehendMedical comprehendMedical, String userId)
    {
        Long objectId = comprehendMedical.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId();
        String objectType = comprehendMedical.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType();

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
        return userId;
    }

    private String getOwnerOfMedia(ComprehendMedical comprehendMedical)
    {
        String userId;
        userId = ParticipantUtils.getAssigneeIdFromParticipants(comprehendMedical.getMediaEcmFileVersion().getFile().getParticipants());
        if (StringUtils.isEmpty(userId))
        {
            // If there is no assignee, take the creator
            userId = comprehendMedical.getMediaEcmFileVersion().getFile().getCreator();
        }
        return userId;
    }

    private void getUserOwnerOfMediaToNotify(List<AcmUser> users, String userIdOwnerOfMedia)
    {
        AcmUser userOwnerOfMedia;
        if (StringUtils.isNotEmpty(userIdOwnerOfMedia))
        {
            userOwnerOfMedia = getUserDao().findByUserId(userIdOwnerOfMedia);
            if (userOwnerOfMedia != null && StringUtils.isNotEmpty(userOwnerOfMedia.getMail()))
            {
                users.add(userOwnerOfMedia);
            }
        }
    }

    private void getUserOwnerOfParentObjectToNotify(List<AcmUser> users, String userIdOwnerOfParentObject)
    {
        AcmUser userOwnerOfParentObject;
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
    public MediaEngineConfiguration getConfiguration() throws GetConfigurationException
    {
        return getComprehendMedicalConfigurationService().loadProperties();
    }

    @Override
    public void saveConfiguration(MediaEngineConfiguration configuration)
            throws SaveConfigurationException, MediaEngineServiceNotFoundException
    {
        try
        {
            getComprehendMedicalConfigurationService().saveProperties(configuration);
        }
        catch (ConfigurationPropertyException e)
        {
            throw new SaveConfigurationException(e);
        }
    }

    @Override
    public boolean allow(EcmFileVersion ecmFileVersion)
    {
        return ecmFileVersion.isValidFile() && isServiceEnabled() &&
                !isExcludedFileTypes(ecmFileVersion.getFile().getFileType()) &&
                isProcessable(ecmFileVersion);
    }

    @Override
    public void checkStatus(DelegateExecution delegateExecution)
    {
        getAuditPropertyEntityAdapter().setUserId(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());
        String action = MediaEngineActionType.PROCESSING.toString();
        String previousAction = (String) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString());

        if (ids != null && !ids.isEmpty() && action.equalsIgnoreCase(previousAction))
        {
            // Because all IDs are follow the same business process (objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                if (MediaEngineStatusType.PROCESSING.toString().equals(mediaEngine.getStatus()))
                {
                    ComprehendMedicineDTO providerDTO = getProviderDTO(mediaEngine);

                    if (providerDTO != null && !MediaEngineStatusType.PROCESSING.toString().equals(providerDTO.getStatus()))
                    {
                        String status = providerDTO.getStatus();

                        switch (MediaEngineStatusType.valueOf(providerDTO.getStatus()))
                        {
                        case PROCESSING:
                            action = MediaEngineActionType.PROCESSING.toString();
                            break;
                        case COMPLETED:
                            action = doComplete(ids, providerDTO, delegateExecution);
                            break;
                        case FAILED:
                            action = doFailed(mediaEngine);
                            break;

                        default:
                            throw new RuntimeException(
                                    String.format(
                                            "Received ComprehendMedical status type of [%s] for COMPREHEND_MEDICAL_ID=[%s] and FILE_ID=[%s], but cannot handle it.",
                                            status, mediaEngine.getId(), mediaEngine.getMediaEcmFileVersion().getFile().getId()));
                        }

                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(), status);
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.MESSAGE.toString(),
                                providerDTO.getMessage());
                    }
                }
            }
            catch (GetMediaEngineException | GetMediaEngineToolException | MediaEngineProviderNotFound e)
            {
                LOG.warn("Could not check if ComprehendMedical should be completed. REASON=[{}]", e.getMessage());
            }

            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
        }
    }

    private ComprehendMedicineDTO getProviderDTO(MediaEngine mediaEngine)
            throws GetMediaEngineToolException, MediaEngineProviderNotFound
    {
        ComprehendMedicalConfiguration configuration = getComprehendMedicalConfigurationService().loadProperties();
        String providerName = configuration.getProvider();

        Map<String, Object> props = new HashMap<>();
        props.put("jobId", ((ComprehendMedical) mediaEngine).getJobId());
        return (ComprehendMedicineDTO) getComprehendMedicalProviderFactory().getProvider(providerName).get(mediaEngine.getRemoteId(),
                props);
    }

    private String doComplete(List<Long> ids, ComprehendMedicineDTO comprehendMedicineDTO, DelegateExecution delegateExecution)
    {
        if (ids != null)
        {
            ids.forEach(id -> doComplete(id, comprehendMedicineDTO, delegateExecution));
        }

        return MediaEngineActionType.COMPLETED.toString();
    }

    private void doComplete(Long id, ComprehendMedicineDTO comprehendMedicineDTO, DelegateExecution delegateExecution)
    {
        try
        {
            ComprehendMedical comprehendMedical = (ComprehendMedical) get(id);
            comprehendMedical.setEntities(ComprehendMedicalUtils.getEntitiesFromOutput(comprehendMedicineDTO.getOutput()));
            save(comprehendMedical);

            addTagsToFile(comprehendMedical);

            getObjectLockingManager().releaseObjectLock(comprehendMedical.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE, true,
                    ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER, null);
        }
        catch (GetMediaEngineException | SaveMediaEngineException e)
        {
            LOG.warn("Taking items for ComprehendMedical with ID=[{}] and PROCESS_ID=[{}] failed. REASON=[{}]", id,
                    delegateExecution.getProcessInstanceId(), e.getMessage());
        }
    }

    private void addTagsToFile(ComprehendMedical comprehendMedical)
    {
        if (comprehendMedical != null && comprehendMedical.getEntities() != null)
        {
            for (int i = 0; i < comprehendMedical.getEntities().size(); i++)
            {
                String text = comprehendMedical.getEntities().get(i).getText();
                String objectType = comprehendMedical.getMediaEcmFileVersion().getFile().getObjectType();
                Long objectId = comprehendMedical.getMediaEcmFileVersion().getFile().getId();
                String objectTitle = comprehendMedical.getMediaEcmFileVersion().getFile().getFileName();
                addTagToFile(text, objectType, objectId, objectTitle);

                // So far attributes we don't need in tags, just entities. But once we need them, uncomment the code
                // below
                /*
                 * if (comprehendMedical.getEntities().get(i).getAttributes() != null)
                 * {
                 * for (int j = 0; j < comprehendMedical.getEntities().get(i).getAttributes().size(); j++)
                 * {
                 * text = comprehendMedical.getEntities().get(i).getAttributes().get(j).getText();
                 * objectType = comprehendMedical.getMediaEcmFileVersion().getFile().getObjectType();
                 * objectId = comprehendMedical.getMediaEcmFileVersion().getFile().getId();
                 * objectTitle = comprehendMedical.getMediaEcmFileVersion().getFile().getFileName();
                 * addTagToFile(text, objectType, objectId, objectTitle);
                 * }
                 * }
                 */
            }
        }
    }

    private void addTagToFile(String text, String objectType, Long objectId, String objectTitle)
    {
        String entityText = text;
        AcmTag entityTag = getTagService().getTagByTextOrDescOrName(entityText, entityText, entityText);
        if (entityTag == null)
        {
            entityTag = getTagService().saveTag(entityText, entityText, entityText);
        }

        AcmAssociatedTag associatedTag = null;

        try
        {
            associatedTag = getAssociatedTagService().getAssociatedTagByTagIdAndObjectIdAndType(entityTag.getId(), objectId, objectType);
        }
        catch (Exception e)
        {
            LOG.debug("There is no associated tag for this file for objectId=[{}], objectType=[{}] and tag=[{}]", objectId, objectType,
                    entityTag.getId());
        }

        if (associatedTag == null)
        {
            getAssociatedTagService().saveAssociateTag(objectType, objectId, objectTitle, entityTag);
        }
    }

    private String doFailed(MediaEngine mediaEngine)
    {
        getObjectLockingManager().releaseObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE,
                true,
                ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER, null);

        return MediaEngineActionType.FAILED.toString();
    }

    @Override
    public void process(DelegateExecution delegateExecution)
    {
        getAuditPropertyEntityAdapter().setUserId(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                if (MediaEngineStatusType.QUEUED.toString().equals(mediaEngine.getStatus()))
                {
                    ComprehendMedicalConfiguration configuration = getComprehendMedicalConfigurationService().loadProperties();
                    List<MediaEngine> processingObjects = getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
                    List<MediaEngine> processingAutomaticObjects = processingObjects.stream()
                            .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
                    List<MediaEngine> processingObjectsDistinctByProcessId = processingAutomaticObjects.stream()
                            .filter(ComprehendMedicalUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingObjectsDistinctByProcessId.size())
                    {
                        moveFromQueue(delegateExecution, mediaEngine, configuration);
                    }
                }

            }
            catch (GetMediaEngineException e)
            {
                LOG.warn("Could not check if ComprehendMedical should be processed. REASON=[{}]", e.getMessage());
            }
        }
    }

    private void moveFromQueue(DelegateExecution delegateExecution, MediaEngine mediaEngine, ComprehendMedicalConfiguration configuration)
    {
        try
        {
            acquireLock(mediaEngine);
            if (mediaEngine.getProcessId() == null)
            {
                mediaEngine.setProcessId(delegateExecution.getProcessInstanceId());
            }
            String providerName = configuration.getProvider();
            // Create Job on provider side and set the Status and Action to PROCESSING
            MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
            mediaEngineDTO.setMediaEcmFileVersion(createTempFile(mediaEngine, configuration.getTempPath()));
            mediaEngineDTO.getProperties().put("fileSize", mediaEngineDTO.getMediaEcmFileVersion().length() + "");
            mediaEngineDTO.getProperties().put("mimeType", "text/plain");
            getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), "UPLOADED_TMP",
                    mediaEngineDTO.getMediaEcmFileVersion().getAbsolutePath());
            mediaEngineDTO = getComprehendMedicalProviderFactory().getProvider(providerName).create(mediaEngineDTO);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(),
                    MediaEngineStatusType.PROCESSING.toString());
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PROCESSING.toString());

            if (mediaEngine instanceof ComprehendMedical)
            {
                ((ComprehendMedical) mediaEngine).setJobId(mediaEngineDTO.getJobId());

                save(mediaEngine);
            }
        }
        catch (Exception e)
        {
            LOG.error("Error while calling PROVIDER=[{}] to comprehend medicine. REASON=[{}]",
                    configuration.getProvider(), e.getMessage(), e);
        }
    }

    @Override
    public File createTempFile(MediaEngine mediaEngine, String tempPath) throws IOException, ArkCaseFileRepositoryException
    {
        InputStream mediaVersionInputStream = getEcmFileTransaction()
                .downloadFileTransactionAsInputStream(mediaEngine.getMediaEcmFileVersion().getFile());

        String text = "";

        if (mediaEngine.getMediaEcmFileVersion().getVersionMimeType().startsWith("application/pdf") &&
                mediaEngine.getMediaEcmFileVersion().isSearchablePDF())
        {
            text = getTextFromPDF(mediaVersionInputStream);
        }
        else if (mediaEngine.getMediaEcmFileVersion().getVersionMimeType().startsWith("text/plain"))
        {
            text = getTextFromPlainText(mediaVersionInputStream);
        }
        else if (mediaEngine.getMediaEcmFileVersion().getVersionMimeType().startsWith("application/msword") ||
                mediaEngine.getMediaEcmFileVersion().getVersionMimeType()
                        .startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
        {
            text = getTextFromWord(mediaVersionInputStream);
        }
        else
        {
            throw new ArkCaseFileRepositoryException("File is not PlainText, searchable PDF or Word document");
        }

        File tmpFile = new File(tempPath + mediaEngine.getRemoteId() + "."
                + mediaEngine.getMediaEcmFileVersion().getFile().getFileExtension());

        FileUtils.writeStringToFile(tmpFile, text, "UTF-8");

        mediaVersionInputStream.close();

        return tmpFile;
    }

    private String getTextFromPDF(InputStream inputStream) throws IOException
    {
        String text = "";
        try
        {

            PdfReader reader = new PdfReader(inputStream);

            for (int i = 1; i <= reader.getNumberOfPages(); i++)
            {
                text += PdfTextExtractor.getTextFromPage(reader, i);
            }
        }
        catch (IOException e)
        {
            LOG.error("Could not take the text from the PDF file", e);
            throw e;
        }

        return text;
    }

    private String getTextFromPlainText(InputStream inputStream) throws IOException
    {
        String text = "";

        try
        {
            text = IOUtils.toString(inputStream);
        }
        catch (IOException e)
        {
            LOG.error("Could not take the text from the PlainText file", e);
            throw e;
        }

        return text;
    }

    private String getTextFromWord(InputStream inputStream) throws IOException
    {
        String text = "";

        try
        {
            XWPFDocument doc = new XWPFDocument(inputStream);
            text = new XWPFWordExtractor(doc).getText();
        }
        catch (IOException e)
        {
            LOG.error("Could not take the text from the Word file", e);
            throw e;
        }

        return text;
    }

    private void acquireLock(MediaEngine mediaEngine)
    {
        AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE);
        if (lock == null || lock.getCreator().equalsIgnoreCase(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER))
        {
            getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE,
                    MediaEngineConstants.LOCK_TYPE_WRITE,
                    null,
                    true,
                    ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);
        }
        else
        {
            throw new AcmObjectLockException(String.format("Cannot acquire lock object with id={%d}!", mediaEngine.getId()));
        }
    }

    @Override
    public void purge(DelegateExecution delegateExecution)
    {
        LOG.debug("Purge Comprehend Medicine information");

        getAuditPropertyEntityAdapter().setUserId(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many)
            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                ComprehendMedicalConfiguration configuration = getComprehendMedicalConfigurationService().loadProperties();
                int purgeAttempts = 0;
                int purgeAttemptsInConfiguration = configuration.getProviderPurgeAttempts();
                if (delegateExecution.hasVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString()))
                {
                    purgeAttempts = (int) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString());
                }

                if (purgeAttempts < purgeAttemptsInConfiguration)
                {

                    boolean purged = purge(mediaEngine);

                    if (purged)
                    {
                        LOG.debug(
                                "Comprehend Medicine information for ComprehendMedicine object with REMOTE_ID=[{}] on provider side are purged.",
                                mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn(
                                "Comprehend Medicine information for ComprehendMedicine object with REMOTE_ID=[{}] on provider side are not purged.",
                                mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for ComprehendMedicine object with REMOTE_ID=[{}] exceeded. Terminating purge job.",
                            mediaEngine.getRemoteId());
                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                            MediaEngineActionType.PURGE_TERMINATE.toString());
                }
            }
            catch (Exception e)
            {
                LOG.warn("Could not purge Comprehend Medicine information on provider side. REASON=[{}]", e.getMessage());
                delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                        MediaEngineActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job cannot proceed because there is no ComprehendMedicine object. Terminating purge job.");
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PURGE_TERMINATE.toString());
        }
    }

    @Override
    public void removeProcessId(DelegateExecution delegateExecution)
    {
        LOG.debug("Remove Process ID ComprehendMedical information");

        getAuditPropertyEntityAdapter().setUserId(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            ids.forEach(id -> {
                try
                {
                    MediaEngine mediaEngine = get(id);
                    mediaEngine.setProcessId(null);
                    save(mediaEngine);
                }
                catch (Exception e)
                {
                    LOG.error("Could not remove process id from ComprehendMedical. REASON=[{}]", e.getMessage());
                }
            });
        }
    }

    @Override
    public boolean isProcessable(EcmFileVersion ecmFileVersion)
    {
        // TODO: Check for correct files that can be processed
        boolean allow = ecmFileVersion != null &&
                ecmFileVersion.getVersionMimeType() != null && (ecmFileVersion.isSearchablePDF() ||
                        ecmFileVersion.getVersionMimeType().startsWith("text/plain") ||
                        ecmFileVersion.getVersionMimeType().startsWith("application/msword") ||
                        ecmFileVersion.getVersionMimeType()
                                .startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

        if (!allow)
        {
            LOG.warn("The media file is not processable. Automatic Comprehend Medicine will be terminated.");
        }

        return allow;
    }

    @Override
    public String getServiceName()
    {
        return ComprehendMedicalConstants.SERVICE;
    }

    @Override
    public String getSystemUser()
    {
        return ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER;
    }

    @Override
    public MediaEngine getForCopiedFile(Long fileId, Long versionId) throws GetMediaEngineException
    {
        return getByMediaVersionId(versionId);
    }

    public ComprehendMedicalProviderFactory getComprehendMedicalProviderFactory()
    {
        return comprehendMedicalProviderFactory;
    }

    public void setComprehendMedicalProviderFactory(ComprehendMedicalProviderFactory comprehendMedicalProviderFactory)
    {
        this.comprehendMedicalProviderFactory = comprehendMedicalProviderFactory;
    }

    public ComprehendMedicalConfigurationService getComprehendMedicalConfigurationService()
    {
        return comprehendMedicalConfigurationService;
    }

    public void setComprehendMedicalConfigurationService(ComprehendMedicalConfigurationService comprehendMedicalConfigurationService)
    {
        this.comprehendMedicalConfigurationService = comprehendMedicalConfigurationService;
    }

    public ComprehendMedicalDao getComprehendMedicalDao()
    {
        return comprehendMedicalDao;
    }

    public void setComprehendMedicalDao(ComprehendMedicalDao comprehendMedicalDao)
    {
        this.comprehendMedicalDao = comprehendMedicalDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

    public AssociatedTagService getAssociatedTagService()
    {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService)
    {
        this.associatedTagService = associatedTagService;
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }
}
