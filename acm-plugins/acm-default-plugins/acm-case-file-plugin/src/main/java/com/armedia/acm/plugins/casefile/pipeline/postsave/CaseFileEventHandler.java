package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publish new case file is created
 */
@Deprecated
public class CaseFileEventHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    private CaseFileEventUtility caseFileEventUtility;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

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
