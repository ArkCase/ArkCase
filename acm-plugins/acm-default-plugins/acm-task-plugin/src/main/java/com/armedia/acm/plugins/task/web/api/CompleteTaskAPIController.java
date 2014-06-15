package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTaskCompletedEvent;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CompleteTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/completeTask/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask completeTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Completing task '" + taskId + "'");
        }

        try
        {
            AcmTask completed = getTaskDao().completeTask(authentication, taskId);

            publishTaskCompletedEvent(authentication, httpSession, completed, true);

            return completed;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskCompletedEvent(authentication, httpSession, fakeTask, false);

            throw new AcmUserActionFailedException("complete", "task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskCompletedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask completed,
            boolean succeeded)
    {
        AcmTaskCompletedEvent event = new AcmTaskCompletedEvent(completed);
        event.setSucceeded(succeeded);
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        getTaskEventPublisher().publishTaskEvent(event, authentication, ipAddress);
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
