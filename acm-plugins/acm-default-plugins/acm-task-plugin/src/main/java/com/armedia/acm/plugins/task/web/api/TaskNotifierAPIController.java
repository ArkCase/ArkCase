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

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AbstractTaskNotifier;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 18, 2016
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class TaskNotifierAPIController
{

    private AbstractTaskNotifier upcomingTasksNotifier;

    private AbstractTaskNotifier overdueTasksNotifier;

    /**
     * @param upcomingTasksNotifier
     *            the upcomingTasksNotifier to set
     */
    public void setUpcomingTasksNotifier(AbstractTaskNotifier upcomingTasksNotifier)
    {
        this.upcomingTasksNotifier = upcomingTasksNotifier;
    }

    /**
     * @param overdueTasksNotifier
     *            the overdueTasksNotifier to set
     */
    public void setOverdueTasksNotifier(AbstractTaskNotifier overdueTasksNotifier)
    {
        this.overdueTasksNotifier = overdueTasksNotifier;
    }

    @RequestMapping(value = "/task/sendDueTaskEmailNotification", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<AcmTask>> queryTasks()
    {

        Map<String, List<AcmTask>> tasksMap = new HashMap<>();

        tasksMap.put("upcoming", upcomingTasksNotifier.queryTasks().collect(Collectors.toList()));
        tasksMap.put("overdue", overdueTasksNotifier.queryTasks().collect(Collectors.toList()));

        return tasksMap;

    }

}
