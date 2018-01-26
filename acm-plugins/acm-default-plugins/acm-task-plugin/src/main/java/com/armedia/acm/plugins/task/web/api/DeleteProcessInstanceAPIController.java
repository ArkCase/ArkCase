package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by manoj.dhungana on 2/7/2016.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class DeleteProcessInstanceAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;

    @RequestMapping(value = "/deleteProcessInstance/{parentObjectId}/{processInstanceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteProcessInstance(
            @PathVariable("parentObjectId") String parentObjectId,
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody String deleteReason,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        log.info("User [{}] is deleting workflow process with ID [{}]", authentication.getName(), processInstanceId);
        try
        {
            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            getTaskDao().deleteProcessInstance(parentObjectId, processInstanceId, deleteReason, authentication, ipAddress);

        }
        catch (AcmTaskException e)
        {
            log.debug("failed deleting process instance with ID [{}]", processInstanceId);
            throw new AcmUserActionFailedException("delete", "process instance", Long.valueOf(processInstanceId), e.getMessage(), e);
        }
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }
}
