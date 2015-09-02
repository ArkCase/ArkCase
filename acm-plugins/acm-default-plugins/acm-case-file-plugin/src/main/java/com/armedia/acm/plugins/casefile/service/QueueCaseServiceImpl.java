package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFileQueuePipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by armdev on 8/26/15.
 */
public class QueueCaseServiceImpl implements QueueCaseService
{
    private PipelineManager<CaseFile, CaseFileQueuePipelineContext> queuePipelineManager;
    private CaseFileDao caseFileDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        // somehow the normal find and save DAO methods aren't working for me here.  Changes to CaseFile itself
        // don't get persisted.  But if I skip detach and add persist and flush, all seems well.
        CaseFile caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        getCaseFileDao().getEm().refresh(caseFile);

        CaseFileQueuePipelineContext ctx = new CaseFileQueuePipelineContext();
        ctx.setEnqueueName(queueName);

        getQueuePipelineManager().setPipelineContext(ctx);

        CaseFileQueuePipelineContext pipelineContext = new CaseFileQueuePipelineContext();
        getQueuePipelineManager().onPreSave(caseFile, pipelineContext);

        caseFile = getCaseFileDao().getEm().merge(caseFile);
        getCaseFileDao().getEm().persist(caseFile);

        getCaseFileDao().getEm().flush();

        getQueuePipelineManager().onPostSave(caseFile, pipelineContext);

        log.debug("Case file state: {}, queue: {}", caseFile.getStatus(), caseFile.getQueue() == null ? "null" : caseFile.getQueue().getName());

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
