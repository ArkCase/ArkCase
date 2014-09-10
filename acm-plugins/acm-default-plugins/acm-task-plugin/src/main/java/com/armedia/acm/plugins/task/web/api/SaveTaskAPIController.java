package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class SaveTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/save/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(
            @PathVariable("taskId") Long taskId,
            @RequestBody AcmTask in,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Saving task id'" + taskId + "'");
        }

        try
        {
            // make sure task exists - findById will throw an exception if there is no such task
            getTaskDao().findById(taskId);

            AcmTask retval = getTaskDao().save(in);

            publishTaskSavedEvent(authentication, httpSession, retval, true);

            return retval;
        }
        catch (AcmTaskException e)
        {
            publishTaskSavedEvent(authentication, httpSession, in, false);
            throw new AcmUserActionFailedException("Save", "Task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskSavedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask saved,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(saved, "save", authentication.getName(), succeeded, ipAddress);
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
