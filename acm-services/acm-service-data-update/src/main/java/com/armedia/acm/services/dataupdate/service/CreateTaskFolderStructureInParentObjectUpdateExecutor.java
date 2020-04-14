package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public class CreateTaskFolderStructureInParentObjectUpdateExecutor implements AcmDataUpdateExecutor
{
    private AcmTaskService acmTaskService;
    private AcmFolderDao acmFolderDao;
    private final Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;

    @Override
    public String getUpdateId()
    {
        return "create_task_folder_structure_in_parent_object";
    }

    @Override
    @Async
    public void execute()
    {

        List<AcmTask> allTasksFromSolr = getTaskDao().allTasks();

        try
        {
            for (AcmTask task : allTasksFromSolr)
            {
                if (task.getParentObjectId() != null)
                {
                    String taskFolderName = getAcmTaskService().getTaskFolderNameInParentObject(task);
                    if (getAcmFolderDao().findAnyFolderByName(taskFolderName) == null)
                    {
                        getAcmTaskService().createTaskFolderStructureInParentObject(task);
                    }
                }
            }
        }
        catch (AcmCreateObjectFailedException | AcmUserActionFailedException | AcmObjectNotFoundException | LinkAlreadyExistException e)
        {
            log.error("Error on creating Task Folder Structure in Parent Object", e);
        }
    }

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }

    public AcmFolderDao getAcmFolderDao()
    {
        return acmFolderDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao)
    {
        this.acmFolderDao = acmFolderDao;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }
}
