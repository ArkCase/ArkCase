package com.armedia.acm.form.casefile.service;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 12/5/14.
 */
public class CaseFileWorkflowListener
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void handleNewCaseFile(CaseFile caseFile, FrevvoUploadedFiles files, RuntimeService activitiRuntimeService,
                                  FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        EcmFile pdfRendition = files.getPdfRendition();
        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        configuration.setEcmFile(pdfRendition);

        log.debug("Calling business rules for new case files");

        configuration = fileWorkflowBusinessRule.applyRules(configuration);

        log.debug("start process? " + configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            startBusinessProcess(caseFile, configuration, activitiRuntimeService);
        }
    }

    private void startBusinessProcess(CaseFile caseFile, EcmFileWorkflowConfiguration configuration,
                                      RuntimeService activitiRuntimeService)
    {
        String processName = configuration.getProcessName();

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("OBJECT_TYPE", "CASE_FILE");
        pvars.put("OBJECT_ID", caseFile.getId());
        pvars.put("OBJECT_NAME", caseFile.getCaseNumber());
        pvars.put("CASE_FILE", caseFile.getId());
        pvars.put("REQUEST_TYPE", "BACKGROUND_INVESTIGATION");
        pvars.put("REQUEST_ID", caseFile.getId());
        pvars.put("OBJECT_FOLDER_ID", caseFile.getEcmFolderId());

        if (caseFile.getOriginator() != null
                && caseFile.getOriginator().getPerson() != null
                && caseFile.getOriginator().getPerson().getFamilyName() != null)
        {
            pvars.put("SUBJECT_LAST_NAME", caseFile.getOriginator().getPerson().getFamilyName());
        }

        log.debug("starting process: " + processName);

        ProcessInstance pi = activitiRuntimeService.startProcessInstanceByKey(processName, pvars);

        log.debug("process ID: " + pi.getId());
    }
}


