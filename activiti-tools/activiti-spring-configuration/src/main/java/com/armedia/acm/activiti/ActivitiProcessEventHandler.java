package com.armedia.acm.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class ActivitiProcessEventHandler implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private RuntimeService runtimeService;
    private ApplicationEventPublisher applicationEventPublisher;
    private HistoryService historyService;

    public void handleProcessEvent(String eventName, ProcessInstance execution)
    {
        log.info("Got an Activiti event; eventName: " + eventName + "; " +
            "process instance id: " + execution.getProcessInstanceId());


        String acmUser = findLastUserToCompleteATask(execution.getProcessInstanceId());

        Map<String, Object> processVariables = getRuntimeService().getVariables(execution.getId());

        AcmBusinessProcessEvent event = new AcmBusinessProcessEvent(execution);
        event.setEventType("com.armedia.acm.activiti.businessProcess." + eventName);
        event.setProcessVariables(processVariables);
        event.setEventProperties(processVariables);
        event.setUserId(acmUser);

        applicationEventPublisher.publishEvent(event);

    }

    protected String findLastUserToCompleteATask(String processInstanceId)
    {
        HistoricTaskInstance lastCompletedTask = getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .singleResult();
        String acmUser = "ACTIVITI_SYSTEM";
        if ( lastCompletedTask != null && lastCompletedTask.getAssignee() != null && lastCompletedTask.getEndTime() != null )
        {
            log.debug("Found the last assignee to complete a task: " + lastCompletedTask.getAssignee() +
                "; task end time: " + lastCompletedTask.getEndTime());
            acmUser = lastCompletedTask.getAssignee();
        }
        return acmUser;
    }

    public RuntimeService getRuntimeService()
    {
        return runtimeService;
    }

    public void setRuntimeService(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public HistoryService getHistoryService()
    {
        return historyService;
    }

    public void setHistoryService(HistoryService historyService)
    {
        this.historyService = historyService;
    }
}
