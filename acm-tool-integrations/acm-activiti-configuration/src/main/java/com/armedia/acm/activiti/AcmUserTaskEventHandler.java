package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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


import org.activiti.engine.RuntimeService;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Map;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmUserTaskEventHandler implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LogManager.getLogger(getClass());
    private RuntimeService runtimeService;

    public void handleTaskEvent(String eventName, Task task)
    {
        log.debug("Got a task event '{}'; execution of type '{}'", eventName, task.getClass().getName());

        Map<String, Object> processVariables = getRuntimeService().getVariables(task.getProcessInstanceId());
        log.debug("process variables from runtime: {}", processVariables.size());

        Map<String, Object> localVariables = getRuntimeService().getVariablesLocal(task.getExecutionId());
        log.debug("local variables from runtime: {}", processVariables.size());

        AcmTaskActivitiEvent event = new AcmTaskActivitiEvent(task, eventName, processVariables, localVariables);

        getApplicationEventPublisher().publishEvent(event);

    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public RuntimeService getRuntimeService()
    {
        return runtimeService;
    }

    public void setRuntimeService(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }
}
