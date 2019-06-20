/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetWorkflowListener implements ApplicationListener<AcmTimesheetEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;

    @Override
    public void onApplicationEvent(AcmTimesheetEvent event)
    {
        if (event != null && event.isStartWorkflow())
        {
            startWorkflow(event);
        }
    }

    protected void startWorkflow(AcmTimesheetEvent event)
    {
        EcmFile pdfRendition = event.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // TimesheetWorkflowListener is not handling buckslip process
            return;
        }
        LOG.debug("Start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startWorkflow(event, configuration);
        }
    }

    private void startWorkflow(AcmTimesheetEvent event, EcmFileWorkflowConfiguration configuration)
    {
        AcmTimesheet timesheet = (AcmTimesheet) event.getSource();
        String processName = configuration.getProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);

        String taskName = createName(timesheet);

        Map<String, Object> pvars = new HashMap<>();
        List<String> candidateGroups =  findCandidateGroups(event);

        pvars.put("reviewers", reviewers);
        pvars.put("candidateGroups", candidateGroups);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", event.getUploadedFiles().getPdfRendition().getFileId());

        Long formXmlId = event.getUploadedFiles().getFormXml() != null
                ? event.getUploadedFiles().getFormXml().getFileId()
                : null;
        pvars.put("formXmlId", formXmlId);

        pvars.put("OBJECT_TYPE", TimesheetConstants.OBJECT_TYPE);
        pvars.put("OBJECT_ID", timesheet.getId());
        pvars.put("OBJECT_NAME", createName(timesheet));

        LOG.debug("Starting process: " + processName);

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        LOG.debug("process ID: " + pi.getId());
    }

    private List<String> findReviewers(AcmTimesheetEvent event)
    {
        List<String> reviewers = new ArrayList<>();

        for (AcmParticipant participant : ((AcmTimesheet) event.getSource()).getParticipants())
        {
            if (ParticipantTypes.APPROVER.equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }

        return reviewers;
    }

    private List<String> findCandidateGroups(AcmTimesheetEvent event)
    {
        List<String> candidateGroups = new ArrayList<>();

        for (AcmParticipant participant : ((AcmTimesheet) event.getSource()).getParticipants())
        {
            if (ParticipantTypes.OWNING_GROUP.equals(participant.getParticipantType()))
            {
                candidateGroups.add(participant.getParticipantLdapId());
            }
        }

        return candidateGroups;
    }

    public String createName(AcmTimesheet timesheet)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TIMESHEET_DATE_FORMAT);

        String objectType = StringUtils.capitalize(TimesheetConstants.OBJECT_TYPE.toLowerCase());
        String startDate = formatter.format(timesheet.getStartDate());
        String endDate = formatter.format(timesheet.getEndDate());

        return objectType + " " + startDate + "-" + endDate;
    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(
            FileWorkflowBusinessRule fileWorkflowBusinessRule)
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
