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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTasksForAPeriod;
import com.armedia.acm.plugins.task.model.NumberOfDays;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marst on 8/16/14.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class ListAllTasksAPIController
{

    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/list/{due}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> tasksForUser(
            @PathVariable("due") String due,
            Authentication authentication,
            HttpSession session) throws AcmListObjectsFailedException
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        try
        {
            List<AcmTask> retval = null;
            switch (AcmTasksForAPeriod.getTasksForPeriodByText(due))
            {
            case ALL:
                if (log.isInfoEnabled())
                {
                    log.info("Finding tasks assigned to all users ");
                }
                retval = getTaskDao().allTasks();
                break;
            case PAST_DUE:
                retval = getTaskDao().pastDueTasks();
                break;
            case DUE_TOMORROW:
                retval = getTaskDao().dueSpecificDateTasks(NumberOfDays.ONE_DAY);
                break;
            case DUE_IN_7_DAYS:
                retval = getTaskDao().dueSpecificDateTasks(NumberOfDays.SEVEN_DAYS);
                break;
            case DUE_IN_30_DAYS:
                retval = getTaskDao().dueSpecificDateTasks(NumberOfDays.THIRTY_DAYS);
                break;
            default:
                retval = getTaskDao().allTasks();
                break;
            }

            for (AcmTask task : retval)
            {
                AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "searchResult",
                        authentication.getName(), true, ipAddress);
                getTaskEventPublisher().publishTaskEvent(event);
            }

            // return only tasks that are assigned to someone.
            return retval.stream().filter(task -> !"".equals(task.getAssignee()) && task.getAssignee() != null)
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw new AcmListObjectsFailedException("task", e.getMessage(), e);
        }
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
