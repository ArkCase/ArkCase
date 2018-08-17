package com.armedia.acm.plugins.casefile.pipeline.presave;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckChangeCaseFileState implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        // Convert XML data to Object
        // ChangeCaseStatusForm form = (ChangeCaseStatusForm) convertFromXMLToObject(cleanXML(xml),
        // ChangeCaseStatusForm.class);

        if (form == null)
        {
            message = "Cannot unmarshall Close Case Form.";
        }

        // Get CaseFile depends on the CaseFile ID
        CaseFile caseFile = getCaseFileDao().find(form.getCaseId());

        if (caseFile == null)
        {
            message = "Cannot find case file by given caseId=" + form.getCaseId();
        }

        // Skip if the case is already closed or in "in approval" and if it's not edit mode
        if (("IN APPROVAL".equals(caseFile.getStatus()) || "CLOSED".equals(caseFile.getStatus())) && !"edit".equals(mode))
        {
            LOG.info("The case file is already in '" + caseFile.getStatus() + "' mode. No further action will be taken.");
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
    }

    @Override
    public void rollback(ChangeCaseStatus entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
