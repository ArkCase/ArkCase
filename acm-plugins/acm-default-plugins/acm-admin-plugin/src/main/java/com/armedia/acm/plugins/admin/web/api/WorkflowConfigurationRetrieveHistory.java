package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.IOException;

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
    public String retrieveHistory(
            @PathVariable("key") String key,
            @PathVariable("version") int  version,
            HttpServletResponse response) throws IOException, AcmWorkflowConfigurationException {

        try {
            List<AcmProcessDefinition> processDefinitions = workflowConfigurationService.retrieveHistory(key, version);

            JSONArray resultArray = new JSONArray();
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DateFormats.WORKFLOW_DATE_FORMAT);

            for (AcmProcessDefinition processIter : processDefinitions) {
                JSONObject processObj = new JSONObject();
                processObj.put(WorkflowConfigurationService.PROP_ID, String.valueOf(processIter.getId()));
                processObj.put(WorkflowConfigurationService.PROP_KEY, processIter.getKey());
                processObj.put(WorkflowConfigurationService.PROP_ACTIVE, processIter.isActive());
                processObj.put(WorkflowConfigurationService.PROP_NAME, (processIter.getName() !=  null ?  processIter.getName() : ""));
                processObj.put(WorkflowConfigurationService.PROP_VERSION, String.valueOf(processIter.getVersion()));
                processObj.put(WorkflowConfigurationService.PROP_DESCRIPTION, (processIter.getDescription() != null ? processIter.getDescription() : ""));
                processObj.put(WorkflowConfigurationService.PROP_CREATED, (processIter.getCreated() != null ? dateFormatter.format(processIter.getCreated()) : ""));
                processObj.put(WorkflowConfigurationService.PROP_CREATOR, (processIter.getCreator() != null ? processIter.getCreator() : ""));
                processObj.put(WorkflowConfigurationService.PROP_MODIFIED, (processIter.getModified() != null ? dateFormatter.format(processIter.getModified()) : ""));
                processObj.put(WorkflowConfigurationService.PROP_MODIFIER, (processIter.getModifier() != null ? processIter.getModifier() : ""));
                resultArray.put(processObj);
            }

            return resultArray.toString();
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
