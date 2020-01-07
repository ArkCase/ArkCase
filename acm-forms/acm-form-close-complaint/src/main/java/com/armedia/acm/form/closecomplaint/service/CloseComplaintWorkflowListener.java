package com.armedia.acm.form.closecomplaint.service;

/*-
 * #%L
 * ACM Forms: Close Complaint
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

import com.armedia.acm.form.closecomplaint.model.CloseComplaintFormEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private final Logger log = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private String closeComplaintTaskName;

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
        EcmFile pdfRendition = closeComplaintFormEvent.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(pdfRendition);

        log.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // CloseComplaintWorkflowListener is not handling buckslip process
            return;
        }
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
        pvars.put("pdfRenditionId", closeComplaintFormEvent.getUploadedFiles().getPdfRendition().getFileId());
        Long id = closeComplaintFormEvent.getUploadedFiles().getFormXml() != null
                ? closeComplaintFormEvent.getUploadedFiles().getFormXml().getFileId()
                : null;
        pvars.put("formXmlId", id);

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", closeComplaintFormEvent.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put("OBJECT_NAME", closeComplaintFormEvent.getUploadedFiles().getPdfRendition().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", "COMPLAINT");
        pvars.put("PARENT_OBJECT_ID", closeComplaintFormEvent.getComplaintId());
        pvars.put("COMPLAINT", closeComplaintFormEvent.getComplaintId());
        pvars.put("REQUEST_TYPE", "CLOSE_COMPLAINT_REQUEST");
        pvars.put("REQUEST_ID", closeComplaintFormEvent.getRequest().getId());
        pvars.put("IP_ADDRESS", closeComplaintFormEvent.getIpAddress());

        log.debug("starting process: [{}]", processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        log.debug("process ID: [{}]", pi.getId());
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
