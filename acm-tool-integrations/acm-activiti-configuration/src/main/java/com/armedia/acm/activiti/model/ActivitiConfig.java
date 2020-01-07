package com.armedia.acm.activiti.model;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class ActivitiConfig
{
    @JsonProperty("activiti.job.executor.activate")
    @Value("${activiti.job.executor.activate}")
    private Boolean jobExecutorActivate;

    @Value("${activiti.workflow.file.pattern}")
    private String activitiWorkflowFilePattern;

    public Boolean getJobExecutorActivate()
    {
        return jobExecutorActivate;
    }

    public void setJobExecutorActivate(Boolean jobExecutorActivate)
    {
        this.jobExecutorActivate = jobExecutorActivate;
    }

    /**
     * @return the activitiWorkflowFilePattern
     */
    public String getActivitiWorkflowFilePattern()
    {
        return activitiWorkflowFilePattern;
    }

    /**
     * @param activitiWorkflowFilePattern
     *            the activitiWorkflowFilePattern to set
     */
    public void setActivitiWorkflowFilePattern(String activitiWorkflowFilePattern)
    {
        this.activitiWorkflowFilePattern = activitiWorkflowFilePattern;
    }
}
