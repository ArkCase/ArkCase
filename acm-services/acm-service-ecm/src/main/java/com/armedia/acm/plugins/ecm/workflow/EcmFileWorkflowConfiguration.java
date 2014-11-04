package com.armedia.acm.plugins.ecm.workflow;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by armdev on 11/4/14.
 */
public class EcmFileWorkflowConfiguration
{
    private EcmFile ecmFile;

    private boolean startProcess;
    private String processName;
    private int taskPriority;
    private String taskDueDateExpression;

    public boolean isStartProcess()
    {
        return startProcess;
    }

    public void setStartProcess(boolean startProcess)
    {
        this.startProcess = startProcess;
    }

    public String getProcessName()
    {
        return processName;
    }

    public void setProcessName(String processName)
    {
        this.processName = processName;
    }

    public int getTaskPriority()
    {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority)
    {
        this.taskPriority = taskPriority;
    }

    public String getTaskDueDateExpression()
    {
        return taskDueDateExpression;
    }

    public void setTaskDueDateExpression(String taskDueDateExpression)
    {
        this.taskDueDateExpression = taskDueDateExpression;
    }

    public EcmFile getEcmFile()
    {
        return ecmFile;
    }

    public void setEcmFile(EcmFile ecmFile)
    {
        this.ecmFile = ecmFile;
    }
}
