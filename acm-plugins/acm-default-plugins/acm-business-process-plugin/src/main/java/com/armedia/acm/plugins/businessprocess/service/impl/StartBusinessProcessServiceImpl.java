package com.armedia.acm.plugins.businessprocess.service.impl;

import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StartBusinessProcessServiceImpl implements StartBusinessProcessService
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RuntimeService activitiRuntimeService;

    @Override
    public void startBusinessProcess(String processName, Map<String, Object> processVariables)
    {
        log.debug("Starting process named: {}.", processName);
        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, processVariables);
        log.debug("Started process with ID: {}.", pi.getId());
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
