package com.armedia.acm.plugins.consultation.listener;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ChangeConsultationStatusWorkflowListener implements ApplicationListener<ChangeConsultationStatusEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private AcmBpmnService acmBpmnService;
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
        List<String> candidateGroups = findCandidateGroups(event);

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

        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(processName, pvars);

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

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }

    public String getChangeConsultationStatusTaskName()
    {
        return changeConsultationStatusTaskName;
    }

    public void setChangeConsultationStatusTaskName(String changeConsultationStatusTaskName)
    {
        this.changeConsultationStatusTaskName = changeConsultationStatusTaskName;
    }
}
