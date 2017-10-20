package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class SaveTaskAPIController
{
    private TaskDao taskDao;
    private EcmFileParticipantService fileParticipantService;
    private TaskEventPublisher taskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/save/{taskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(@PathVariable("taskId") Long taskId, @RequestBody AcmTask in, Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Saving task id'" + taskId + "'");
        }

        try
        {
            // make sure task exists - findById will throw an exception if there is no such task
            AcmTask originalTask = getTaskDao().findById(taskId);

            AcmTask retval = getTaskDao().save(in);
            try
            {
                getFileParticipantService().inheritParticipantsFromAssignedObject(in.getParticipants(), originalTask.getParticipants(),
                        in.getContainer().getFolder());
                getFileParticipantService().inheritParticipantsFromAssignedObject(in.getParticipants(), originalTask.getParticipants(),
                        in.getContainer().getAttachmentFolder());
            }
            catch (AcmAccessControlException e)
            {
                throw new AcmUserActionFailedException("save", retval.getObjectType(), retval.getId(),
                        "Failed to set participants on child folders!", e);
            }

            publishTaskSavedEvent(authentication, httpSession, retval, true);

            return retval;
        }
        catch (AcmTaskException e)
        {
            publishTaskSavedEvent(authentication, httpSession, in, false);
            throw new AcmUserActionFailedException("save", "Task", taskId, e.getMessage(), e);
        }
    }

    protected void publishTaskSavedEvent(Authentication authentication, HttpSession httpSession, AcmTask saved, boolean succeeded)
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

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

}
