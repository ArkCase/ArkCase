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
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.service.ArkCaseOCRService;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRPurgeDelegate implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        LOG.debug("Purge OCR information for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(OCRBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many)
            try
            {
                OCR ocr = getArkCaseOCRService().get(ids.get(0));
                OCRConfiguration configuration = getArkCaseOCRService().getConfiguration();
                int purgeAttempts = 0;
                int purgeAttemptsInConfiguration = configuration.getProviderPurgeAttempts();
                if (delegateExecution.hasVariable(OCRBusinessProcessVariableKey.PURGE_ATTEMPTS.toString()))
                {
                    purgeAttempts = (int) delegateExecution.getVariable(OCRBusinessProcessVariableKey.PURGE_ATTEMPTS.toString());
                }

                if (purgeAttempts < purgeAttemptsInConfiguration)
                {
                    boolean purged = getArkCaseOCRService().getOCRServiceFactory().getService(configuration.getProvider()).purge(ocr);

                    if (purged)
                    {
                        LOG.debug("OCR information for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] on provider side are purged.",
                                delegateExecution.getProcessInstanceId(), ocr.getRemoteId());
                        delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                                OCRActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn("OCR information for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] on provider side are not purged.",
                                delegateExecution.getProcessInstanceId(), ocr.getRemoteId());
                        delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                                OCRActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(OCRBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] exceeded. Terminating purge job.",
                            delegateExecution.getProcessInstanceId(), ocr.getRemoteId());
                    delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                            OCRActionType.PURGE_TERMINATE.toString());
                }
            }
            catch (Exception e)
            {
                LOG.warn("Could not purge OCR information on provider side. PROCESS_ID=[{}], REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
                delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                        OCRActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job for PROCESS_ID=[{}] cannot proceed because there is no OCR. Terminating purge job.",
                    delegateExecution.getProcessInstanceId());
            delegateExecution.setVariable(OCRBusinessProcessVariableKey.ACTION.toString(),
                    OCRActionType.PURGE_TERMINATE.toString());
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
