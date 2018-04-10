package com.armedia.acm.services.transcribe.job;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.scheduler.AcmSchedulableBean;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.TranscribeServiceProviderNotFoundException;
import com.armedia.acm.services.transcribe.model.*;
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
            List<Transcribe> processingTranscribeObjects = getArkCaseTranscribeService().getAllByStatus(TranscribeStatusType.PROCESSING.toString());
            List<Transcribe> processingTranscribeAutomaticObjects = processingTranscribeObjects.stream().filter(t -> TranscribeType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
            List<Transcribe> processingTranscribeObjectsDistinctByProcessId = processingTranscribeAutomaticObjects.stream().filter(TranscribeUtils.distinctByProperty(Transcribe::getProcessId)).collect(Collectors.toList());

            if (configuration.getNumberOfFilesForProcessing() > processingTranscribeObjectsDistinctByProcessId.size())
            {
                String key = TranscribeBusinessProcessVariableKey.STATUS.toString();
                String value = TranscribeStatusType.QUEUED.toString();
                List<ProcessInstance> processInstances = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables().variableValueEqualsIgnoreCase(key, value).list();

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
                            getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider()).create(transcribe);
                            getArkCaseTranscribeService().signal(processInstance, TranscribeStatusType.PROCESSING.toString(), TranscribeActionType.PROCESSING.toString());
                        }
                    }
                }
            }
        }
        catch (GetConfigurationException | GetTranscribeException | CreateTranscribeException | TranscribeServiceProviderNotFoundException e)
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
