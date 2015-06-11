package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/9/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class WorkflowConfigurationReplaceBpmnFile {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(
            value = "/workflowconfiguration/files",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String replaceFile(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) throws IOException, AcmWorkflowConfigurationException
    {

        try {
            if (file.isEmpty()) {
                throw new AcmWorkflowConfigurationException("Uploaded BPMN File is empty");
            }
            InputStream fileInputStream = file.getInputStream();
            workflowConfigurationService.uploadBpmnFile(fileInputStream);
            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't replace BPMN file", e);
            }
            throw new AcmWorkflowConfigurationException("Can't replace BPMN file", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService) {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
