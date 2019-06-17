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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class FindTaskByIdAPIController
{
    private AcmTaskService taskService;

    private TaskEventPublisher taskEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/byId/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public AcmTask findTaskById(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException
    {
        log.info("Finding task with id:'{}'", taskId);

        AcmTask task = getTaskService().retrieveTask(taskId);
        if (task != null)
        {
            raiseEvent(authentication, session, task, true);
            return task;
        }

        log.error("Could not find task with id:'{}'", taskId);
        raiseFakeEvent(taskId, authentication, session);
        throw new AcmObjectNotFoundException("task", taskId, null);
    }

    private void raiseFakeEvent(Long taskId, Authentication authentication, HttpSession session)
    {
        // gen up a fake task so we can audit the failure
        AcmTask fakeTask = new AcmTask();
        fakeTask.setTaskId(taskId);
        raiseEvent(authentication, session, fakeTask, false);
    }

    protected void raiseEvent(Authentication authentication, HttpSession session, AcmTask task, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "findById", authentication.getName(),
                succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public AcmTaskService getTaskService()
    {
        return taskService;
    }

    public void setTaskService(AcmTaskService taskService)
    {
        this.taskService = taskService;
    }
}
