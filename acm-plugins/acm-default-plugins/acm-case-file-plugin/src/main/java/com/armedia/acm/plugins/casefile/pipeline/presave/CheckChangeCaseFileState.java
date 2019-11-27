package com.armedia.acm.plugins.casefile.pipeline.presave;

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
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CheckChangeCaseFileState implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private Logger LOG = LogManager.getLogger(getClass());

    private CaseFileDao caseFileDao;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        if (form == null)
        {
            throw new PipelineProcessException("Cannot unmarshall Close Case Form.");
        }

        // Get CaseFile depends on the CaseFile ID
        CaseFile caseFile = getCaseFileDao().find(form.getCaseId());

        if (caseFile == null)
        {
            throw new PipelineProcessException(String.format("Cannot find case file by given caseId=%d", form.getCaseId()));
        }

        // Skip if the case is already closed or in "in approval" and if it's not edit mode
        if (("IN APPROVAL".equals(caseFile.getStatus())) && !"edit".equals(mode))
        {
            LOG.info("The case file is already in '[{}]' mode. No further action will be taken.", caseFile.getStatus());
            message = String.format("The case file is already in '%s' mode. No further action will be taken.", caseFile.getStatus());
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
    }

    @Override
    public void rollback(ChangeCaseStatus entity, CaseFilePipelineContext pipelineContext)
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
