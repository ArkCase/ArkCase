package com.armedia.acm.plugins.ecm.workflow;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by armdev on 11/4/14.
 */
public class EcmFileWorkflowConfiguration
{
    /**
     * Input parameter - The file that has been updated; check whether to start a business process for this file.
     */
    private EcmFile ecmFile;

    /**
     * Output - whether a process should be started.
     */
    private boolean startProcess;

    /**
     * Output - name of the process to be started.
     */
    private String processName;

    /**
     * Output - Numeric priority to be assigned to any user tasks in the process ( 0 - 100 )
     */
    private int taskPriority;

    /**
     * Output - ISO-8601 duration expression: the duration is applied to the current date, to compute the due date
     * for any user tasks in the process.  See https://en.wikipedia.org/wiki/ISO_8601#Durations.
     */
    private String taskDueDateExpression;

    /**
     * Output - task name that would show up in the user interface.
     */
    private String taskName;

    /**
     * Output - comma-separated list of approvers.
     */
    private String approvers;

    /**
     * Output - specific type of workflow or document.  The document approval business process is used for many
     * document types and use cases; this field allows a more descriptive or specific process name than the actual
     * business process name.
     *
     * @return
     */
    private String requestType;

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

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getApprovers()
    {
        return approvers;
    }

    public void setApprovers(String approvers)
    {
        this.approvers = approvers;
    }

    public String getRequestType()
    {
        return requestType;
    }

    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }
}
