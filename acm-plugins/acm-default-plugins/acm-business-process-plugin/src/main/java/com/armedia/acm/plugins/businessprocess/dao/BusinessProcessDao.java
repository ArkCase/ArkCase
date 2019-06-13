package com.armedia.acm.plugins.businessprocess.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcess;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcessConstants;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BusinessProcessDao extends AcmAbstractDao<BusinessProcess>
{
    private RuntimeService activitiRuntimeService;
    private TaskService activitiTaskService;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    protected Class<BusinessProcess> getPersistenceClass()
    {
        return BusinessProcess.class;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Long> findTasksIdsForParentObjectIdAndParentObjectType(String parentObjectType, Long parentObjectId)
    {
        List<ProcessInstance> processes = getActivitiRuntimeService().createProcessInstanceQuery()
                .variableValueEquals(BusinessProcessConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType)
                .variableValueEquals(BusinessProcessConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId).list();

        Stream<Task> activitiWorkflowTasksStream = processes.stream()
                .map(it -> getActivitiTaskService().createTaskQuery()
                        .processInstanceId(it.getProcessInstanceId())
                        .singleResult());

        Stream<Task> adhochTasksStream = getActivitiTaskService().createTaskQuery()
                .taskVariableValueEquals(BusinessProcessConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType)
                .taskVariableValueEquals(BusinessProcessConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId)
                .list().stream();

        List<Long> taskIds = Stream.concat(activitiWorkflowTasksStream, adhochTasksStream)
                .map(it -> Long.valueOf(it.getId()))
                .collect(Collectors.toList());
        log.debug("Found [{}] tasks for object [{}:{}]", taskIds.size(), parentObjectType, parentObjectId);
        return taskIds;
    }

    public RuntimeService getActivitiRuntimeService() 
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService) 
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public TaskService getActivitiTaskService() 
    {
        return activitiTaskService;
    }

    public void setActivitiTaskService(TaskService activitiTaskService) 
    {
        this.activitiTaskService = activitiTaskService;
    }
}
