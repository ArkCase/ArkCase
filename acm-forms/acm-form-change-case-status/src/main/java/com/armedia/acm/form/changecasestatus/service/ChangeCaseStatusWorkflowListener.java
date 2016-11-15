/**
 *
 */
package com.armedia.acm.form.changecasestatus.service;

import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusFormEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class ChangeCaseStatusWorkflowListener implements ApplicationListener<ChangeCaseStatusFormEvent>
{

    private FileWorkflowBusinessRule fileWorkflowBusinessRule;

    private RuntimeService activitiRuntimeService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ChangeCaseStatusFormEvent event)
    {
        if (!"edit".equals(event.getMode()))
        {
            handleNewCloseCaseRequest(event);
        }
    }

    protected void handleNewCloseCaseRequest(ChangeCaseStatusFormEvent event)
    {
        EcmFile pdfRendition = event.getFrevvoUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);

        LOG.debug("Start process? [{}]", configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(event, configuration);
        }
    }

    private void startBusinessProcess(ChangeCaseStatusFormEvent event, EcmFileWorkflowConfiguration configuration)
    {
        String processName = configuration.getProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);

        String taskName = "Request to Change Case Status '" + event.getCaseNumber() + "'";

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", event.getFrevvoUploadedFiles().getPdfRendition().getFileId());
        pvars.put("formXmlId", event.getFrevvoUploadedFiles().getFormXml().getFileId());

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", event.getFrevvoUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", event.getFrevvoUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "CASE_FILE");
        pvars.put("PARENT_OBJECT_ID", event.getCaseId());
        pvars.put("CASE_FILE", event.getCaseId());
        pvars.put("REQUEST_TYPE", "CHANGE_CASE_STATUS");
        pvars.put("REQUEST_ID", event.getRequest().getId());
        pvars.put("IP_ADDRESS", event.getIpAddress());
        pvars.put("PENDING_STATUS", event.getRequest().getStatus());

        LOG.debug("Starting process: [{}]", processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        LOG.debug("Process ID: [{}]", pi.getId());
    }

    private List<String> findReviewers(ChangeCaseStatusFormEvent event)
    {
        List<String> reviewers = new ArrayList<>();

        for (AcmParticipant participant : event.getRequest().getParticipants())
        {
            if ("approver".equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }

        return reviewers;
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
