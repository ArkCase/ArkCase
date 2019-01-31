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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.exception.OCRServiceProviderNotFoundException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.model.OCRType;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.ocr.utils.OCRUtils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRProcessDelegate implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(OCRBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                OCR ocr = getArkCaseOCRService().get(ids.get(0));
                if (OCRStatusType.QUEUED.toString().equals(ocr.getStatus()))
                {
                    OCRConfiguration configuration = getArkCaseOCRService().getConfiguration();
                    List<OCR> processingOCRObjects = getArkCaseOCRService()
                            .getAllByStatus(OCRStatusType.PROCESSING.toString());
                    List<OCR> processingOCRAutomaticObjects = processingOCRObjects.stream()
                            .filter(t -> OCRType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
                    List<OCR> processingOCRObjectsDistinctByProcessId = processingOCRAutomaticObjects.stream()
                            .filter(OCRUtils.distinctByProperty(OCR::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingOCRObjectsDistinctByProcessId.size())
                    {
                        try
                        {
                            if (ocr.getProcessId() == null)
                            {
                                ocr.setProcessId(delegateExecution.getProcessInstanceId());
                            }
                            getArkCaseOCRService().getOCRServiceFactory().getService(configuration.getProvider()).create(ocr);
                            getArkCaseOCRService().save(ocr);
                            delegateExecution.setVariable(OCRBusinessProcessVariableKey.STATUS.toString(),
                                    OCRStatusType.PROCESSING.toString());
                            delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                                    OCRActionType.PROCESSING.toString());
                        }
                        catch (OCRServiceProviderNotFoundException | CreateOCRException e)
                        {
                            LOG.error("Error while calling PROVIDER=[{}] to OCR the media for PROCESS_ID=[{}]. REASON=[{}]",
                                    configuration.getProvider().toString(), delegateExecution.getProcessInstanceId(), e.getMessage(), e);
                        }
                    }
                }

            }
            catch (GetOCRException | GetConfigurationException e)
            {
                LOG.warn("Could not check if OCR should be processed for PROCESS_ID=[{}]. REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
