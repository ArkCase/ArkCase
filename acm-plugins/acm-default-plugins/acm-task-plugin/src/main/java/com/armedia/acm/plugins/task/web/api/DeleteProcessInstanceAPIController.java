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
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by manoj.dhungana on 2/7/2016.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class DeleteProcessInstanceAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    @RequestMapping(value = "/deleteProcessInstance/{parentObjectId}/{processInstanceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteProcessInstance(
            @PathVariable("parentObjectId") String parentObjectId,
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody String deleteReason,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        log.info("User [{}] is deleting workflow process with ID [{}]", authentication.getName(), processInstanceId);
        try
        {
            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            getTaskDao().deleteProcessInstance(parentObjectId, processInstanceId, deleteReason, authentication, ipAddress);

        }
        catch (AcmTaskException e)
        {
            log.debug("failed deleting process instance with ID [{}]", processInstanceId);
            throw new AcmUserActionFailedException("delete", "process instance", Long.valueOf(processInstanceId), e.getMessage(), e);
        }
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
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
