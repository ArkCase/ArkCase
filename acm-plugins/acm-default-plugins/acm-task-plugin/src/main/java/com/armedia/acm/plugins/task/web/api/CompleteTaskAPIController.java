package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTaskCompletedEvent;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CompleteTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private AcmSpringMvcErrorManager errorManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/completeTask/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask completeTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws IOException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Completing task '" + taskId + "'");
        }

        try
        {
            AcmTask completed = getTaskDao().completeTask(authentication, taskId);

            publishTaskCompletedEvent(authentication, httpSession, completed);

            return completed;
        }
        catch (AcmTaskException e)
        {
            getErrorManager().sendErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), response);
        }

        // Spring MVC has already flushed the response output stream during sendErrorResponse, but Java
        // makes us return something.
        return null;
    }

    protected void publishTaskCompletedEvent(Authentication authentication, HttpSession httpSession, AcmTask completed)
    {
        AcmTaskCompletedEvent event = new AcmTaskCompletedEvent(completed);
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

    public AcmSpringMvcErrorManager getErrorManager()
    {
        return errorManager;
    }

    public void setErrorManager(AcmSpringMvcErrorManager errorManager)
    {
        this.errorManager = errorManager;
    }
}
