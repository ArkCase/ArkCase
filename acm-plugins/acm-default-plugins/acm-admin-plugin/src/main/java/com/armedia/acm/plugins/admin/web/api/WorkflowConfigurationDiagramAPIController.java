package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.core.model.DiagramResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by riste.tutureski on 5/16/2017.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationDiagramAPIController
{
    private AcmBpmnService acmBpmnService;

    @RequestMapping(value = "/workflowconfiguration/diagram/{deploymentId}/{key}/{version}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public DiagramResponse getDiagram(@PathVariable ("deploymentId") String deploymentId, @PathVariable ("key") String key, @PathVariable ("version") Integer version) throws AcmBpmnException
    {
        byte[] data = getAcmBpmnService().getDiagram(deploymentId, key, version);
        DiagramResponse response = new DiagramResponse(data);

        return response;
    }

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }
}
