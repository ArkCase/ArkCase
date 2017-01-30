package com.armedia.acm.plugins.ecm.handler;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dmiller on 12/5/2016.
 */
public class FileCreatedWorkflowHandler implements ApplicationListener<EcmFileAddedEvent>
{
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;

    private RuntimeService activitiRuntimeService;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());


    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        EcmFile source = event.getSource();

        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(source);

        LOG.debug("Calling business rules for new file id {}, type {}", source.getId(), source.getFileType());

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);

        LOG.debug("start process for file id {}, type {}? {}", source.getId(), source.getFileType(), configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(event, configuration);
        }
    }

    private void startBusinessProcess(EcmFileAddedEvent event, EcmFileWorkflowConfiguration configuration)
    {
        String processName = configuration.getProcessName();

        Map<String, Object> pvars = new HashMap<>();

        String approversCsv = configuration.getApprovers();
        List<String> approvers = approversCsv == null ?
                new ArrayList<>() :
                Arrays.stream(approversCsv.split(",")).filter(s -> s != null).map(s -> s.trim()).collect(Collectors.toList());
        // the process should work with either "approvers" or "futureApprovers"
        pvars.put("approvers", approvers);
        pvars.put("taskName", configuration.getTaskName());
        pvars.put("documentAuthor", event.getUserId());
        pvars.put("pdfRenditionId", event.getSource().getFileId());

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", event.getSource().getFileId());
        pvars.put("OBJECT_NAME", event.getSource().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", event.getParentObjectType());
        pvars.put("PARENT_OBJECT_ID", event.getParentObjectId());
        pvars.put(event.getParentObjectType(), event.getParentObjectId());
        pvars.put("REQUEST_TYPE", configuration.getRequestType());
        pvars.put("IP_ADDRESS", event.getIpAddress());

        pvars.put("taskDueDateExpression", configuration.getTaskDueDateExpression());
        pvars.put("taskPriority", configuration.getTaskPriority());

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        LOG.debug("Started business process with id {} for file of type {}, id {}", pi.getId(), event.getSource().getFileType(), event.getEcmFileId());

    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }
}
