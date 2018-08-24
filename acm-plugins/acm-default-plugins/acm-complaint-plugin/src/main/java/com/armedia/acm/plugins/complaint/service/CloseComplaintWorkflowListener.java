package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintEvent;
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

public class CloseComplaintWorkflowListener implements ApplicationListener<CloseComplaintEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private String closeComplaintTaskName;
    private PDFCloseComplaintDocumentGenerator pdfCloseComplaintDocumentGenerator;

    @Override
    public void onApplicationEvent(CloseComplaintEvent closeComplaintEvent)
    {
        if (!"edit".equals(closeComplaintEvent.getMode()))
        {
            handleNewCloseComplaintRequest(closeComplaintEvent);
        }
    }

    private void handleNewCloseComplaintRequest(CloseComplaintEvent closeComplaintEvent)
    {
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(closeComplaintEvent.getUploadedFiles().getPdfRendition());
        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            return;
        }
        log.debug("start process? [{}]", configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(closeComplaintEvent, configuration);
        }
    }

    private void startBusinessProcess(CloseComplaintEvent closeComplaintEvent, EcmFileWorkflowConfiguration configuration)
    {

        String processName = configuration.getProcessName();

        String author = closeComplaintEvent.getUserId();
        List<String> reviewers = findReviewers(closeComplaintEvent);

        // Default one if "closeComplaintTaskName" is null or empty
        String taskName = "Task " + closeComplaintEvent.getComplaintNumber();

        // Overwrite "taskName" with "closeComplaintTaskName" has value
        if (getCloseComplaintTaskName() != null && !getCloseComplaintTaskName().isEmpty())
        {
            taskName = String.format(getCloseComplaintTaskName(), closeComplaintEvent.getComplaintNumber());
        }

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", closeComplaintEvent.getUploadedFiles().getPdfRendition().getFileId());
        Long formXmlId = null;
        if (closeComplaintEvent.getUploadedFiles().getFormXml() != null)
        {
            formXmlId = closeComplaintEvent.getUploadedFiles().getFormXml().getFileId();
        }
        pvars.put("formXmlId", formXmlId);

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", closeComplaintEvent.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", closeComplaintEvent.getUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "COMPLAINT");
        pvars.put("PARENT_OBJECT_ID", closeComplaintEvent.getComplaintId());
        pvars.put("COMPLAINT", closeComplaintEvent.getComplaintId());
        pvars.put("REQUEST_TYPE", "CLOSE_COMPLAINT_REQUEST");
        pvars.put("REQUEST_ID", closeComplaintEvent.getRequest().getId());
        pvars.put("IP_ADDRESS", closeComplaintEvent.getIpAddress());

        log.debug("starting process: [{}]", processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        log.debug("process ID: [{}]", pi.getId());
    }

    private List<String> findReviewers(CloseComplaintEvent closeComplaintEvent)
    {
        List<String> reviewers = new ArrayList<>();
        for (AcmParticipant participant : closeComplaintEvent.getRequest().getParticipants())
        {
            if ("approver".equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }
        return reviewers;
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

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }

    public PDFCloseComplaintDocumentGenerator getPdfCloseComplaintDocumentGenerator()
    {
        return pdfCloseComplaintDocumentGenerator;
    }

    public void setPdfCloseComplaintDocumentGenerator(PDFCloseComplaintDocumentGenerator pdfCloseComplaintDocumentGenerator)
    {
        this.pdfCloseComplaintDocumentGenerator = pdfCloseComplaintDocumentGenerator;
    }
}
