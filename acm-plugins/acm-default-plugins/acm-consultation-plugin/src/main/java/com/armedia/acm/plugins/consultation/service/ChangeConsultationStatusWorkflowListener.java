package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeConsultationStatusWorkflowListener implements ApplicationListener<ChangeConsultationStatusEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private String changeConsultationStatusTaskName;

    @Override
    public void onApplicationEvent(ChangeConsultationStatusEvent event)
    {
        if (!"edit".equals(event.getMode()))
        {
            handleNewCloseConsultationRequest(event);
        }
    }

    protected void handleNewCloseConsultationRequest(ChangeConsultationStatusEvent event)
    {
        EcmFile pdfRendition = event.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // ChangeConsultationStatusWorkflowListener is not handling buckslip process
            return;
        }
        LOG.debug("Start process? [{}]", configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(event, configuration);
        }
    }

    private void startBusinessProcess(ChangeConsultationStatusEvent event, EcmFileWorkflowConfiguration configuration)
    {
        String processName = configuration.getProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);
        List<String> candidateGroups =  findCandidateGroups(event);

        // Default one if "changeConsultationStatusTaskName" is null or empty
        String taskName = "Task " + event.getConsultationNumber();

        // Overwrite "taskName" with "changeConsultationStatusTaskName" value
        if (getChangeConsultationStatusTaskName() != null && !getChangeConsultationStatusTaskName().isEmpty())
        {
            taskName = String.format(getChangeConsultationStatusTaskName(), event.getConsultationNumber());
        }

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("candidateGroups", candidateGroups);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", event.getUploadedFiles().getPdfRendition().getFileId());
        Long formXmlId = null;
        if (event.getUploadedFiles().getFormXml() != null)
        {
            pvars.put("formXmlId", event.getUploadedFiles().getFormXml().getFileId());
        }
        pvars.put("formXmlId", formXmlId);
        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", event.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", event.getUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "CONSULTATION");
        pvars.put("PARENT_OBJECT_ID", event.getConsultationId());
        pvars.put("CONSULTATION", event.getConsultationId());
        pvars.put("REQUEST_TYPE", "CHANGE_CONSULTATION_STATUS");
        pvars.put("REQUEST_ID", event.getRequest().getId());
        pvars.put("IP_ADDRESS", event.getIpAddress());
        pvars.put("PENDING_STATUS", event.getRequest().getStatus());

        LOG.debug("Starting process: [{}]", processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        LOG.debug("Process ID: [{}]", pi.getId());
    }

    private List<String> findReviewers(ChangeConsultationStatusEvent event)
    {
        List<String> reviewers = new ArrayList<>();

        for (AcmParticipant participant : event.getRequest().getParticipants())
        {
            if (ParticipantTypes.APPROVER.equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }

        return reviewers;
    }

    private List<String> findCandidateGroups(ChangeConsultationStatusEvent event)
    {
        List<String> candidateGroups = new ArrayList<>();

        for (AcmParticipant participant : event.getRequest().getParticipants())
        {
            if (ParticipantTypes.OWNING_GROUP.equals(participant.getParticipantType()))
            {
                candidateGroups.add(participant.getParticipantLdapId());
            }
        }

        return candidateGroups;
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

    public String getChangeConsultationStatusTaskName() {
        return changeConsultationStatusTaskName;
    }

    public void setChangeConsultationStatusTaskName(String changeConsultationStatusTaskName) {
        this.changeConsultationStatusTaskName = changeConsultationStatusTaskName;
    }
}
