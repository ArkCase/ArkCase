package com.armedia.acm.plugins.task.web.api;

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
@RequestMapping({"/api/v1/plugin/service/notification", "/api/latest/plugin/service/notification"})
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

    @RequestMapping(value = "/jsapn/sendDueTaskEmailNotification", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<AcmTask>> queryTasks()
    {

        Map<String, List<AcmTask>> tasksMap = new HashMap<>();

        tasksMap.put("upcoming", upcomingTasksNotifier.queryTasks().collect(Collectors.toList()));
        tasksMap.put("overdue", overdueTasksNotifier.queryTasks().collect(Collectors.toList()));

        return tasksMap;

    }

}
