package com.armedia.acm.services.transcribe.job;

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
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.services.mediaengine.mapper.MediaEngineMapper;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineProcessInstanceCreatedDateComparator;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.transcribe.factory.TranscribeProviderFactory;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.armedia.acm.services.transcribe.service.TranscribeConfigurationService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
public class TranscribeQueueJob
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private RuntimeService activitiRuntimeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MediaEngineMapper mediaEngineMapper;
    private AcmObjectLockService objectLockService;
    private AcmObjectLockingManager objectLockingManager;
    private TranscribeProviderFactory transcribeProviderFactory;
    private TranscribeConfigurationService transcribeConfigurationService;

    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

            MediaEngineConfiguration configuration = getTranscribeConfigurationService().loadProperties();
            List<MediaEngine> processingTranscribeObjects = getArkCaseTranscribeService()
                    .getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
            List<MediaEngine> processingTranscribeAutomaticObjects = processingTranscribeObjects.stream()
                    .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<MediaEngine> processingTranscribeObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream()
                    .filter(TranscribeUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

            List<ProcessInstance> processInstances = null;
            Integer emptySlots = configuration.getNumberOfFilesForProcessing() - processingTranscribeObjectsDistinctByProcessId.size();
            if (emptySlots > 0)
            {
                processInstances = getProcessInstances();
            }

            if (processInstances != null && !processInstances.isEmpty())
            {
                processInstances.sort(new MediaEngineProcessInstanceCreatedDateComparator());

                if (emptySlots < processInstances.size())
                {
                    processInstances = processInstances.subList(0, emptySlots);
                }

                moveProcessesFromQueue(processInstances, configuration);
            }
        }
        catch (GetMediaEngineException e)
        {
            LOG.error("Could not move Transcribe from the queue. REASON=[{}]", e.getMessage(), e);
        }
    }

    private void moveProcessesFromQueue(List<ProcessInstance> processInstances, MediaEngineConfiguration configuration)
    {
        processInstances.forEach(processInstance -> {
            try
            {
                moveSingleProcessInstanceFromQueue(processInstance, configuration);
            }
            catch (AcmObjectLockException | GetMediaEngineException
                    | CreateMediaEngineToolException | MediaEngineProviderNotFound | IOException | ArkCaseFileRepositoryException e)
            {
                LOG.error("Could not move TRANSCRIBE from the queue. REASON=[{}]", e.getMessage(), e);
            }
        });
    }

    public void moveSingleProcessInstanceFromQueue(ProcessInstance processInstance, MediaEngineConfiguration configuration)
            throws GetMediaEngineException, CreateMediaEngineToolException,
            MediaEngineProviderNotFound, IOException, ArkCaseFileRepositoryException
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");

        if (ids != null && !ids.isEmpty())
        {
            MediaEngine mediaEngine = getArkCaseTranscribeService().get(ids.get(0));

            AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE);
            if (mediaEngine != null
                    && (lock == null || lock.getCreator().equalsIgnoreCase(TranscribeConstants.TRANSCRIBE_SYSTEM_USER)))
            {
                getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE,
                        MediaEngineConstants.LOCK_TYPE_WRITE, null, true, TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

                String providerName = configuration.getProvider();
                String tempPath = configuration.getTempPath();
                MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, tempPath);
                mediaEngineDTO
                        .setMediaEcmFileVersion(getArkCaseTranscribeService().createTempFile(mediaEngine, tempPath));
                getTranscribeProviderFactory().getProvider(providerName).create(mediaEngineDTO);
                getArkCaseTranscribeService().signal(processInstance, MediaEngineStatusType.PROCESSING.toString(),
                        MediaEngineActionType.PROCESSING.toString());
            }
        }
    }

    private List<ProcessInstance> getProcessInstances()
    {
        String key = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String value = MediaEngineStatusType.QUEUED.toString();
        String serviceNameKey = MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString();
        String serviceNameValue = TranscribeConstants.SERVICE;

        return getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                .variableValueEqualsIgnoreCase(key, value)
                .variableValueEqualsIgnoreCase(serviceNameKey, serviceNameValue).list();
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public MediaEngineMapper getMediaEngineMapper()
    {
        return mediaEngineMapper;
    }

    public void setMediaEngineMapper(MediaEngineMapper mediaEngineMapper)
    {
        this.mediaEngineMapper = mediaEngineMapper;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public TranscribeProviderFactory getTranscribeProviderFactory()
    {
        return transcribeProviderFactory;
    }

    public void setTranscribeProviderFactory(TranscribeProviderFactory transcribeProviderFactory)
    {
        this.transcribeProviderFactory = transcribeProviderFactory;
    }

    public TranscribeConfigurationService getTranscribeConfigurationService()
    {
        return transcribeConfigurationService;
    }

    public void setTranscribeConfigurationService(TranscribeConfigurationService transcribeConfigurationService)
    {
        this.transcribeConfigurationService = transcribeConfigurationService;
    }
}
