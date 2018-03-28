package com.armedia.acm.services.transcribe.job;

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
public class ProcessInstanceTest implements ProcessInstance
{
    private String id;
    private Map<String, Object> processVariables;

    public ProcessInstanceTest(String id, Map<String, Object> processVariables)
    {
        this.id = id;
        this.processVariables = processVariables;
    }

    @Override
    public String getProcessDefinitionId()
    {
        return null;
    }

    @Override
    public String getBusinessKey()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public boolean isSuspended()
    {
        return false;
    }

    @Override
    public boolean isEnded()
    {
        return false;
    }

    @Override
    public String getActivityId()
    {
        return null;
    }

    @Override
    public String getProcessInstanceId()
    {
        return null;
    }

    @Override
    public String getParentId()
    {
        return null;
    }

    @Override
    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    @Override
    public String getTenantId()
    {
        return null;
    }
}
