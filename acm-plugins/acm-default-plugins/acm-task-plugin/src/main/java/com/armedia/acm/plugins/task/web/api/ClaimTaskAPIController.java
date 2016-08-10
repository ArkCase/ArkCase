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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by manoj.dhungana on 2/7/2016.
 */

@Controller
@RequestMapping({"/api/v1/plugin/task", "/api/latest/plugin/task"})
public class ClaimTaskAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    @RequestMapping(value = "/claim/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask claimTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        log.info("User [{}] is claiming workflow task with ID [{}]", authentication.getName(), taskId);
        try
        {
            getTaskDao().claimTask(taskId, authentication.getName());
            AcmTask claimedTask = getTaskDao().findById(taskId);
            publishTaskClaimEvent(authentication, httpSession, claimedTask, "claim", true);
            return claimedTask;
        } catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskClaimEvent(authentication, httpSession, fakeTask, "claim", false);
            throw new AcmUserActionFailedException("claim", "task", taskId, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/unclaim/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask unclaimTask(
            @PathVariable("taskId") Long taskId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        log.info("User [{}] is unclaiming workflow task with ID [{}]", authentication.getName(), taskId);
        try
        {
            getTaskDao().unclaimTask(taskId);
            AcmTask unclaimedTask = getTaskDao().findById(taskId);
            publishTaskClaimEvent(authentication, httpSession, unclaimedTask, "unclaim", true);
            return unclaimedTask;
        } catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(taskId);
            publishTaskClaimEvent(authentication, httpSession, fakeTask, "unclaim", false);
            throw new AcmUserActionFailedException("unclaim", "task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskClaimEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask acmTask,
            String taskEvent,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(acmTask, taskEvent, authentication.getName(), succeeded, ipAddress);
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
