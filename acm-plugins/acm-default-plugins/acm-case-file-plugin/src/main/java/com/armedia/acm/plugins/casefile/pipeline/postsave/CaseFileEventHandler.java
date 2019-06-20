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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Publish new case file is created
 */
@Deprecated
public class CaseFileEventHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    private CaseFileEventUtility caseFileEventUtility;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("CaseFile entering CaseFileEventHandler : [{}]", entity);

        if (pipelineContext.isNewCase())
        {
            log.info("CaseFile is new : [{}]", entity);
            // Not sure why we would need to raise an event here since the transaction is not complete
            // getCaseFileEventUtility().raiseCaseFileCreated(entity, pipelineContext.getAuthentication());
        }
        log.trace("CaseFile exiting CaseFileEventHandler : [{}]", entity);

    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }
}
