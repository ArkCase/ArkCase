package com.armedia.acm.plugins.ecm.workflow;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
     * for any user tasks in the process. See https://en.wikipedia.org/wiki/ISO_8601#Durations.
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
     * Output - specific type of workflow or document. The document approval business process is used for many
     * document types and use cases; this field allows a more descriptive or specific process name than the actual
     * business process name.
     *
     * @return
     */
    private String requestType;

    /**
     * should enable buckslip process
     */
    private boolean buckslipProcess;

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

    public boolean isBuckslipProcess()
    {
        return buckslipProcess;
    }

    public void setBuckslipProcess(boolean buckslipProcess)
    {
        this.buckslipProcess = buckslipProcess;
    }
}
