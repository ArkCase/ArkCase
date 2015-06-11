package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/9/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationMakeActive {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(value = "/workflowconfiguration/workflows/{key}/versions/{version}/active", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String makeActive(
            @PathVariable("key") String key,
            @PathVariable("version") int  version,
            HttpServletResponse response) throws IOException, AcmWorkflowConfigurationException {

        try {
            workflowConfigurationService.makeActive(key, version);
            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't make workflow active", e);
            }
            throw new AcmWorkflowConfigurationException("Can't make workflow active", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService) {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
