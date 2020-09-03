/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;
import com.armedia.acm.services.costsheet.model.CostsheetConfig;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class CostsheetWorkflowListener implements ApplicationListener<AcmCostsheetEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private AcmBpmnService acmBpmnService;
    private CostsheetConfig costsheetConfig;

    @Override
    public void onApplicationEvent(AcmCostsheetEvent event)
    {
        if (event != null && event.isStartWorkflow() && costsheetConfig.getUseApprovalWorkflow())
        {
            startWorkflow(event);
        }
    }

    protected void startWorkflow(AcmCostsheetEvent event)
    {
        AcmCostsheet costsheet = (AcmCostsheet) event.getSource();
        String processName = costsheetConfig.getWorkflowProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);
        List<String> candidateGroups =  findCandidateGroups(event);

        String taskName = createName(costsheet);

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
        pvars.put("candidateGroups", candidateGroups);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", author);
        pvars.put("pdfRenditionId", event.getUploadedFiles().getPdfRendition().getFileId());

        Long formXmlId = event.getUploadedFiles().getFormXml() != null
                ? event.getUploadedFiles().getFormXml().getFileId()
                : null;
        pvars.put("formXmlId", formXmlId);

        pvars.put("OBJECT_TYPE", CostsheetConstants.OBJECT_TYPE);
        pvars.put("OBJECT_ID", costsheet.getId());
        pvars.put("OBJECT_NAME", createName(costsheet));

        LOG.debug("Starting process: " + processName);

        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(processName, pvars);

        LOG.debug("process ID: " + pi.getId());
    }

    private List<String> findReviewers(AcmCostsheetEvent event)
    {
        List<String> reviewers = new ArrayList<>();

        for (AcmParticipant participant : ((AcmCostsheet) event.getSource()).getParticipants())
        {
            if (ParticipantTypes.APPROVER.equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }

        return reviewers;
    }

    private List<String> findCandidateGroups(AcmCostsheetEvent event)
    {
        List<String> candidateGroups = new ArrayList<>();

        for (AcmParticipant participant : ((AcmCostsheet) event.getSource()).getParticipants())
        {
            if (ParticipantTypes.OWNING_GROUP.equals(participant.getParticipantType()))
            {
                candidateGroups.add(participant.getParticipantLdapId());
            }
        }

        return candidateGroups;
    }

    public String createName(AcmCostsheet costsheet)
    {
        String objectType = StringUtils.capitalise(CostsheetConstants.OBJECT_TYPE.toLowerCase());
        String objectNumber = costsheet.getParentNumber();

        return objectType + " " + objectNumber;
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

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }

    public CostsheetConfig getCostsheetConfig()
    {
        return costsheetConfig;
    }

    public void setCostsheetConfig(CostsheetConfig costsheetConfig)
    {
        this.costsheetConfig = costsheetConfig;
    }
}
