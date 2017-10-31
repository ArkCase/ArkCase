package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;

import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * Created by vladimir.radeski on 10/24/2017.
 */

@RequestMapping({"/api/v1/plugin/task", "/api/latest/plugin/task", "/api/v1/plugin/tasks", "/api/latest/plugin/tasks"})
public class CreateBusinessProcessTasksAPIController {
    private TaskEventPublisher taskEventPublisher;
    private AcmTaskService taskService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/reviewDocuments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> reviewDocuments(@RequestBody AcmTask in, @RequestParam(value = "businessProcessName", defaultValue = "acmDocumentWorkflow") String businessProcessName, Authentication authentication, HttpSession httpSession)
            throws AcmAppErrorJsonMsg, AcmCreateObjectFailedException {

        try {
            return getTaskService().startReviewDocumentsWorkflow(in, businessProcessName, authentication);
        } catch (AcmTaskException e) {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null); // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    protected void publishAdHocTaskCreatedEvent(Authentication authentication, HttpSession httpSession, AcmTask created, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
        if (created.getStatus() != null && created.getStatus().equalsIgnoreCase(TaskConstants.STATE_CLOSED))
        {
            event = new AcmApplicationTaskEvent(created, "complete", authentication.getName(), succeeded, ipAddress);
            getTaskEventPublisher().publishTaskEvent(event);
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

    public AcmTaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(AcmTaskService taskService) {
        this.taskService = taskService;
    }

}
