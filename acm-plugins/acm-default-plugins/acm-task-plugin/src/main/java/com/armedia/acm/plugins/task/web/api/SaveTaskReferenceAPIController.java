package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.Reference;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping({"/api/v1/plugin/task", "/api/latest/plugin/task"})
public class SaveTaskReferenceAPIController
{
    private AcmTaskService taskService;

    @RequestMapping(value = "/saveReference", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ObjectAssociation createReferenceForTask(
            @RequestBody Reference reference, Authentication authentication) throws AcmCreateObjectFailedException
    {
        return getTaskService().saveReferenceToTask(reference, authentication);
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
