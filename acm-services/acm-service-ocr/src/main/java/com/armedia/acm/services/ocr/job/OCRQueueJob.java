package com.armedia.acm.services.ocr.job;

/*-
 * #%L
 * ACM Service: OCR
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

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.scheduler.AcmSchedulableBean;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.mapper.MediaEngineMapper;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineProcessInstanceCreatedDateComparator;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.ocr.model.OCRConstants;
import com.armedia.acm.services.ocr.service.ArkCaseOCRService;
import com.armedia.acm.services.ocr.service.OCRConfigurationPropertiesService;
import com.armedia.acm.services.ocr.utils.OCRUtils;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationService;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRQueueJob implements AcmSchedulableBean
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;
    private RuntimeService activitiRuntimeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmObjectLockService objectLockService;
    private AcmObjectLockingManager objectLockingManager;
    private OCRConfigurationPropertiesService ocrConfigurationPropertiesService;
    private MediaEngineIntegrationService mediaEngineIntegrationService;
    private MediaEngineMapper mediaEngineMapper;

    @Override
    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

            MediaEngineConfiguration configuration = getOcrConfigurationPropertiesService().get();
            List<MediaEngine> processingOCRObjects = getArkCaseOCRService()
                    .getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
            List<MediaEngine> processingOCRAutomaticObjects = processingOCRObjects.stream()
                    .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<MediaEngine> processingOCRObjectsDistinctByProcessId = processingOCRAutomaticObjects.stream()
                    .filter(OCRUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

            List<ProcessInstance> processInstances = null;
            if (configuration.getNumberOfFilesForProcessing() > processingOCRObjectsDistinctByProcessId.size())
            {
                processInstances = getProcessesFromQueue();
            }

            if (processInstances != null && !processInstances.isEmpty())
            {
                processInstances.sort(new MediaEngineProcessInstanceCreatedDateComparator());
                getSingleProcess(processInstances);
            }

        }
        catch (GetMediaEngineException | GetConfigurationException e)
        {
            LOG.error("Could not move OCR from the queue. REASON=[{}]", e.getMessage(), e);
        }
    }

    private List<ProcessInstance> getProcessesFromQueue()
    {
        String key = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String value = MediaEngineStatusType.QUEUED.toString();

        return getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                .variableValueEqualsIgnoreCase(key, value).list();
    }

    private void getSingleProcess(List<ProcessInstance> processInstances)
    {
        processInstances.forEach(processInstance -> {
            try
            {
                moveProcessInstanceFromQueue(processInstance);
            }
            catch (GetConfigurationException | AcmObjectLockException | GetMediaEngineException | SaveMediaEngineException
                    | CreateMediaEngineToolException e)
            {
                LOG.error("Could not move OCR from the queue. REASON=[{}]", e.getMessage(), e);
            }
        });
    }

    public void moveProcessInstanceFromQueue(ProcessInstance processInstance)
            throws GetMediaEngineException, SaveMediaEngineException, GetConfigurationException, CreateMediaEngineToolException
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");

        if (ids != null && !ids.isEmpty())
        {
            MediaEngine mediaEngine = getArkCaseOCRService().get(ids.get(0));

            AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    MediaEngineConstants.OBJECT_TYPE_FILE);
            if (mediaEngine != null && (lock == null || lock.getCreator().equalsIgnoreCase(OCRConstants.OCR_SYSTEM_USER)))
            {
                getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                        MediaEngineConstants.OBJECT_TYPE_FILE,
                        MediaEngineConstants.LOCK_TYPE_WRITE, null, true, OCRConstants.OCR_SYSTEM_USER);

                MediaEngineDTO mediaEngineDTO = getMediaEngineDTO(mediaEngine);
                getMediaEngineIntegrationService().create(mediaEngineDTO);
                getArkCaseOCRService().save(mediaEngine);
                getArkCaseOCRService().signal(processInstance, MediaEngineStatusType.PROCESSING.toString(),
                        MediaEngineActionType.PROCESSING.toString());
            }
        }
    }

    private MediaEngineDTO getMediaEngineDTO(MediaEngine mediaEngine) throws GetConfigurationException
    {
        MediaEngineConfiguration configuration = getOcrConfigurationPropertiesService().get();
        MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().MediaEngineToDTO(mediaEngine);

        mediaEngineDTO.setProviderName(configuration.getProvider());
        mediaEngineDTO.setServiceName(configuration.getService().toString());
        return mediaEngineDTO;
    }

    public ArkCaseOCRService getArkCaseOCRService()
    {
        return arkCaseOCRService;
    }

    public void setArkCaseOCRService(ArkCaseOCRService arkCaseOCRService)
    {
        this.arkCaseOCRService = arkCaseOCRService;
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

    public OCRConfigurationPropertiesService getOcrConfigurationPropertiesService()
    {
        return ocrConfigurationPropertiesService;
    }

    public void setOcrConfigurationPropertiesService(OCRConfigurationPropertiesService ocrConfigurationPropertiesService)
    {
        this.ocrConfigurationPropertiesService = ocrConfigurationPropertiesService;
    }

    public MediaEngineIntegrationService getMediaEngineIntegrationService()
    {
        return mediaEngineIntegrationService;
    }

    public void setMediaEngineIntegrationService(MediaEngineIntegrationService mediaEngineIntegrationService)
    {
        this.mediaEngineIntegrationService = mediaEngineIntegrationService;
    }

    public MediaEngineMapper getMediaEngineMapper()
    {
        return mediaEngineMapper;
    }

    public void setMediaEngineMapper(MediaEngineMapper mediaEngineMapper)
    {
        this.mediaEngineMapper = mediaEngineMapper;
    }
}
