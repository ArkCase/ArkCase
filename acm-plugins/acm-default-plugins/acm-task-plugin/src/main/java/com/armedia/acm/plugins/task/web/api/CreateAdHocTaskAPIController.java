package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmAdHocTaskCreatedEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private AcmSpringMvcErrorManager errorManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(
            @RequestBody AcmTask in,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws IOException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Creating ad-hoc task.");
        }

        try
        {
            AcmTask adHocTask = getTaskDao().createAdHocTask(in);

            publishAdHocTaskCreatedEvent(authentication, httpSession, adHocTask);

            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            getErrorManager().sendErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), response);
        }

        // Spring MVC has already flushed the response output stream during sendErrorResponse, but Java
        // makes us return something.
        return null;
    }

    protected void publishAdHocTaskCreatedEvent(Authentication authentication, HttpSession httpSession, AcmTask completed)
    {
        AcmAdHocTaskCreatedEvent event = new AcmAdHocTaskCreatedEvent(completed);
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
