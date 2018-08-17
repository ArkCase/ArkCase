package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

public class ChangeCaseFileStateHandler
        implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{

    private AcmUserActionExecutor userActionExecutor;
    private CaseFileDao caseFileDao;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");

        if (!"edit".equals(mode))
        {
            // Record user action
            getUserActionExecutor().execute(form.getCaseId(), AcmUserActionName.LAST_CHANGE_CASE_STATUS_CREATED,
                    ctx.getAuthentication().getName());
        }
        else
        {
            // Record user action
            getUserActionExecutor().execute(form.getCaseId(), AcmUserActionName.LAST_CHANGE_CASE_STATUS_MODIFIED,
                    ctx.getAuthentication().getName());
        }

        // Get CaseFile depends on the CaseFile ID
        CaseFile caseFile = getCaseFileDao().find(form.getCaseId());

        // Update Status to "IN APPROVAL"
        if (!caseFile.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            caseFile.setStatus("IN APPROVAL");
            CaseFile updatedCaseFile = getCaseFileDao().save(caseFile);

            ctx.setCaseFile(updatedCaseFile);
        }
    }

    @Override
    public void rollback(ChangeCaseStatus entity, CaseFilePipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
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
