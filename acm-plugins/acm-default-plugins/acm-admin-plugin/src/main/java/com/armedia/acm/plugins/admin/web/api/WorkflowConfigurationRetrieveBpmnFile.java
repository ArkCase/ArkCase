package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/9/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationRetrieveBpmnFile {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(value = "/workflowconfiguration/workflows/{key}/versions/{version}/file", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveFile(
            @PathVariable("key") String key,
            @PathVariable("version") int  version,
            HttpServletResponse response) throws IOException, AcmWorkflowConfigurationException {

        try {
            InputStream bpmnStream = workflowConfigurationService.retrieveBpmnFile(key, version);
            String bpmnFileContent = IOUtils.toString(bpmnStream);
            return bpmnFileContent;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get workflow BPMN file", e);
            }
            throw new AcmWorkflowConfigurationException("Can't get workflow BPMN file", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService) {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
