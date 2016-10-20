package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({"/api/v1/plugin/task", "/api/latest/plugin/task"})
public class FindTaskByIdAPIController
{
    private AcmTaskService taskService;

    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/byId/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask findTaskById(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException
    {
        log.info("Finding task with id:'{}'", taskId);

        AcmTask task = getTaskService().retrieveTask(taskId);
        if (task != null)
        {
            raiseEvent(authentication, session, task, true);
            return task;
        }

        log.error("Could not find task with id:'{}' {}");
        raiseFakeEvent(taskId, authentication, session);
        throw new AcmObjectNotFoundException("task", taskId, null);
    }

    private void raiseFakeEvent(Long taskId, Authentication authentication, HttpSession session)
    {
        // gen up a fake task so we can audit the failure
        AcmTask fakeTask = new AcmTask();
        fakeTask.setTaskId(taskId);
        raiseEvent(authentication, session, fakeTask, false);
    }

    protected void raiseEvent(Authentication authentication, HttpSession session, AcmTask task, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "findById", authentication.getName(),
                succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public AcmTaskService getTaskService()
    {
        return taskService;
    }

    public void setTaskService(AcmTaskService taskService)
    {
        this.taskService = taskService;
    }
}
