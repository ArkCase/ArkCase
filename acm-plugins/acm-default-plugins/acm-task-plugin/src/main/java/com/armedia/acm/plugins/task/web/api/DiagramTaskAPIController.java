package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by riste.tutureski on 5/12/2017.
 */
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class DiagramTaskAPIController
{
    private AcmTaskService acmTaskService;

    @RequestMapping(value = "/diagram/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public byte[] diagram(@PathVariable("taskId") Long taskId) throws AcmTaskException
    {
        return getAcmTaskService().getDiagram(taskId);
    }

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }
}
