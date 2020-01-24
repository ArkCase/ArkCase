package com.armedia.acm.plugins.casefile.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
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

        // Get CaseFile depends on the CaseFile ID
        CaseFile caseFile = getCaseFileDao().find(form.getCaseId());

        // Update Status to "IN APPROVAL"

        if(ctx.getPropertyValue("changeCaseStatusFlow").equals(false)){
            caseFile.setStatus(form.getStatus());
        }
        else if (!caseFile.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            caseFile.setStatus("IN APPROVAL");

        }
        CaseFile updatedCaseFile = getCaseFileDao().save(caseFile);

        ctx.setCaseFile(updatedCaseFile);

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
