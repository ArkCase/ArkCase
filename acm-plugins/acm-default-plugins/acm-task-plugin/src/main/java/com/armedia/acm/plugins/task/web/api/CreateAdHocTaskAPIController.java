package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(
            @RequestBody AcmTask in,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Creating ad-hoc task.");
        }

        try
        {
            AcmTask adHocTask = getTaskDao().createAdHocTask(in);

            publishAdHocTaskCreatedEvent(authentication, httpSession, adHocTask, true);

            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null);  // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    protected void publishAdHocTaskCreatedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask created,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
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
