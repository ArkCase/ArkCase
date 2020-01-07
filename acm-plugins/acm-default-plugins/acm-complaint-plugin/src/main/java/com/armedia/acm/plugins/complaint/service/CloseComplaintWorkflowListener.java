package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintEvent;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloseComplaintWorkflowListener implements ApplicationListener<CloseComplaintEvent>
{
    private final Logger log = LogManager.getLogger(getClass());
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
        List<String> candidateGroups =  findCandidateGroups(closeComplaintEvent);

        // Default one if "closeComplaintTaskName" is null or empty
        String taskName = "Task " + closeComplaintEvent.getComplaintNumber();

        // Overwrite "taskName" with "closeComplaintTaskName" has value
        if (getCloseComplaintTaskName() != null && !getCloseComplaintTaskName().isEmpty())
        {
            taskName = String.format(getCloseComplaintTaskName(), closeComplaintEvent.getComplaintNumber());
        }

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("candidateGroups", candidateGroups);
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
            if (ParticipantTypes.APPROVER.equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }
        return reviewers;
    }

    private List<String> findCandidateGroups(CloseComplaintEvent closeComplaintEvent)
    {
        List<String> candidateGroups = new ArrayList<>();

        for (AcmParticipant participant : closeComplaintEvent.getRequest().getParticipants())
        {
            if (ParticipantTypes.OWNING_GROUP.equals(participant.getParticipantType()))
            {
                candidateGroups.add(participant.getParticipantLdapId());
            }
        }

        return candidateGroups;
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
