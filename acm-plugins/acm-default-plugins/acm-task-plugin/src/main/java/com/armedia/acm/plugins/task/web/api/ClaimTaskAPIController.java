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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by manoj.dhungana on 2/7/2016.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class ClaimTaskAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    @RequestMapping(value = "/claim/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask claimTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        log.info("User [{}] is claiming workflow task with ID [{}]", authentication.getName(), taskId);
        try
        {
            getTaskDao().claimTask(taskId, authentication.getName());
            AcmTask claimedTask = getTaskDao().findById(taskId);
            publishTaskClaimEvent(authentication, httpSession, claimedTask, "claim", true);
            return getTaskDao().save(claimedTask);
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskClaimEvent(authentication, httpSession, fakeTask, "claim", false);
            throw new AcmUserActionFailedException("claim", "task", taskId, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/unclaim/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask unclaimTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        log.info("User [{}] is unclaiming workflow task with ID [{}]", authentication.getName(), taskId);
        try
        {
            getTaskDao().unclaimTask(taskId);
            AcmTask unclaimedTask = getTaskDao().findById(taskId);
            publishTaskClaimEvent(authentication, httpSession, unclaimedTask, "unclaim", true);
            return getTaskDao().save(unclaimedTask);
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskClaimEvent(authentication, httpSession, fakeTask, "unclaim", false);
            throw new AcmUserActionFailedException("unclaim", "task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskClaimEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask acmTask,
            String taskEvent,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(acmTask, taskEvent, authentication.getName(), succeeded, ipAddress);
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
