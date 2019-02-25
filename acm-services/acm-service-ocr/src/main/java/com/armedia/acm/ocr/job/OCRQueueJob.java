package com.armedia.acm.ocr.job;

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
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.exception.OCRServiceProviderNotFoundException;
import com.armedia.acm.ocr.exception.SaveOCRException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRProcessInstanceCreatedDateComparator;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.model.OCRType;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.ocr.utils.OCRUtils;
import com.armedia.acm.scheduler.AcmSchedulableBean;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

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

    @Override
    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

            OCRConfiguration configuration = getArkCaseOCRService().getConfiguration();
            List<OCR> processingOCRObjects = getArkCaseOCRService()
                    .getAllByStatus(OCRStatusType.PROCESSING.toString());
            List<OCR> processingOCRAutomaticObjects = processingOCRObjects.stream()
                    .filter(t -> OCRType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<OCR> processingOCRObjectsDistinctByProcessId = processingOCRAutomaticObjects.stream()
                    .filter(OCRUtils.distinctByProperty(OCR::getProcessId)).collect(Collectors.toList());

            if (configuration.getNumberOfFilesForProcessing() > processingOCRObjectsDistinctByProcessId.size())
            {
                String key = OCRBusinessProcessVariableKey.STATUS.toString();
                String value = OCRStatusType.QUEUED.toString();
                List<ProcessInstance> processInstances = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                        .variableValueEqualsIgnoreCase(key, value).list();

                if (processInstances != null && !processInstances.isEmpty())
                {
                    processInstances.sort(new OCRProcessInstanceCreatedDateComparator());
                    for (ProcessInstance processInstance : processInstances)
                    {
                        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");

                        if (ids != null && !ids.isEmpty())
                        {
                            OCR ocr = getArkCaseOCRService().get(ids.get(0));

                            AcmObjectLock lock = objectLockService.findLock(ocr.getEcmFileVersion().getFile().getId(), "FILE");
                            if (ocr != null && (lock == null || lock.getCreator().equalsIgnoreCase(OCRConstants.OCR_SYSTEM_USER)))
                            {
                                objectLockingManager.acquireObjectLock(ocr.getEcmFileVersion().getFile().getId(), "FILE", "WRITE",
                                        null,
                                        true,
                                        OCRConstants.OCR_SYSTEM_USER);
                                getArkCaseOCRService().getOCRServiceFactory().getService(configuration.getProvider()).create(ocr);
                                getArkCaseOCRService().save(ocr);
                                getArkCaseOCRService().signal(processInstance, OCRStatusType.PROCESSING.toString(),
                                        OCRActionType.PROCESSING.toString());
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (GetConfigurationException | SaveOCRException | GetOCRException | CreateOCRException | AcmObjectLockException
                | OCRServiceProviderNotFoundException e)
        {
            LOG.error("Could not move OCR from the queue. REASON=[{}]", e.getMessage(), e);
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

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }
}
