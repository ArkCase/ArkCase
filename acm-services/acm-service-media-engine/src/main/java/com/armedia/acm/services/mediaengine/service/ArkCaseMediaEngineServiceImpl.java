package com.armedia.acm.services.mediaengine.service;

/*-
 * #%L
 * ACM Service: Media engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.mediaengine.dao.MediaEngineDao;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.mapper.MediaEngineMapper;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessModel;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.mediaengine.rules.MediaEngineBusinessProcessRulesExecutor;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.spring.SpringContextHolder;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public abstract class ArkCaseMediaEngineServiceImpl<T extends MediaEngine>
        implements MediaEngineService<T>
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private FolderAndFilesUtils folderAndFilesUtils;
    private MediaEngineServiceFactory mediaEngineServiceFactory;
    private MediaEngineEventPublisher mediaEngineEventPublisher;
    private ArkCaseBeanUtils arkCaseBeanUtils;
    private RuntimeService activitiRuntimeService;
    private MediaEngineBusinessProcessRulesExecutor mediaEngineBusinessProcessRulesExecutor;
    private EcmFileVersionDao ecmFileVersionDao;
    private Map<String, MediaEngineBusinessProcessRulesExecutor> processHandlerMap;
    private PipelineManager<MediaEngine, MediaEnginePipelineContext> pipelineManager;
    private EcmFileTransaction ecmFileTransaction;
    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;
    private MediaEngineMapper mediaEngineMapper;
    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AuditDao auditDao;

    @Override
    @Transactional
    public MediaEngine create(Long mediaVersionId, MediaEngineType type) throws CreateMediaEngineException
    {
        EcmFileVersion ecmFileVersion = getEcmFileVersionDao().find(mediaVersionId);

        return create(ecmFileVersion, type);
    }

    @Override
    @Transactional
    public MediaEngine create(EcmFileVersion ecmFileVersion, MediaEngineType type) throws CreateMediaEngineException
    {
        MediaEngine mediaEngine = createEntity();
        mediaEngine.setMediaEcmFileVersion(ecmFileVersion);
        mediaEngine.setType(type.toString());

        return create(mediaEngine);
    }

    @Override
    @Transactional
    public MediaEngine create(MediaEngine mediaEngine) throws CreateMediaEngineException
    {
        if (!allow(mediaEngine.getMediaEcmFileVersion()))
        {
            throw new CreateMediaEngineException(String.format("%s service is not allowed.", getServiceName()));
        }

        if (mediaEngine.getId() != null)
        {
            throw new CreateMediaEngineException(
                    String.format("There is existing [%s] with ID=[%d].", getServiceName(), mediaEngine.getId()));
        }

        MediaEngine existingMediaEngine = null;
        try
        {
            existingMediaEngine = getExisting(mediaEngine);
        }
        catch (GetMediaEngineException e)
        {
            throw new CreateMediaEngineException(
                    String.format("Creating [%s] job is aborted. REASON=[%s]", getServiceName(), e.getMessage()), e);
        }

        if (existingMediaEngine != null && (MediaEngineStatusType.QUEUED.toString().equalsIgnoreCase(existingMediaEngine.getStatus()) ||
                MediaEngineStatusType.PROCESSING.toString().equalsIgnoreCase(existingMediaEngine.getStatus())))
        {
            existingMediaEngine.setMediaEcmFileVersion(getExistingMediaVersionId(mediaEngine));
        }

        MediaEnginePipelineContext context = new MediaEnginePipelineContext();
        context.setEcmFileVersion(mediaEngine.getMediaEcmFileVersion());
        context.setType(MediaEngineType.valueOf(mediaEngine.getType()));
        context.setServiceName(getServiceName());

        try
        {
            MediaEngine mediaEngineForProcessing = existingMediaEngine != null ? existingMediaEngine : mediaEngine;

            mediaEngineForProcessing.setRemoteId(resetRemoteId(mediaEngineForProcessing));
            return getPipelineManager().executeOperation(mediaEngineForProcessing, context, () -> {
                try
                {
                    return save(mediaEngineForProcessing);
                }
                catch (SaveMediaEngineException e)
                {
                    throw new PipelineProcessException(
                            String.format("[%s] for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]", getServiceName(),
                                    mediaEngineForProcessing.getMediaEcmFileVersion() != null
                                            ? mediaEngineForProcessing.getMediaEcmFileVersion().getId()
                                            : null,
                                    e.getMessage()));
                }
            });
        }
        catch (PipelineProcessException e)
        {
            throw new CreateMediaEngineException(String.format(
                    "[%s] for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]", getServiceName(),
                    mediaEngine.getMediaEcmFileVersion() != null ? mediaEngine.getMediaEcmFileVersion().getId() : null,
                    e.getMessage()), e);
        }
    }

    public abstract MediaEngine getExisting(MediaEngine mediaEngine) throws GetMediaEngineException;

    public abstract EcmFileVersion getExistingMediaVersionId(MediaEngine mediaEngine) throws CreateMediaEngineException;

    public abstract String resetRemoteId(MediaEngine mediaEngine);

    @Override
    public MediaEngine get(String remoteId) throws GetMediaEngineException
    {
        throw new NotImplementedException();
    }

    @Override
    public MediaEngine get(Long id) throws GetMediaEngineException
    {
        return getMediaEntityDao(getServiceName()).find(id);
    }

    @Override
    public MediaEngine getByMediaVersionId(Long mediaVersionId) throws GetMediaEngineException
    {
        return getMediaEntityDao(getServiceName()).findByMediaVersionId(mediaVersionId);
    }

    @Override
    public MediaEngine getByFileId(Long fileId) throws GetMediaEngineException
    {
        return getMediaEntityDao(getServiceName()).findByFileId(fileId);
    }

    @Override
    public List<MediaEngine> getAll() throws GetMediaEngineException
    {
        return getMediaEntityDao(getServiceName()).findAll();
    }

    @Override
    public List<MediaEngine> getAllByStatus(String status) throws GetMediaEngineException
    {
        return getMediaEntityDao(getServiceName()).findAllByStatus(status);
    }

    @Override
    public MediaEngine save(MediaEngine mediaEngine) throws SaveMediaEngineException
    {
        LOG.debug("Trying to save [{}] in database. ID=[{}], VERSION=[{}]", mediaEngine.getObjectType(), mediaEngine.getId(),
                mediaEngine.getMediaEcmFileVersion().getVersionTag());
        MediaEngine saved = getMediaEntityDao(getServiceName()).save(mediaEngine);
        String action = mediaEngine.getId() == null ? MediaEngineActionType.CREATED.toString() : MediaEngineActionType.UPDATED.toString();
        String serviceName = getServiceName();
        getMediaEngineEventPublisher().publish(saved, action, serviceName, "");

        LOG.debug("{} with ID=[{}], VERSION=[{}], successfully saved.", mediaEngine.getObjectType(), mediaEngine.getId(),
                mediaEngine.getMediaEcmFileVersion().getVersionTag());

        return saved;
    }

    @Override
    public MediaEngine copy(MediaEngine mediaEngine, EcmFileVersion ecmFileVersion) throws CreateMediaEngineException
    {
        LOG.debug("Copying [{}] with ID=[{}], VERSION=[{}], DESTINATION=[{}]", mediaEngine.getObjectType(), mediaEngine.getId(),
                mediaEngine.getMediaEcmFileVersion().getVersionTag(), ecmFileVersion.getFile().getParentObjectId());

        MediaEngine copy = null;
        copy = createCopyObject(mediaEngine, ecmFileVersion);

        MediaEngine savedCopy = null;
        try
        {
            savedCopy = save(copy);
        }
        catch (SaveMediaEngineException e)
        {
            throw new CreateMediaEngineException(String.format("Could not create copy for [%s] object with ID=[%d]. REASON=[%s]",
                    getServiceName(), mediaEngine != null ? mediaEngine.getId() : null, e.getMessage()));
        }

        if (mediaEngine != null)
        {
            ProcessInstance processInstance = null;
            if (StringUtils.isNotEmpty(mediaEngine.getProcessId()))
            {
                processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                        .processInstanceId(mediaEngine.getProcessId()).singleResult();
            }

            if (processInstance != null)
            {
                List<Long> ids = (List<Long>) processInstance.getProcessVariables()
                        .get(MediaEngineBusinessProcessVariableKey.IDS.toString());
                if (ids == null)
                {
                    ids = new ArrayList<>();
                }

                ids.add(savedCopy.getId());
                getActivitiRuntimeService().setVariable(processInstance.getId(),
                        MediaEngineBusinessProcessVariableKey.IDS.toString(),
                        ids);
            }
            LOG.debug("Copying [{}] with ID=[{}], VERSION=[{}], DESTINATION=[{}] successfully done", mediaEngine.getObjectType(),
                    mediaEngine.getId(), mediaEngine.getMediaEcmFileVersion().getVersionTag(),
                    ecmFileVersion.getFile().getParentObjectId());

            return savedCopy;
        }

        throw new CreateMediaEngineException(
                String.format("Could not create copy for [%s] object with ID=[%d]", getServiceName(),
                        mediaEngine != null ? mediaEngine.getId() : null));
    }

    public MediaEngine createCopyObject(MediaEngine mediaEngine, EcmFileVersion ecmFileVersion)
    {
        MediaEngine copy = null;
        try
        {
            copy = createEntity();
            copy.setMediaEcmFileVersion(ecmFileVersion);
            getArkCaseBeanUtils().copyProperties(copy, mediaEngine);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for [{}] object with ID=[{}]. REASON=[{}]", getServiceName(),
                    mediaEngine != null ? mediaEngine.getId() : null, e.getMessage());
        }
        return copy;
    }

    @Override
    public MediaEngine complete(Long id) throws SaveMediaEngineException
    {
        LOG.debug("Completing [{}] object with ID=[{}]", getServiceName(), id);
        MediaEngine mediaEngine = getMediaEntityDao(getServiceName()).find(id);
        if (mediaEngine != null && StringUtils.isNotEmpty(mediaEngine.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(mediaEngine.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, MediaEngineStatusType.COMPLETED.toString(), MediaEngineActionType.COMPLETED.toString());
                mediaEngine.setStatus(MediaEngineStatusType.COMPLETED.toString());

                LOG.debug("Completing done for [{}] object with ID=[{}]", getServiceName(), id);

                return mediaEngine;
            }
        }

        throw new SaveMediaEngineException(String.format("Could not complete [%s] object with ID=[%d]", getServiceName(), id));
    }

    @Override
    public MediaEngine cancel(Long id) throws SaveMediaEngineException
    {
        LOG.debug("Canceling [{}] object with ID=[{}]", getServiceName(), id);
        MediaEngine mediaEngine = getMediaEntityDao(getServiceName()).find(id);
        if (mediaEngine != null && StringUtils.isNotEmpty(mediaEngine.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(mediaEngine.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, MediaEngineStatusType.DRAFT.toString(), MediaEngineActionType.CANCELLED.toString());
                mediaEngine.setStatus(MediaEngineStatusType.DRAFT.toString());

                LOG.debug("Canceling done for [{}] object with ID=[{}]", getServiceName(), id);

                return mediaEngine;
            }
        }

        throw new SaveMediaEngineException(String.format("Could not cancel [%s] object with ID=[%d]", getServiceName(), id));
    }

    @Override
    public MediaEngine fail(Long id, String message) throws SaveMediaEngineException
    {
        MediaEngine mediaEngine = getMediaEntityDao(getServiceName()).find(id);
        if (mediaEngine != null && StringUtils.isNotEmpty(mediaEngine.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(mediaEngine.getProcessId()).singleResult();
            if (processInstance != null)
            {
                String statusKey = MediaEngineBusinessProcessVariableKey.STATUS.toString();
                String actionKey = MediaEngineBusinessProcessVariableKey.ACTION.toString();
                String messageKey = MediaEngineBusinessProcessVariableKey.MESSAGE.toString();
                String status = MediaEngineStatusType.FAILED.toString();
                String action = MediaEngineActionType.FAILED.toString();

                getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
                getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);
                getActivitiRuntimeService().setVariable(processInstance.getId(), messageKey, message);

                mediaEngine.setStatus(MediaEngineStatusType.FAILED.toString());

                getObjectLockingManager().releaseObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE,
                        true,
                        getSystemUser(), null);

                return mediaEngine;
            }
        }

        throw new SaveMediaEngineException(String.format("Could not set as failed [%s] object with ID=[%d]", getServiceName(), id));
    }

    @Override
    public MediaEngine changeStatus(Long id, String status) throws SaveMediaEngineException
    {
        LOG.debug("Changing status of the [{}] with ID=[{}]. New status will be STATUS=[{}]", getServiceName(), id, status);
        if (id == null || StringUtils.isEmpty(status))
        {
            String message = String.format("Status of the [%s] cannot be changed. ID=[%d], STATUS=[%s]", getServiceName(), id, status);
            LOG.error(message);
            throw new SaveMediaEngineException(message);
        }

        MediaEngine mediaEngine = getMediaEntityDao(getServiceName()).find(id);

        if (mediaEngine == null)
        {
            String message = String.format("[%s] with ID=[%d] cannot be found or retrieved from database.", getServiceName(), id);
            LOG.error(message);
            throw new SaveMediaEngineException(message);
        }

        mediaEngine.setStatus(status);

        try
        {
            return getMediaEntityDao(getServiceName()).save(mediaEngine);
        }
        catch (Exception e)
        {
            String message = String.format("Status of the [%s] cannot be changed. ID=[%d], STATUS=[%s]. REASON=[%s]", getServiceName(), id,
                    status,
                    e.getMessage());
            LOG.error(message);
            throw new SaveMediaEngineException(message, e);
        }
    }

    @Override
    public List<MediaEngine> changeStatusMultiple(List<Long> ids, String status) throws SaveMediaEngineException
    {
        if (ids != null)
        {
            List<MediaEngine> changedProcesses = new ArrayList<>();
            ids.forEach(id -> {
                try
                {
                    MediaEngine changed = changeStatus(id, status);
                    changedProcesses.add(changed);
                }
                catch (SaveMediaEngineException e)
                {
                    LOG.warn("Changing status for [{}] with ID=[{}] in bulk operation failed. REASON=[{}]", getServiceName(), id,
                            e.getMessage());
                }
            });

            return changedProcesses;
        }

        String message = String.format("Status of multiple [%s] objects cannot be changed. IDS=[null], STATUS=[%s]", getServiceName(),
                status);
        LOG.error(message);
        throw new SaveMediaEngineException(message);
    }

    @Override
    public void audit(Long id, String action, String message)
    {
        if (id != null && action != null)
        {
            MediaEngine mediaEngine = getMediaEntityDao(getServiceName()).find(id);
            if (mediaEngine != null)
            {
                getMediaEngineEventPublisher().publish(mediaEngine, action, getServiceName(), message);
            }
        }
    }

    @Override
    public void auditMultiple(List<Long> ids, String action, String message)
    {
        if (ids != null)
        {
            ids.forEach(id -> audit(id, action, message));
        }
    }

    @Override
    public ProcessInstance startBusinessProcess(MediaEngine mediaEngine, String serviceName)
    {
        LOG.debug("Checking if starting business process is allowed for [{}] Object [{}]", getServiceName(), mediaEngine);
        ProcessInstance processInstance = null;
        if (mediaEngine != null)
        {
            // Check drools if we need to start workflow for provided MediaEngine object
            MediaEngineBusinessProcessModel mediaEngineBusinessProcessModel = createMediaEngineBusinessProcessModelEntity();
            mediaEngineBusinessProcessModel.setType(mediaEngine.getType());

            LOG.debug(
                    "Executing Drools Business rules for [{}] [{}] with ID=[{}], MEDIA_FILE_ID=[{}] and MEDIA_FILE_VERSION_ID=[{}]",
                    mediaEngine.getType(), getServiceName(), mediaEngine.getId(), mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    mediaEngine.getMediaEcmFileVersion().getId());

            mediaEngineBusinessProcessModel = getProcessHandlerMap().get(serviceName).applyRules(mediaEngineBusinessProcessModel);

            LOG.debug("Start business process: [{}]", mediaEngineBusinessProcessModel.isStart());

            if (mediaEngineBusinessProcessModel.isStart())
            {
                // Check if there is already startes business process. This can be the case when we replace media file
                // and in the MediaEngine Configuration (properties file) we have set "copy mediaEngine" instead of
                // "new mediaEngine".
                // In that case we have complete two copies of MediaEngine object, just different IDS. So we should use
                // the same Process
                // for both MediaEngine objects
                if (StringUtils.isNotEmpty(mediaEngine.getProcessId()))
                {
                    processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(mediaEngine.getProcessId())
                            .includeProcessVariables().singleResult();
                }

                if (processInstance == null)
                {
                    // When we don't have process instance, create it
                    processInstance = createProcessInstance(mediaEngine, mediaEngineBusinessProcessModel);
                }
                else
                {
                    // When we have process instance, just update the variable 'IDS'
                    updateProcessInstance(mediaEngine, processInstance);
                }
            }
        }

        LOG.debug("There is no [{}] Object. It's [{}]", getServiceName(), mediaEngine);

        return processInstance;
    }

    @Override
    public void signal(ProcessInstance processInstance, String status, String action)
    {
        if (processInstance != null && StringUtils.isNotEmpty(status) && StringUtils.isNotEmpty(action))
        {
            String statusKey = MediaEngineBusinessProcessVariableKey.STATUS.toString();
            String actionKey = MediaEngineBusinessProcessVariableKey.ACTION.toString();
            getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
            getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);
            getActivitiRuntimeService().signal(processInstance.getId());
        }
    }

    private ProcessInstance createProcessInstance(MediaEngine mediaEngine, MediaEngineBusinessProcessModel mediaEngineBusinessProcessModel)
    {
        String status = MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(mediaEngine.getType())
                ? MediaEngineStatusType.QUEUED.toString()
                : mediaEngine.getStatus();
        String action = MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(mediaEngine.getType())
                ? MediaEngineActionType.QUEUED.toString()
                : mediaEngine.getStatus();

        List<Long> ids = new ArrayList<>();
        ids.add(mediaEngine.getId());

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(MediaEngineBusinessProcessVariableKey.IDS.toString(), ids);
        processVariables.put(MediaEngineBusinessProcessVariableKey.REMOTE_ID.toString(), mediaEngine.getRemoteId());
        processVariables.put(MediaEngineBusinessProcessVariableKey.STATUS.toString(), status);
        processVariables.put(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
        processVariables.put(MediaEngineBusinessProcessVariableKey.TYPE.toString(), mediaEngine.getType());
        processVariables.put(MediaEngineBusinessProcessVariableKey.CREATED.toString(), new Date());
        processVariables.put(MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString(), getServiceName());
        processVariables.put(MediaEngineBusinessProcessVariableKey.MESSAGE.toString(), "");

        ProcessInstance processInstance = getActivitiRuntimeService().startProcessInstanceByKey(mediaEngineBusinessProcessModel.getName(),
                processVariables);

        mediaEngine.setProcessId(processInstance.getId());

        return processInstance;
    }

    private void updateProcessInstance(MediaEngine mediaEngine, ProcessInstance processInstance)
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get(MediaEngineBusinessProcessVariableKey.IDS.toString());
        if (ids != null)
        {
            if (!ids.contains(mediaEngine.getId()))
            {
                ids.add(mediaEngine.getId());
            }

            getActivitiRuntimeService().setVariable(processInstance.getId(), MediaEngineBusinessProcessVariableKey.IDS.toString(), ids);
        }
    }

    @Override
    public boolean isExcludedFileTypes(String fileType)
    {
        try
        {
            return Arrays.asList(getConfiguration().getExcludedFileTypes().split(",")).contains(fileType);
        }
        catch (GetConfigurationException e)
        {
            LOG.warn("Failed to retrieve MediaEngine configuration. Automatic MediaEngine will be terminated.");
            return true;
        }
    }

    @Override
    public File createTempFile(MediaEngine mediaEngine, String tempPath) throws IOException, ArkCaseFileRepositoryException
    {
        InputStream mediaVersionInputStream = getEcmFileTransaction()
                .downloadFileTransactionAsInputStream(mediaEngine.getMediaEcmFileVersion().getFile());

        File tmpFile = new File(tempPath + mediaEngine.getRemoteId() + "."
                + mediaEngine.getMediaEcmFileVersion().getFile().getFileExtension());

        FileUtils.copyInputStreamToFile(mediaVersionInputStream, tmpFile);

        mediaVersionInputStream.close();

        return tmpFile;
    }

    @Override
    public MediaEngineServiceFactory getMediaEngineServiceFactory()
    {
        return mediaEngineServiceFactory;
    }

    public void setMediaEngineServiceFactory(MediaEngineServiceFactory mediaEngineServiceFactory)
    {
        this.mediaEngineServiceFactory = mediaEngineServiceFactory;
    }

    @Override
    public boolean isAutomaticOn()
    {
        try
        {
            MediaEngineConfiguration configuration = getConfiguration();
            boolean automatic = configuration != null && configuration.isAutomaticEnabled();

            if (!automatic)
            {
                LOG.warn("Automatic [{}] is not enabled. It will be terminated.", getServiceName());
            }

            return automatic;
        }
        catch (GetConfigurationException e)
        {
            LOG.warn("Failed to retrieve [{}] configuration. Automatic [SERVICE] will be terminated.", getServiceName());
            return false;
        }
    }

    @Override
    public boolean isServiceEnabled()
    {
        try
        {
            MediaEngineConfiguration configuration = getConfiguration();
            boolean enable = configuration != null && configuration.isEnabled();

            if (!enable)
            {
                LOG.warn("Required service not enabled. It will be terminated.");
            }

            return enable;
        }
        catch (GetConfigurationException e)
        {
            LOG.error("Failed to retrieve Media Engine configuration.", e);
            return false;
        }
    }

    public MediaEngineDao<MediaEngine> getMediaEntityDao(String objectType)
    {
        if (objectType != null)
        {
            Map<String, MediaEngineDao> daos = getSpringContextHolder().getAllBeansOfType(MediaEngineDao.class);

            if (daos != null)
            {
                for (MediaEngineDao<MediaEngine> dao : daos.values())
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
    public Map<String, String> getFailureReasonMessage(Long mediaVersionId)
    {
        String auditTrackId = "MEDIA_SERVICE|" + MediaEngineConstants.FAILED_EVENT.replace("[SERVICE]", getServiceName().toLowerCase());
        Map<String, String> failureResult = new HashMap<>();
        try
        {
            AuditEvent event = getAuditDao().getLastAuditEventByObjectIdAndTrackId(mediaVersionId, auditTrackId);
            if (StringUtils.isNotEmpty(event.getEventDescription()))
            {
                failureResult.put("failureReason", event.getEventDescription());
            }
            else
            {
                failureResult.put("failureReason", "Unknown reason");
            }
        }
        catch (NoResultException e)
        {
            failureResult.put("failureReason", "Unknown reason");
        }

        return failureResult;
    }

    protected abstract MediaEngine createEntity();

    protected abstract MediaEngineBusinessProcessModel createMediaEngineBusinessProcessModelEntity();

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public MediaEngineEventPublisher getMediaEngineEventPublisher()
    {
        return mediaEngineEventPublisher;
    }

    public void setMediaEngineEventPublisher(MediaEngineEventPublisher mediaEngineEventPublisher)
    {
        this.mediaEngineEventPublisher = mediaEngineEventPublisher;
    }

    public ArkCaseBeanUtils getArkCaseBeanUtils()
    {
        return arkCaseBeanUtils;
    }

    public void setArkCaseBeanUtils(ArkCaseBeanUtils arkCaseBeanUtils)
    {
        this.arkCaseBeanUtils = arkCaseBeanUtils;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public MediaEngineBusinessProcessRulesExecutor getMediaEngineBusinessProcessRulesExecutor()
    {
        return mediaEngineBusinessProcessRulesExecutor;
    }

    public void setMediaEngineBusinessProcessRulesExecutor(MediaEngineBusinessProcessRulesExecutor mediaEngineBusinessProcessRulesExecutor)
    {
        this.mediaEngineBusinessProcessRulesExecutor = mediaEngineBusinessProcessRulesExecutor;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public Map<String, MediaEngineBusinessProcessRulesExecutor> getProcessHandlerMap()
    {
        return processHandlerMap;
    }

    public void setProcessHandlerMap(Map<String, MediaEngineBusinessProcessRulesExecutor> processHandlerMap)
    {
        this.processHandlerMap = processHandlerMap;
    }

    public PipelineManager<MediaEngine, MediaEnginePipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<MediaEngine, MediaEnginePipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public MediaEngineMapper getMediaEngineMapper()
    {
        return mediaEngineMapper;
    }

    public void setMediaEngineMapper(MediaEngineMapper mediaEngineMapper)
    {
        this.mediaEngineMapper = mediaEngineMapper;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AuditDao getAuditDao()
    {
        return auditDao;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }
}
