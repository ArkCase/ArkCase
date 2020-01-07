package com.armedia.acm.plugins.task.web.api;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by manoj.dhungana on 11/13/2014.
 */
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })

public class DeleteTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/deleteTask/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask completeTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Deleting task '" + taskId + "'");
        }

        try
        {
            AcmTask deleted = getTaskDao().deleteTask(authentication, taskId);

            publishTaskDeletedEvent(authentication, httpSession, deleted, true);

            return deleted;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskDeletedEvent(authentication, httpSession, fakeTask, false);

            throw new AcmUserActionFailedException("delete", "task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskDeletedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask completed,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(completed, "delete", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }
}
