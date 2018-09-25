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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;

    @Override
    public void onApplicationEvent(AcmCostsheetEvent event)
    {
        if (event != null && event.isStartWorkflow())
        {
            startWorkflow(event);
        }
    }

    protected void startWorkflow(AcmCostsheetEvent event)
    {
        EcmFile pdfRendition = event.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // CostsheetWorkflowListener is not handling buckslip process
            return;
        }
        LOG.debug("Start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startWorkflow(event, configuration);
        }
    }

    private void startWorkflow(AcmCostsheetEvent event, EcmFileWorkflowConfiguration configuration)
    {
        AcmCostsheet costsheet = (AcmCostsheet) event.getSource();
        String processName = configuration.getProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);

        String taskName = createName(costsheet);

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("reviewers", reviewers);
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

        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

        LOG.debug("process ID: " + pi.getId());
    }

    private List<String> findReviewers(AcmCostsheetEvent event)
    {
        List<String> reviewers = new ArrayList<>();

        for (AcmParticipant participant : ((AcmCostsheet) event.getSource()).getParticipants())
        {
            if ("approver".equals(participant.getParticipantType()))
            {
                reviewers.add(participant.getParticipantLdapId());
            }
        }

        return reviewers;
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

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }
}
