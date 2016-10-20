package com.armedia.acm.form.closecomplaint.service;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintFormEvent;
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
 * Created by armdev on 11/5/14.
 */
public class CloseComplaintWorkflowListener implements ApplicationListener<CloseComplaintFormEvent>
{
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;

    private RuntimeService activitiRuntimeService;

    private String closeComplaintTaskName;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(CloseComplaintFormEvent closeComplaintFormEvent)
    {
        if (!"edit".equals(closeComplaintFormEvent.getMode()))
        {
            handleNewCloseComplaintRequest(closeComplaintFormEvent);
        }
    }

    protected void handleNewCloseComplaintRequest(CloseComplaintFormEvent closeComplaintFormEvent)
    {
        EcmFile pdfRendition = closeComplaintFormEvent.getFrevvoUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(pdfRendition);

        log.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);

        log.debug("start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(closeComplaintFormEvent, configuration);
        }
    }

    private void startBusinessProcess(CloseComplaintFormEvent closeComplaintFormEvent, EcmFileWorkflowConfiguration configuration)
    {
        String processName = configuration.getProcessName();

        String author = closeComplaintFormEvent.getUserId();
        List<String> reviewers = findReviewers(closeComplaintFormEvent);

        // Default one if "closeComplaintTaskName" is null or empty
        String taskName = "Task " + closeComplaintFormEvent.getComplaintNumber();

        // Overwrite "taskName" with "closeComplaintTaskName" has value
        if (getCloseComplaintTaskName() != null && !getCloseComplaintTaskName().isEmpty())
        {
            taskName = String.format(getCloseComplaintTaskName(), closeComplaintFormEvent.getComplaintNumber());
        }

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", closeComplaintFormEvent.getFrevvoUploadedFiles().getPdfRendition().getFileId());
        pvars.put("formXmlId", closeComplaintFormEvent.getFrevvoUploadedFiles().getFormXml().getFileId());

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", closeComplaintFormEvent.getFrevvoUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", closeComplaintFormEvent.getFrevvoUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "COMPLAINT");
        pvars.put("PARENT_OBJECT_ID", closeComplaintFormEvent.getComplaintId());
        pvars.put("COMPLAINT", closeComplaintFormEvent.getComplaintId());
        pvars.put("REQUEST_TYPE", "CLOSE_COMPLAINT_REQUEST");
        pvars.put("REQUEST_ID", closeComplaintFormEvent.getRequest().getId());
        pvars.put("IP_ADDRESS", closeComplaintFormEvent.getIpAddress());

        log.debug("starting process: " + processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        log.debug("process ID: " + pi.getId());
    }

    private List<String> findReviewers(CloseComplaintFormEvent closeComplaintFormEvent)
    {
        List<String> reviewers = new ArrayList<>();
        for (AcmParticipant participant : closeComplaintFormEvent.getRequest().getParticipants())
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

    public String getCloseComplaintTaskName()
    {
        return closeComplaintTaskName;
    }

    public void setCloseComplaintTaskName(String closeComplaintTaskName)
    {
        this.closeComplaintTaskName = closeComplaintTaskName;
    }
}
