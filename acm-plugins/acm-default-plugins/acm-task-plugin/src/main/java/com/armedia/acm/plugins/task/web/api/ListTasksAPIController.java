package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class ListTasksAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/forUser/{user:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> tasksForUser(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session) throws AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Finding tasks assigned to user '" + user + "'");
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        try
        {
            List<AcmTask> retval = getTaskDao().tasksForUser(user);
            // to do: we also should get back the tasks that owner by this user
            for (AcmTask task : retval)
            {
                AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "searchResult",
                        authentication.getName(), true, ipAddress);
                getTaskEventPublisher().publishTaskEvent(event);

            }

            return retval;
        }
        catch (Exception e)
        {
            log.error("List Tasks Failed: " + e.getMessage(), e);
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
