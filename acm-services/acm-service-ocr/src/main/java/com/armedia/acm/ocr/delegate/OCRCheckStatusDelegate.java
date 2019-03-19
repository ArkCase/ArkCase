package com.armedia.acm.ocr.delegate;

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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRCheckStatusDelegate implements JavaDelegate
{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EcmFileService ecmFileService;
    private RuntimeService activitiRuntimeService;
    private AcmObjectLockingManager objectLockingManager;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        LOGGER.debug("Check the status for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(OCRBusinessProcessVariableKey.IDS.toString());
        String action = OCRActionType.PROCESSING.toString();
        String previousAction = (String) delegateExecution.getVariable(OCRBusinessProcessVariableKey.ACTION.toString());

        if (ids != null && !ids.isEmpty() && action.equalsIgnoreCase(previousAction))
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                OCR ocr = getArkCaseOCRService().get(ids.get(0));
                if (!(OCRStatusType.PROCESSING.toString().equalsIgnoreCase(ocr.getStatus())))
                {
                    ocr.setStatus(OCRStatusType.PROCESSING.toString());
                    getArkCaseOCRService().save(ocr);
                }

                OCRConfiguration configuration = getArkCaseOCRService().getConfiguration();
                OCR providerOCR = getArkCaseOCRService().getOCRServiceFactory().getService(configuration.getProvider())
                        .get(ocr.getRemoteId());

                if (providerOCR != null && !OCRStatusType.PROCESSING.toString().equals(providerOCR.getStatus()))
                {
                    String status = providerOCR.getStatus();

                    switch (OCRStatusType.valueOf(providerOCR.getStatus()))
                    {
                    case COMPLETED:
                        action = OCRActionType.COMPLETED.toString();
                        for (Long id : ids)
                        {
                            try
                            {
                                Authentication authentication = SecurityContextHolder.getContext() != null
                                        && SecurityContextHolder.getContext().getAuthentication() != null
                                                ? SecurityContextHolder.getContext().getAuthentication()
                                                : new AcmAuthentication(null, null, null, true,
                                                        OCRConstants.OCR_SYSTEM_USER);

                                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
                                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

                                String fileName = ocr.getEcmFileVersion().getFile().getFileName();
                                String fileLocation;

                                if (getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.QPDF_TMP) != null)
                                {
                                    fileLocation = (String) getActivitiRuntimeService().getVariable(ocr.getProcessId(),
                                            OCRConstants.QPDF_TMP);
                                    File file = new File(fileLocation);
                                    try (InputStream stream = new FileInputStream(fileLocation))
                                    {
                                        AcmMultipartFile multipartFile = new AcmMultipartFile(fileName, fileName, "application/pdf",
                                                false,
                                                file.length(),
                                                new byte[0], stream, true);

                                        getEcmFileService().update(ocr.getEcmFileVersion().getFile(), multipartFile,
                                                authentication);
                                    }
                                }
                                objectLockingManager.releaseObjectLock(ocr.getEcmFileVersion().getFile().getId(), "FILE", "WRITE", true,
                                        OCRConstants.OCR_SYSTEM_USER, null);
                            }
                            catch (Exception e)
                            {
                                LOGGER.warn("Taking items for OCR with ID=[{}] and PROCESS_ID=[{}] failed. REASON=[{}]", id,
                                        delegateExecution.getProcessInstanceId(), e.getMessage());
                            }
                        }
                        break;
                    case FAILED:
                        action = OCRActionType.FAILED.toString();
                        objectLockingManager.releaseObjectLock(ocr.getEcmFileVersion().getFile().getId(), "FILE", "WRITE", true,
                                OCRConstants.OCR_SYSTEM_USER, null);
                        break;

                    default:
                        throw new RuntimeException(
                                String.format("Received OCR status type of [%s] for OCR_ID=[%s] and FILE_ID=[%s], but cannot handle it.",
                                        status, ocr.getId(), ocr.getEcmFileVersion().getFile().getId()));
                    }

                    delegateExecution.setVariable(OCRBusinessProcessVariableKey.STATUS.toString(), status);
                    delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(), action);
                }
            }
            catch (GetOCRException | GetConfigurationException e)
            {
                LOGGER.warn("Could not check if OCR should be completed. PROCESS_ID=[{}], REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
            }

            delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(), action);
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }
}
