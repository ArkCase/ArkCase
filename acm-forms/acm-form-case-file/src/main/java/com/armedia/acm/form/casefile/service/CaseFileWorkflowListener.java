package com.armedia.acm.form.casefile.service;

/*-
 * #%L
 * ACM Forms: Case File
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
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by armdev on 12/5/14.
 */
public class CaseFileWorkflowListener
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmBpmnService acmBpmnService;

    public void handleNewCaseFile(CaseFile caseFile, UploadedFiles files, RuntimeService activitiRuntimeService,
            FileWorkflowBusinessRule fileWorkflowBusinessRule,
            FrevvoFormAbstractService formService)
    {
        EcmFile pdfRendition = files.getPdfRendition();

        if (pdfRendition == null)
        {
            return;
        }

        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(pdfRendition);

        log.debug("Calling business rules for new case files");

        configuration = fileWorkflowBusinessRule.applyRules(configuration);
        if (configuration.isBuckslipProcess())
        {
            // CaseFileWorkflowListener is not handling buckslip process
            return;
        }
        log.debug("start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(caseFile, configuration, activitiRuntimeService, formService);
        }
    }

    private void startBusinessProcess(CaseFile caseFile, EcmFileWorkflowConfiguration configuration,
            RuntimeService activitiRuntimeService, FrevvoFormAbstractService formService)
    {
        String processName = configuration.getProcessName();

        Map<String, Object> pvars = new HashMap<>();

        String reviewersCsv = configuration.getApprovers();
        List<String> reviewers = reviewersCsv == null ? new ArrayList<>()
                : Arrays.stream(reviewersCsv.split(",")).filter(s -> s != null).map(s -> s.trim()).collect(Collectors.toList());
        pvars.put("reviewers", reviewers);
        pvars.put("taskName", configuration.getTaskName());
        pvars.put("documentAuthor", caseFile.getCreator());
        pvars.put("pdfRenditionId", configuration.getEcmFile().getFileId());
        pvars.put("documentType", caseFile.getContainer().getContainerObjectTitle());
        pvars.put("OBJECT_TYPE", "CASE_FILE");
        pvars.put("OBJECT_ID", caseFile.getId());
        pvars.put("OBJECT_NAME", caseFile.getCaseNumber());
        pvars.put("CASE_FILE", caseFile.getId());
        pvars.put("REQUEST_TYPE", !configuration.getRequestType().isEmpty() ? configuration.getRequestType() : "BACKGROUND_INVESTIGATION");
        pvars.put("REQUEST_ID", caseFile.getId());
        String cmisFolderId = formService.findFolderIdForAttachments(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
        pvars.put("OBJECT_FOLDER_ID", cmisFolderId);
        pvars.put("taskDueDateExpression", configuration.getTaskDueDateExpression());
        pvars.put("taskPriority", configuration.getTaskPriority());

        if (caseFile.getOriginator() != null
                && caseFile.getOriginator().getPerson() != null
                && caseFile.getOriginator().getPerson().getFamilyName() != null)
        {
            pvars.put("SUBJECT_LAST_NAME", caseFile.getOriginator().getPerson().getFamilyName());
        }

        log.debug("starting process: " + processName);

        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(processName, pvars);

        log.debug("process ID: " + pi.getId());
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
