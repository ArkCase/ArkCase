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
import com.armedia.acm.plugins.task.model.TaskOutcome;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CompleteTaskWithOutcomeAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/completeTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask completeTask(Authentication authentication, @RequestBody AcmTask in, HttpSession httpSession)
            throws AcmUserActionFailedException
    {
        log.info("Completing task '{}'", in.getTaskId());

        try
        {
            AcmTask current = getTaskDao().findById(in.getTaskId());

            validateCompleteTaskRequirements(current, in);

            // if we get here, it must be OK
            getTaskDao().save(in);

            AcmTask completed = getTaskDao().completeTask(authentication, in.getTaskId(), in.getOutcomeName(),
                    in.getTaskOutcome() == null ? null : in.getTaskOutcome().getName());
            completed.setTaskOutcome(in.getTaskOutcome());

            publishTaskCompletedEvent(authentication, httpSession, completed, true,
                    in.getTaskOutcome() == null ? null : in.getTaskOutcome().getName());

            return completed;
        }
        catch (AcmTaskException e)
        {
            publishTaskCompletedEvent(authentication, httpSession, in, false);

            throw new AcmUserActionFailedException("complete", "task", in.getTaskId(), e.getMessage(), e);
        }
        catch (AcmUserActionFailedException e)
        {
            publishTaskCompletedEvent(authentication, httpSession, in, false);
            throw e;
        }
    }

    private void validateCompleteTaskRequirements(AcmTask current, AcmTask in) throws AcmUserActionFailedException
    {
        // if a task has available outcomes, the task outcome must be set
        if (current.getAvailableOutcomes() != null && !current.getAvailableOutcomes().isEmpty())
        {
            // make sure a task outcome was chosen
            if (in.getTaskOutcome() == null)
            {
                throw new AcmUserActionFailedException("complete", "task", in.getTaskId(),
                        "Outcome must be selected to completed this task", null);
            }

            // now make sure it is valid (matches an available task outcome)
            TaskOutcome selectedTaskOutcome = null;
            for (TaskOutcome availableTaskOutcome : current.getAvailableOutcomes())
            {
                if (in.getTaskOutcome().getName().equals(availableTaskOutcome.getName()))
                {
                    selectedTaskOutcome = availableTaskOutcome;
                }
            }

            if (selectedTaskOutcome == null)
            {
                throw new AcmUserActionFailedException("complete", "task", in.getTaskId(), "Selected outcome is not valid for this task",
                        null);
            }

            // now make sure any required fields are populated
            if (selectedTaskOutcome.getFieldsRequiredWhenOutcomeIsChosen() != null
                    && !selectedTaskOutcome.getFieldsRequiredWhenOutcomeIsChosen().isEmpty())
            {
                JSONObject json = new JSONObject(in);
                for (String required : selectedTaskOutcome.getFieldsRequiredWhenOutcomeIsChosen())
                {
                    if (!json.has(required) || json.get(required) == null)
                    {
                        throw new AcmUserActionFailedException("complete", "task", in.getTaskId(),
                                "Required field '" + required + "' must be set to complete this task", null);
                    }
                }
            }

        }
    }

    protected void publishTaskCompletedEvent(Authentication authentication, HttpSession httpSession, AcmTask completed, boolean succeeded)
    {
        publishTaskCompletedEvent(authentication, httpSession, completed, succeeded, null);
    }

    private void publishTaskCompletedEvent(Authentication authentication, HttpSession httpSession, AcmTask completed, boolean succeeded,
            String eventDescription)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(completed, "complete", authentication.getName(), succeeded, ipAddress);
        event.setEventDescription(eventDescription);
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
