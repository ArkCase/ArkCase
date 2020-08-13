/**
 * 
 */
package com.armedia.acm.forms.roi.service;

/*-
 * #%L
 * ACM Forms: Report of Investigation
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
import com.armedia.acm.form.config.Item;
import com.armedia.acm.forms.roi.model.ReportOfInvestigationFormEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.plugins.task.model.TaskConstants;

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
 *
 */
public class ReportOfInvestigationWorkflowListener implements ApplicationListener<ReportOfInvestigationFormEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private AcmBpmnService acmBpmnService;

    @Override
    public void onApplicationEvent(ReportOfInvestigationFormEvent event)
    {
        handleReportOfInvestigation(event);

    }

    protected void handleReportOfInvestigation(ReportOfInvestigationFormEvent event)
    {
        EcmFile pdfRendition = event.getUploadedFiles().getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(pdfRendition);

        LOG.debug("Calling business rules");

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // ReportOfInvestigationWorkflowListener is not handling buckslip process
            return;
        }
        LOG.debug("Start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(event, configuration);
        }
    }

    private void startBusinessProcess(ReportOfInvestigationFormEvent event, EcmFileWorkflowConfiguration configuration)
    {
        String processName = configuration.getProcessName();

        String author = event.getUserId();
        List<String> reviewers = findReviewers(event);

        String taskName = "ROI for " + event.getForObjectType() + " '" + event.getForObjectNumber() + "'";

        Map<String, Object> pvars = new HashMap<>();

        pvars.put(TaskConstants.VARIABLE_NAME_REVIEWERS, reviewers);
        pvars.put(TaskConstants.VARIABLE_NAME_TASK_NAME, taskName);
        pvars.put(TaskConstants.VARIABLE_NAME_DOC_AUTHOR, author);
        pvars.put(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID, event.getUploadedFiles().getPdfRendition().getFileId());
        pvars.put(TaskConstants.VARIABLE_NAME_XML_RENDITION_ID, event.getUploadedFiles().getFormXml().getFileId());

        pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, EcmFileConstants.OBJECT_FILE_TYPE);
        pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, event.getUploadedFiles().getPdfRendition().getId());
        pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_NAME, event.getUploadedFiles().getPdfRendition().getFileName());
        pvars.put(EcmFileConstants.OBJECT_FILE_TYPE, event.getUploadedFiles().getPdfRendition().getId());
        pvars.put(TaskConstants.VARIABLE_NAME_REQUEST_TYPE, "ROI");
        pvars.put(TaskConstants.VARIABLE_NAME_REQUEST_ID, event.getObjectId());

        pvars.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, event.getParentObjectType());
        pvars.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, event.getParentObjectId());

        LOG.debug("Starting process: " + processName);

        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(processName, pvars);

        LOG.debug("Process ID: " + pi.getId());
    }

    private List<String> findReviewers(ReportOfInvestigationFormEvent event)
    {
        List<String> approvers = new ArrayList<>();

        for (Item approver : event.getRequest().getApprovers())
        {
            approvers.add(approver.getValue());
        }

        return approvers;
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

}
