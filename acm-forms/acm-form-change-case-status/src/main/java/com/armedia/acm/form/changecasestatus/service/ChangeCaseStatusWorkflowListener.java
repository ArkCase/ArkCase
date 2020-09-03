/**
 *
 */
package com.armedia.acm.form.changecasestatus.service;

/*-
 * #%L
 * ACM Forms: Change Case Status
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

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusFormEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final Logger LOG = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private String changeCaseStatusTaskName;
    private AcmBpmnService acmBpmnService;

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
        EcmFile pdfRendition = event.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // ChangeCaseStatusWorkflowListener is not handling buckslip process
            return;
        }
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

        // Default one if "changeCaseStatusTaskName" is null or empty
        String taskName = "Task " + event.getCaseNumber();

        // Overwrite "taskName" with "changeCaseStatusTaskName" value
        if (getChangeCaseStatusTaskName() != null && !getChangeCaseStatusTaskName().isEmpty())
        {
            taskName = String.format(getChangeCaseStatusTaskName(), event.getCaseNumber());
        }

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", event.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put("formXmlId", event.getUploadedFiles().getFormXml().getFileId());

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", event.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", event.getUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "CASE_FILE");
        pvars.put("PARENT_OBJECT_ID", event.getCaseId());
        pvars.put("CASE_FILE", event.getCaseId());
        pvars.put("REQUEST_TYPE", "CHANGE_CASE_STATUS");
        pvars.put("REQUEST_ID", event.getRequest().getId());
        pvars.put("IP_ADDRESS", event.getIpAddress());
        pvars.put("PENDING_STATUS", event.getRequest().getStatus());

        LOG.debug("Starting process: [{}]", processName);

        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(processName, pvars);

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

    public String getChangeCaseStatusTaskName()
    {
        return changeCaseStatusTaskName;
    }

    public void setChangeCaseStatusTaskName(String changeCaseStatusTaskName)
    {
        this.changeCaseStatusTaskName = changeCaseStatusTaskName;
    }

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }
}
