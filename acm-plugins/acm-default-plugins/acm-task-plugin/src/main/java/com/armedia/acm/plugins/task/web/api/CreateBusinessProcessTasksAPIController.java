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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Created by vladimir.radeski on 10/24/2017.
 */

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task", "/api/v1/plugin/tasks", "/api/latest/plugin/tasks" })
public class CreateBusinessProcessTasksAPIController
{
    private TaskEventPublisher taskEventPublisher;
    private AcmTaskService taskService;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/documents/review", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> reviewDocuments(@RequestBody AcmTask in,
            @RequestParam(value = "businessProcessName", defaultValue = "acmDocumentWorkflow") String businessProcessName,
            Authentication authentication, HttpSession httpSession)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException
    {
        try
        {
            List<AcmTask> acmTasks = null;
            if (businessProcessName.equals("arrestWarrant"))
            {
                getTaskService().startArrestWarrantWorkflow(in);
            }
            else
            {
                acmTasks = getTaskService().startReviewDocumentsWorkflow(in, businessProcessName, authentication);
            }

            return acmTasks;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(-1L); // no object id since the task could not be created
            publishTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/newdocuments/review", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public List<AcmTask> reviewNewDocuments(@RequestPart(name = "task") AcmTask task,
            @RequestParam(name = "businessProcessName") String businessProcessType,
            @RequestPart(name = "files") List<MultipartFile> filesToUpload,
            Authentication authentication, HttpSession httpSession)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException,
            AcmObjectNotFoundException
    {

        try
        {

            List<AcmTask> acmTasks = getTaskService().startReviewDocumentsWorkflow(task, businessProcessType, authentication,
                    filesToUpload);
            // Add participant to folder links
            for (AcmTask acmTask : acmTasks)
            {
                getTaskService().setParticipantsToTaskFolderLink(acmTask);
            }
            return acmTasks;

        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(-1L); // no object id since the task could not be created
            publishTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }

    }

    protected void publishTaskCreatedEvent(Authentication authentication, HttpSession httpSession, AcmTask created, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
        if (created.getStatus() != null && created.getStatus().equalsIgnoreCase(TaskConstants.STATE_CLOSED))
        {
            event = new AcmApplicationTaskEvent(created, "complete", authentication.getName(), succeeded, ipAddress);
            getTaskEventPublisher().publishTaskEvent(event);
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

    public AcmTaskService getTaskService()
    {
        return taskService;
    }

    public void setTaskService(AcmTaskService taskService)
    {
        this.taskService = taskService;
    }

}
