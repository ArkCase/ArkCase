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
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
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
import com.armedia.acm.services.ocr.factory.OCRProviderFactory;
import com.armedia.acm.services.ocr.model.OCRConstants;
import com.armedia.acm.services.ocr.service.ArkCaseOCRService;
import com.armedia.acm.services.ocr.service.OCRConfigurationService;
import com.armedia.acm.services.ocr.utils.OCRUtils;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRQueueJob
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;
    private RuntimeService activitiRuntimeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmObjectLockService objectLockService;
    private AcmObjectLockingManager objectLockingManager;
    private MediaEngineMapper mediaEngineMapper;
    private OCRProviderFactory ocrProviderFactory;
    private OCRConfigurationService ocrConfigurationService;

    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

            MediaEngineConfiguration configuration = getOcrConfigurationService().loadProperties();
            List<MediaEngine> processingOCRObjects = getArkCaseOCRService()
                    .getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
            List<MediaEngine> processingOCRAutomaticObjects = processingOCRObjects.stream()
                    .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<MediaEngine> processingOCRObjectsDistinctByProcessId = processingOCRAutomaticObjects.stream()
                    .filter(OCRUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

            List<ProcessInstance> processInstances = null;
            Integer emptySlots = configuration.getNumberOfFilesForProcessing() - processingOCRObjectsDistinctByProcessId.size();
            if (emptySlots > 0)
            {
                processInstances = getProcessesFromQueue();
            }

            if (processInstances != null && !processInstances.isEmpty())
            {
                processInstances.sort(new MediaEngineProcessInstanceCreatedDateComparator());

                if (emptySlots < processInstances.size())
                {
                    processInstances = processInstances.subList(0, emptySlots);
                }

                moveProcessesFromQueue(processInstances);
            }

        }
        catch (GetMediaEngineException e)
        {
            LOG.error("Could not move OCR from the queue. REASON=[{}]", e.getMessage(), e);
        }
    }

    private List<ProcessInstance> getProcessesFromQueue()
    {
        String key = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String value = MediaEngineStatusType.QUEUED.toString();
        String serviceNameKey = MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString();
        String serviceNameValue = "OCR";

        return getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                .variableValueEqualsIgnoreCase(key, value).variableValueEqualsIgnoreCase(serviceNameKey, serviceNameValue).list();
    }

    private void moveProcessesFromQueue(List<ProcessInstance> processInstances)
    {
        processInstances.forEach(processInstance -> {
            try
            {
                moveSingleProcessInstanceFromQueue(processInstance);
            }
            catch (AcmObjectLockException | GetMediaEngineException | SaveMediaEngineException
                    | CreateMediaEngineToolException | MediaEngineProviderNotFound | IOException | MuleException e)
            {
                LOG.error("Could not move OCR from the queue. REASON=[{}]", e.getMessage(), e);
            }
        });
    }

    public void moveSingleProcessInstanceFromQueue(ProcessInstance processInstance)
            throws GetMediaEngineException, SaveMediaEngineException, CreateMediaEngineToolException,
            MediaEngineProviderNotFound, IOException, MuleException
    {
        // For OCR, ids will always return only one id.
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");

        LOG.debug("Moving process instance from queue for OCR_ID=[{}]", ids);
        if (ids != null && !ids.isEmpty())
        {
            MediaEngine mediaEngine = getArkCaseOCRService().get(ids.get(0));

            AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE);
            if (mediaEngine != null && (lock == null || lock.getCreator().equalsIgnoreCase(OCRConstants.OCR_SYSTEM_USER)))
            {
                getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE,
                        MediaEngineConstants.LOCK_TYPE_WRITE, null, true, OCRConstants.OCR_SYSTEM_USER);
                String providerName = getOcrConfigurationService().loadProperties().getProvider();
                String tempPath = getOcrConfigurationService().loadProperties().getTempPath();
                MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, tempPath);
                mediaEngineDTO.setMediaEcmFileVersion(getArkCaseOCRService().createTempFile(mediaEngine, tempPath));
                getOcrProviderFactory().getProvider(providerName).create(mediaEngineDTO);
                getArkCaseOCRService().save(mediaEngine);
                getArkCaseOCRService().signal(processInstance, MediaEngineStatusType.PROCESSING.toString(),
                        MediaEngineActionType.PROCESSING.toString());

                LOG.debug("Successfully moved process instance from queue for OCR_ID=[{}]", ids);
            }
        }
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

    public MediaEngineMapper getMediaEngineMapper()
    {
        return mediaEngineMapper;
    }

    public void setMediaEngineMapper(MediaEngineMapper mediaEngineMapper)
    {
        this.mediaEngineMapper = mediaEngineMapper;
    }

    public OCRProviderFactory getOcrProviderFactory()
    {
        return ocrProviderFactory;
    }

    public void setOcrProviderFactory(OCRProviderFactory ocrProviderFactory)
    {
        this.ocrProviderFactory = ocrProviderFactory;
    }

    public OCRConfigurationService getOcrConfigurationService()
    {
        return ocrConfigurationService;
    }

    public void setOcrConfigurationService(OCRConfigurationService ocrConfigurationService)
    {
        this.ocrConfigurationService = ocrConfigurationService;
    }
}
