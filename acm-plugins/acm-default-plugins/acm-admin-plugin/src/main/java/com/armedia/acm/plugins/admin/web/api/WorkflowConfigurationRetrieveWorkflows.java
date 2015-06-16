package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/9/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationRetrieveWorkflows {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(value = "/workflowconfiguration/workflows", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<AcmProcessDefinition> retrieveWorkflows(
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "orderBy", required = false, defaultValue = WorkflowConfigurationService.PROP_CREATED) String orderBy,
            @RequestParam(value = "isAsc", required = false, defaultValue = "true") boolean isAsc,
            HttpServletResponse response) throws IOException, AcmWorkflowConfigurationException {

        try {
            List<AcmProcessDefinition> processDefinitions = workflowConfigurationService.retrieveWorkflows(start, length, orderBy, isAsc);

            return processDefinitions;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get workflows list", e);
            }
            throw new AcmWorkflowConfigurationException("Can't get workflows list", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService) {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
