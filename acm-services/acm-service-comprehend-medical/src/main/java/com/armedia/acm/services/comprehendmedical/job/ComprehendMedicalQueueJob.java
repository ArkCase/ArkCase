package com.armedia.acm.services.comprehendmedical.job;

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
import com.armedia.acm.services.comprehendmedical.factory.ComprehendMedicalProviderFactory;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.comprehendmedical.sevice.ArkCaseComprehendMedicalService;
import com.armedia.acm.services.comprehendmedical.sevice.ComprehendMedicalConfigurationService;
import com.armedia.acm.services.comprehendmedical.utils.ComprehendMedicalUtils;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.mapper.MediaEngineMapper;
import com.armedia.acm.services.mediaengine.model.*;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicineDTO;
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
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
public class ComprehendMedicalQueueJob
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private ArkCaseComprehendMedicalService arkCaseComprehendMedicalService;
    private RuntimeService activitiRuntimeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MediaEngineMapper mediaEngineMapper;
    private AcmObjectLockService objectLockService;
    private AcmObjectLockingManager objectLockingManager;
    private ComprehendMedicalProviderFactory comprehendMedicalProviderFactory;
    private ComprehendMedicalConfigurationService comprehendMedicalConfigurationService;

    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

            MediaEngineConfiguration configuration = getComprehendMedicalConfigurationService().loadProperties();
            List<MediaEngine> processingObjects = getArkCaseComprehendMedicalService()
                    .getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
            List<MediaEngine> processingTranscribeAutomaticObjects = processingObjects.stream()
                    .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<MediaEngine> processingObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream()
                    .filter(ComprehendMedicalUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

            List<ProcessInstance> processInstances = null;
            Integer emptySlots = configuration.getNumberOfFilesForProcessing() - processingObjectsDistinctByProcessId.size();
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
            LOG.error("Could not move ComprehendMedical from the queue. REASON=[{}]", e.getMessage(), e);
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
                LOG.error("Could not move ComprehendMedical from the queue. REASON=[{}]", e.getMessage(), e);
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
            MediaEngine mediaEngine = getArkCaseComprehendMedicalService().get(ids.get(0));

            AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE);
            if (mediaEngine != null
                    && (lock == null || lock.getCreator().equalsIgnoreCase(ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER)))
            {
                getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE,
                        MediaEngineConstants.LOCK_TYPE_WRITE, null, true, ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER);

                String providerName = configuration.getProvider();
                String tempPath = configuration.getTempPath();
                MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, tempPath);
                mediaEngineDTO
                        .setMediaEcmFileVersion(getArkCaseComprehendMedicalService().createTempFile(mediaEngine, tempPath));
                mediaEngineDTO.getProperties().put("fileSize", mediaEngineDTO.getMediaEcmFileVersion().length() + "");
                mediaEngineDTO.getProperties().put("mimeType", "text/plain");
                mediaEngineDTO = getComprehendMedicalProviderFactory().getProvider(providerName).create(mediaEngineDTO);
                getArkCaseComprehendMedicalService().signal(processInstance, MediaEngineStatusType.PROCESSING.toString(),
                        MediaEngineActionType.PROCESSING.toString());

                if (mediaEngine instanceof ComprehendMedical)
                {
                    ((ComprehendMedical) mediaEngine).setJobId(mediaEngineDTO.getJobId());

                    try
                    {
                        getArkCaseComprehendMedicalService().save(mediaEngine);
                    }
                    catch (SaveMediaEngineException e)
                    {
                        LOG.error("Failed to save Comprehend Media Object.", e);
                    }
                }
            }
        }
    }

    private List<ProcessInstance> getProcessInstances()
    {
        String key = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String value = MediaEngineStatusType.QUEUED.toString();
        String serviceNameKey = MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString();
        String serviceNameValue = ComprehendMedicalConstants.SERVICE;

        return getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                .variableValueEqualsIgnoreCase(key, value)
                .variableValueEqualsIgnoreCase(serviceNameKey, serviceNameValue).list();
    }

    public ArkCaseComprehendMedicalService getArkCaseComprehendMedicalService()
    {
        return arkCaseComprehendMedicalService;
    }

    public void setArkCaseComprehendMedicalService(ArkCaseComprehendMedicalService arkCaseComprehendMedicalService)
    {
        this.arkCaseComprehendMedicalService = arkCaseComprehendMedicalService;
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
}
