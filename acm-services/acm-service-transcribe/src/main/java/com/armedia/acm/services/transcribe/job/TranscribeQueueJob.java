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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.scheduler.AcmSchedulableBean;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.TranscribeServiceProviderNotFoundException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeProcessInstanceCreatedDateComparator;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
public class TranscribeQueueJob implements AcmSchedulableBean
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private RuntimeService activitiRuntimeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void executeTask()
    {
        try
        {
            getAuditPropertyEntityAdapter().setUserId("TRANSCRIBE_SERVICE");

            TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
            List<Transcribe> processingTranscribeObjects = getArkCaseTranscribeService()
                    .getAllByStatus(TranscribeStatusType.PROCESSING.toString());
            List<Transcribe> processingTranscribeAutomaticObjects = processingTranscribeObjects.stream()
                    .filter(t -> TranscribeType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<Transcribe> processingTranscribeObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream()
                    .filter(TranscribeUtils.distinctByProperty(Transcribe::getProcessId)).collect(Collectors.toList());

            if (configuration.getNumberOfFilesForProcessing() > processingTranscribeObjectsDistinctByProcessId.size())
            {
                String key = TranscribeBusinessProcessVariableKey.STATUS.toString();
                String value = TranscribeStatusType.QUEUED.toString();
                List<ProcessInstance> processInstances = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                        .variableValueEqualsIgnoreCase(key, value).list();

                if (processInstances != null && processInstances.size() > 0)
                {
                    processInstances.sort(new TranscribeProcessInstanceCreatedDateComparator());
                    ProcessInstance processInstance = processInstances.get(0);
                    List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");

                    if (ids != null && ids.size() > 0)
                    {
                        Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                        if (transcribe != null)
                        {
                            getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider())
                                    .create(transcribe);
                            getArkCaseTranscribeService().signal(processInstance, TranscribeStatusType.PROCESSING.toString(),
                                    TranscribeActionType.PROCESSING.toString());
                        }
                    }
                }
            }
        }
        catch (GetConfigurationException | GetTranscribeException | CreateTranscribeException
                | TranscribeServiceProviderNotFoundException e)
        {
            LOG.error("Could not move Transcribe from the queue. REASON=[{}]", e.getMessage(), e);
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
}
