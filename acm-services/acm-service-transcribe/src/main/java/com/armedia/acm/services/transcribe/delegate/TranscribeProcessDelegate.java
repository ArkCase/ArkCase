package com.armedia.acm.services.transcribe.delegate;

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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.TranscribeServiceProviderNotFoundException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/08/2018
 */
public class TranscribeProcessDelegate implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.IDS.toString());

        if (ids != null && ids.size() > 0)
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                if (TranscribeStatusType.QUEUED.toString().equals(transcribe.getStatus()))
                {
                    TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                    List<Transcribe> processingTranscribeObjects = getArkCaseTranscribeService()
                            .getAllByStatus(TranscribeStatusType.PROCESSING.toString());
                    List<Transcribe> processingTranscribeAutomaticObjects = processingTranscribeObjects.stream()
                            .filter(t -> TranscribeType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
                    List<Transcribe> processingTranscribeObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream()
                            .filter(TranscribeUtils.distinctByProperty(Transcribe::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingTranscribeObjectsDistinctByProcessId.size())
                    {
                        try
                        {
                            // Create Transcribe Job on provider side and set the Status and Action to PROCESSING
                            getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider())
                                    .create(transcribe);
                            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.STATUS.toString(),
                                    TranscribeStatusType.PROCESSING.toString());
                            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                                    TranscribeActionType.PROCESSING.toString());
                        }
                        catch (TranscribeServiceProviderNotFoundException | CreateTranscribeException e)
                        {
                            LOG.error("Error while calling PROVIDER=[{}] to transcribe the media. REASON=[{}]",
                                    configuration.getProvider().toString(), e.getMessage(), e);
                        }
                    }
                }

            }
            catch (GetTranscribeException | GetConfigurationException e)
            {
                LOG.warn("Could not check if Transcribe should be processed. REASON=[{}]", e.getMessage());
            }
        }
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
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
