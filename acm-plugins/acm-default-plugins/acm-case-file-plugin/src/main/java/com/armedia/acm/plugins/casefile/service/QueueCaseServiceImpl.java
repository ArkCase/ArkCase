package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFileQueuePipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

/**
 * Created by armdev on 8/26/15.
 */
public class QueueCaseServiceImpl implements QueueCaseService
{
    private PipelineManager<CaseFile, CaseFileQueuePipelineContext> queuePipelineManager;
    private CaseFileDao caseFileDao;

    @Override
    public CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException
    {
        CaseFile caseFile = getCaseFileDao().find(caseFileId);

        CaseFileQueuePipelineContext pipelineContext = new CaseFileQueuePipelineContext();
        getQueuePipelineManager().onPreSave(caseFile, pipelineContext);

        caseFile = getCaseFileDao().save(caseFile);

        getQueuePipelineManager().onPostSave(caseFile, pipelineContext);

        return caseFile;
    }

    public PipelineManager<CaseFile, CaseFileQueuePipelineContext> getQueuePipelineManager()
    {
        return queuePipelineManager;
    }

    public void setQueuePipelineManager(PipelineManager<CaseFile, CaseFileQueuePipelineContext> queuePipelineManager)
    {
        this.queuePipelineManager = queuePipelineManager;
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
