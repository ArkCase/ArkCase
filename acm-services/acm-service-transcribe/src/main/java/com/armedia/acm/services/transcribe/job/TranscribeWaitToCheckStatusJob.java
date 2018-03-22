package com.armedia.acm.services.transcribe.job;

import com.armedia.acm.scheduler.AcmSchedulableBean;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/16/2018
 */
public class TranscribeWaitToCheckStatusJob  implements AcmSchedulableBean
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private RuntimeService activitiRuntimeService;

    @Override
    public void executeTask()
    {
        try
        {
            String key = TranscribeBusinessProcessVariableKey.ACTION.toString();
            String value = TranscribeStatusType.PROCESSING.toString();
            List<ProcessInstance> processInstances = getActivitiRuntimeService().createProcessInstanceQuery().variableValueEqualsIgnoreCase(key, value).list();

            if (processInstances != null && processInstances.size() > 0)
            {

                processInstances.sort(new TranscribeProcessInstanceCreatedDateComparator());

                List<ProcessInstance> filteredProcessInstances = processInstances.stream().filter(processInstance -> "waitCheckStatus".equalsIgnoreCase(processInstance.getActivityId())).collect(Collectors.toList());

                filteredProcessInstances.forEach(processInstance -> {
                    getArkCaseTranscribeService().signal(processInstance, TranscribeStatusType.PROCESSING.toString(), "CHECK_STATUS");
                });
            }
        }
        catch (Exception e)
        {
            LOG.error("Could not read Transcribe configuration. REASON=[{}]", e.getMessage(), e);
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
}
