package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.activiti.engine.ActivitiException;
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
public class FindTaskByIdAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/byId/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask findTaskById(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response
    ) throws AcmObjectNotFoundException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding task with ID '" + taskId + "'");
        }

        try
        {
            AcmTask retval = getTaskDao().findById(taskId);
            raiseEvent(authentication, session, retval, true);
            return retval;
        } catch (AcmTaskException | ActivitiException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            raiseEvent(authentication, session, fakeTask, false);

            log.error("Could not find task with id '" + taskId + "': " + e.getMessage(), e);
            throw new AcmObjectNotFoundException("task", taskId, e.getMessage(), e);
        }
    }

    protected void raiseEvent(Authentication authentication, HttpSession session, AcmTask task, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "findById", authentication.getName(),
                succeeded, ipAddress);
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
