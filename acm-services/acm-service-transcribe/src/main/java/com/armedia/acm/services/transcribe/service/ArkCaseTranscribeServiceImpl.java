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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.labels.service.LabelManagementService;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
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
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.exception.CompileMediaEngineException;
import com.armedia.acm.services.transcribe.factory.TranscribeProviderFactory;
import com.armedia.acm.services.transcribe.mapper.ItemsMapper;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.transcribe.model.TranscribeDTO;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class ArkCaseTranscribeServiceImpl extends ArkCaseMediaEngineServiceImpl<Transcribe>
        implements ArkCaseTranscribeService
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private TranscribeDao transcribeDao;
    private UserDao userDao;
    private LabelManagementService labelManagementService;
    private ItemsMapper itemsMapper;
    private TranscribeProviderFactory transcribeProviderFactory;
    private EcmFileDao ecmFileDao;
    private TranscribeConfigurationService transcribeConfigurationService;
    private NotificationService notificationService;
    private CorrespondenceTemplateManager templateManager;

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

                getUsersToNotify(users, transcribe);

                String templateName;
                String emailSubject = "";
                Template template;

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
                        .newNotification(templateName, NotificationConstants.STATUS_TRANSCRIPTION, transcribe.getObjectType(),
                                transcribe.getMediaEcmFileVersion().getId(), null)
                        .withEmailAddressesForUsers(users)
                        .withData(transcribe.getMediaEcmFileVersion().getFile().getFileName())
                        .withNote(action)
                        .withSubject(emailSubject)
                        .build();

                notificationService.saveNotification(notification);
            }
        }
    }

    private void getUsersToNotify(List<AcmUser> users, Transcribe transcribe)
    {
        String userIdOwnerOfMedia = getUserIdForGivenUserType(MediaEngineUserType.OWNER_OF_MEDIA.toString(), transcribe);
        String userIdOwnerOfParentObject = getUserIdForGivenUserType(MediaEngineUserType.OWNER_OF_PARENT_OBJECT.toString(),
                transcribe);

        getUserOwnerOfMediaToNotify(users, userIdOwnerOfMedia);

        getUserOwnerOfParentObjectToNotify(users, userIdOwnerOfParentObject);
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

    private String getUserIdForGivenUserType(String userType, Transcribe transcribe)
    {
        String userId = null;
        MediaEngineUserType type = MediaEngineUserType.valueOf(userType);
        switch (type)
        {
        case OWNER_OF_MEDIA:
            // First try to find assignee
            userId = getOwnerOfMedia(transcribe);
            break;
        case OWNER_OF_PARENT_OBJECT:
            userId = getOwnerOfParentObject(transcribe, userId);
            break;
        }

        return userId;
    }

    private String getOwnerOfParentObject(Transcribe transcribe, String userId)
    {
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
        return userId;
    }

    private String getOwnerOfMedia(Transcribe transcribe)
    {
        String userId;
        userId = ParticipantUtils.getAssigneeIdFromParticipants(transcribe.getMediaEcmFileVersion().getFile().getParticipants());
        if (StringUtils.isEmpty(userId))
        {
            // If there is no assignee, take the creator
            userId = transcribe.getMediaEcmFileVersion().getFile().getCreator();
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
    public EcmFile compile(Long id) throws CompileMediaEngineException
    {
        if (id != null)
        {
            Transcribe transcribe = getTranscribeDao().find(id);
            if (transcribe != null)
            {
                if (transcribe.getTranscribeItems() != null && !transcribe.getTranscribeItems().isEmpty())
                {
                    File file = null;
                    return doCompile(id, transcribe, file);
                }

                throw new CompileMediaEngineException(
                        String.format("Could not compile Transcribe with ID=[%d]. Transcribe items not found.", id));
            }

            throw new CompileMediaEngineException(
                    String.format("Could not compile Transcribe with ID=[%d]. Transcribe object not found.", id));
        }

        throw new CompileMediaEngineException("Could not compile Transcribe because ID is not provided");
    }

    private EcmFile doCompile(Long id, Transcribe transcribe, File file) throws CompileMediaEngineException
    {
        try
        {
            file = File.createTempFile(TranscribeConstants.TEMP_FILE_PREFIX, TranscribeConstants.TEMP_FILE_SUFFIX);

            try (InputStream in = new FileInputStream(file);
                    OutputStream out = new FileOutputStream(file);
                    XWPFDocument document = new XWPFDocument())
            {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                String text = TranscribeUtils.getText(transcribe.getTranscribeItems());
                List<String> words = new ArrayList(Arrays.asList(text.split(" ")));
                String previousSpeaker = "";
                String currentSpeaker = "";

                for (ListIterator<String> iter = words.listIterator(); iter.hasNext();)
                {
                    String word = iter.next();
                    if (word.startsWith("[") && word.endsWith("]:"))
                    {
                        currentSpeaker = word;
                        if (currentSpeaker.equals(previousSpeaker))
                        {
                            iter.remove();
                        }
                        previousSpeaker = currentSpeaker;
                        currentSpeaker = "";
                    }
                }

                text = words.stream()
                        .collect(Collectors.joining(" "));
                run.setText(text);
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
                deleteExistingFile(transcribe);

                EcmFile ecmFile = getEcmFileService().upload(fileName, transcribe.getObjectType().toLowerCase(),
                        TranscribeConstants.FILE_CATEGORY, in, TranscribeConstants.WORD_MIME_TYPE, fileName, authentication,
                        folder.getCmisFolderId(), parentObjectType, parentObjectId);

                if (ecmFile != null)
                {
                    transcribe.setTranscribeEcmFile(ecmFile);
                    Transcribe saved = getTranscribeDao().save(transcribe);
                    getMediaEngineEventPublisher().publish(saved, MediaEngineActionType.COMPILED.toString(), getServiceName(), "");
                    return ecmFile;
                }

                throw new CompileMediaEngineException(
                        String.format("Could not compile Transcribe with ID=[%d]. Word file not generated.", id));
            }
        }
        catch (IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            throw new CompileMediaEngineException(
                    String.format("Could not compile Transcribe with ID=[%d]. REASON=[%s].", id, e.getMessage()));
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }
    }

    private void deleteExistingFile(Transcribe transcribe) throws AcmUserActionFailedException
    {
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
    }

    @Override
    public String getServiceName()
    {
        return TranscribeConstants.SERVICE;
    }

    @Override
    public MediaEngine getExisting(MediaEngine mediaEngine) throws GetMediaEngineException
    {
        return getByMediaVersionId(mediaEngine.getMediaEcmFileVersion().getId());
    }

    @Override
    public EcmFileVersion getExistingMediaVersionId(MediaEngine mediaEngine) throws CreateMediaEngineException
    {
        throw new CreateMediaEngineException(
                String.format("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[%d]",
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
    public Transcribe get(String remoteId) throws GetMediaEngineException
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean purge(MediaEngine mediaEngine) throws MediaEngineProviderNotFound
    {
        TranscribeConfiguration configuration = getTranscribeConfigurationService().loadProperties();
        String providerName = configuration.getProvider();
        MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
        mediaEngineDTO.setMediaEcmFileVersion(
                new File((String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), "UPLOADED_TMP")));
        return getTranscribeProviderFactory().getProvider(providerName).purge(mediaEngineDTO);
    }

    @Override
    public boolean allow(EcmFileVersion ecmFileVersion)
    {
        return ecmFileVersion.isValidFile() && isServiceEnabled() &&
                !isExcludedFileTypes(ecmFileVersion.getFile().getFileType()) &&
                isProcessable(ecmFileVersion) &&
                isMediaDurationAllowed(ecmFileVersion);
    }

    @Override
    public boolean isMediaDurationAllowed(EcmFileVersion ecmFileVersion)
    {
        TranscribeConfiguration configuration = getTranscribeConfigurationService().loadProperties();
        boolean allow = configuration != null && ecmFileVersion != null
                && ecmFileVersion.getDurationSeconds() <= configuration.getAllowedMediaDuration();

        if (!allow)
        {
            LOG.warn(
                    "The duration of the media file [{}] is more than allowed [{}] seconds. Automatic Transcription will be terminated.",
                    ecmFileVersion.getDurationSeconds(), configuration != null ? configuration.getAllowedMediaDuration() : "unknown");
        }

        return allow;
    }

    @Override
    public boolean isProcessable(EcmFileVersion ecmFileVersion)
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

    @Override
    public MediaEngine createCopyObject(MediaEngine mediaEngine, EcmFileVersion ecmFileVersion)
    {
        Transcribe copy = null;
        try
        {
            copy = new Transcribe();
            copy.setMediaEcmFileVersion(ecmFileVersion);
            getArkCaseBeanUtils().copyProperties(copy, mediaEngine);

            List<TranscribeItem> items = ((Transcribe) mediaEngine).getTranscribeItems();
            List<TranscribeItem> copyItems = new ArrayList<>();

            for (TranscribeItem item : items)
            {
                TranscribeItem copyItem = new TranscribeItem();
                getArkCaseBeanUtils().copyProperties(copyItem, item);
                copyItems.add(copyItem);
            }

            copy.setTranscribeItems(copyItems);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for [SERVICE] object with ID=[{}]. REASON=[{}]",
                    mediaEngine != null ? mediaEngine.getId() : null, e.getMessage());
        }
        return copy;
    }

    @Override
    protected MediaEngine createEntity()
    {
        return new Transcribe();
    }

    @Override
    protected MediaEngineBusinessProcessModel createMediaEngineBusinessProcessModelEntity()
    {
        return new MediaEngineBusinessProcessModel();
    }

    @Override
    public void checkStatus(DelegateExecution delegateExecution)
    {
        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());
        String action = MediaEngineActionType.PROCESSING.toString();
        String previousAction = (String) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString());

        if (ids != null && !ids.isEmpty() && action.equalsIgnoreCase(previousAction))
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                if (MediaEngineStatusType.PROCESSING.toString().equals(mediaEngine.getStatus()))
                {
                    TranscribeDTO providerDTO = getProviderDTO(mediaEngine);

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
                                            "Received Transcribe status type of [%s] for TRANSCRIBE_ID=[%s] and FILE_ID=[%s], but cannot handle it.",
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
                LOG.warn("Could not check if Transcribe should be completed. REASON=[{}]", e.getMessage());
            }

            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
        }
    }

    private String doFailed(MediaEngine mediaEngine)
    {
        getObjectLockingManager().releaseObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE,
                true,
                TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null);

        return MediaEngineActionType.FAILED.toString();
    }

    private String doComplete(List<Long> ids, TranscribeDTO transcribeDTO, DelegateExecution delegateExecution)
    {
        if (ids != null)
        {
            ids.forEach(id -> doComplete(id, transcribeDTO, delegateExecution));
        }

        return MediaEngineActionType.COMPLETED.toString();
    }

    private void doComplete(Long id, TranscribeDTO transcribeDTO, DelegateExecution delegateExecution)
    {
        try
        {
            Transcribe transcribe = (Transcribe) get(id);
            List<TranscribeItem> items = getItemsMapper().DTOStoTranscribeItem(transcribeDTO.getTranscribeItems());

            transcribe.setTranscribeItems(TranscribeUtils.clone(items));
            save(transcribe);

            getObjectLockingManager().releaseObjectLock(transcribe.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE, true,
                    TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null);
        }
        catch (GetMediaEngineException | SaveMediaEngineException e)
        {
            LOG.warn("Taking items for Transcribe with ID=[{}] and PROCESS_ID=[{}] failed. REASON=[{}]", id,
                    delegateExecution.getProcessInstanceId(), e.getMessage());
        }
    }

    private TranscribeDTO getProviderDTO(MediaEngine mediaEngine)
            throws GetMediaEngineToolException, MediaEngineProviderNotFound
    {
        TranscribeConfiguration configuration = getTranscribeConfigurationService().loadProperties();
        String providerName = configuration.getProvider();

        Map<String, Object> props = new HashMap<>();
        props.put("silentBetweenWords", configuration.getSilentBetweenWords());
        props.put("wordCountPerItem", configuration.getWordCountPerItem());
        return (TranscribeDTO) getTranscribeProviderFactory().getProvider(providerName).get(mediaEngine.getRemoteId(),
                props);
    }

    @Override
    public void process(DelegateExecution delegateExecution)
    {
        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                if (MediaEngineStatusType.QUEUED.toString().equals(mediaEngine.getStatus()))
                {
                    TranscribeConfiguration configuration = getTranscribeConfigurationService().loadProperties();
                    List<MediaEngine> processingTranscribeObjects = getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
                    List<MediaEngine> processingTranscribeAutomaticObjects = processingTranscribeObjects.stream()
                            .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
                    List<MediaEngine> processingTranscribeObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream()
                            .filter(TranscribeUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingTranscribeObjectsDistinctByProcessId.size())
                    {
                        moveFromQueue(delegateExecution, mediaEngine, configuration);
                    }
                }

            }
            catch (GetMediaEngineException e)
            {
                LOG.warn("Could not check if Transcribe should be processed. REASON=[{}]", e.getMessage());
            }
        }
    }

    private void moveFromQueue(DelegateExecution delegateExecution, MediaEngine mediaEngine, TranscribeConfiguration configuration)
    {
        try
        {
            acquireLock(mediaEngine);
            if (mediaEngine.getProcessId() == null)
            {
                mediaEngine.setProcessId(delegateExecution.getProcessInstanceId());
            }
            String providerName = configuration.getProvider();
            // Create Transcribe Job on provider side and set the Status and Action to PROCESSING
            MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
            mediaEngineDTO.setMediaEcmFileVersion(createTempFile(mediaEngine, configuration.getTempPath()));
            getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), "UPLOADED_TMP",
                    mediaEngineDTO.getMediaEcmFileVersion().getAbsolutePath());
            getTranscribeProviderFactory().getProvider(providerName).create(mediaEngineDTO);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(),
                    MediaEngineStatusType.PROCESSING.toString());
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PROCESSING.toString());
        }
        catch (CreateMediaEngineToolException | AcmObjectLockException | MediaEngineProviderNotFound | IOException
                | ArkCaseFileRepositoryException e)
        {
            LOG.error("Error while calling PROVIDER=[{}] to transcribe the media. REASON=[{}]",
                    configuration.getProvider(), e.getMessage(), e);
        }
    }

    private void acquireLock(MediaEngine mediaEngine)
    {
        AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE);
        if (lock == null || lock.getCreator().equalsIgnoreCase(TranscribeConstants.TRANSCRIBE_SYSTEM_USER))
        {
            getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE,
                    MediaEngineConstants.LOCK_TYPE_WRITE,
                    null,
                    true,
                    TranscribeConstants.TRANSCRIBE_SYSTEM_USER);
        }
        else
        {
            throw new AcmObjectLockException(String.format("Cannot acquire lock object with id={%d}!", mediaEngine.getId()));
        }
    }

    @Override
    public void purge(DelegateExecution delegateExecution)
    {
        LOG.debug("Purge Transcribe information");

        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many)
            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                TranscribeConfiguration configuration = getTranscribeConfigurationService().loadProperties();
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
                        LOG.debug("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are purged.",
                                mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are not purged.",
                                mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for Transcribe with REMOTE_ID=[{}] exceeded. Terminating purge job.",
                            mediaEngine.getRemoteId());
                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                            MediaEngineActionType.PURGE_TERMINATE.toString());
                }
            }
            catch (Exception e)
            {
                LOG.warn("Could not purge Transcribe information on provider side. REASON=[{}]", e.getMessage());
                delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                        MediaEngineActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job cannot proceed because there is no Transcribe. Terminating purge job.");
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PURGE_TERMINATE.toString());
        }
    }

    @Override
    public void removeProcessId(DelegateExecution delegateExecution)
    {
        LOG.debug("Remove Process ID Transcribe information");

        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

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
                    LOG.error("Could not remove process id from Transcribe. REASON=[{}]", e.getMessage());
                }
            });
        }
    }

    @Override
    public String getSystemUser()
    {
        return TranscribeConstants.TRANSCRIBE_SYSTEM_USER;
    }

    @Override
    public MediaEngine getForCopiedFile(Long fileId, Long versionId) throws GetMediaEngineException
    {
        return getByMediaVersionId(versionId);
    }

    @Override
    public TranscribeConfiguration getConfiguration()
    {
        return getTranscribeConfigurationService().loadProperties();
    }

    @Override
    public void saveConfiguration(MediaEngineConfiguration configuration) throws SaveConfigurationException
    {
        try
        {
            getTranscribeConfigurationService().saveProperties(configuration);
        }
        catch (ConfigurationPropertyException e)
        {
            throw new SaveConfigurationException(e);
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

    public ItemsMapper getItemsMapper()
    {
        return itemsMapper;
    }

    public void setItemsMapper(ItemsMapper itemsMapper)
    {
        this.itemsMapper = itemsMapper;
    }

    public TranscribeProviderFactory getTranscribeProviderFactory()
    {
        return transcribeProviderFactory;
    }

    public void setTranscribeProviderFactory(TranscribeProviderFactory transcribeProviderFactory)
    {
        this.transcribeProviderFactory = transcribeProviderFactory;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public TranscribeConfigurationService getTranscribeConfigurationService()
    {
        return transcribeConfigurationService;
    }

    public void setTranscribeConfigurationService(TranscribeConfigurationService transcribeConfigurationService)
    {
        this.transcribeConfigurationService = transcribeConfigurationService;
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
