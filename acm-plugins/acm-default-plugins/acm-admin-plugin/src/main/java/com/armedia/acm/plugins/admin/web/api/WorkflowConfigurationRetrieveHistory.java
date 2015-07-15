package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by sergey.kolomiets  on 6/9/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationRetrieveHistory {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(value = "/workflowconfiguration/workflows/{key}/versions/{version}/history", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<AcmProcessDefinition> retrieveHistory(
            @PathVariable("key") String key,
            @PathVariable("version") int  version) throws IOException, AcmWorkflowConfigurationException {

        try {
            List<AcmProcessDefinition> processDefinitions = workflowConfigurationService.retrieveHistory(key, version);

            return processDefinitions;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get workflows history", e);
            }
            throw new AcmWorkflowConfigurationException("Can't get workflows history", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService) {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
