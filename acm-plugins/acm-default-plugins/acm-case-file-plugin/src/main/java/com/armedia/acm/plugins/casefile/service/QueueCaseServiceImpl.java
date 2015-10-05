package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by armdev on 8/26/15.
 */
public class QueueCaseServiceImpl implements QueueCaseService
{
    private PipelineManager<CaseFile, CaseFilePipelineContext> queuePipelineManager;
    private CaseFileDao caseFileDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile enqueue(Long caseFileId, String queueName, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        // somehow the normal find and save DAO methods aren't working for me here.  Changes to CaseFile itself
        // don't get persisted.  But if I skip detach and add persist and flush, all seems well.
        CaseFile caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        getCaseFileDao().getEm().refresh(caseFile);

        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        if (caseFile.getQueue() != null)
            ctx.setQueueName(caseFile.getQueue().getName());
        ctx.setEnqueueName(queueName);
        ctx.setAuthentication(auth);
        ctx.setIpAddress(ipAddress);

        getQueuePipelineManager().onPreSave(caseFile, ctx);

        caseFile = getCaseFileDao().getEm().merge(caseFile);
        getCaseFileDao().getEm().persist(caseFile);

        getCaseFileDao().getEm().flush();

        getQueuePipelineManager().onPostSave(caseFile, ctx);

        log.debug("Case file state: {}, queue: {}", caseFile.getStatus(), caseFile.getQueue() == null ? "null" : caseFile.getQueue().getName());

        return caseFile;
    }

    public PipelineManager<CaseFile, CaseFilePipelineContext> getQueuePipelineManager()
    {
        return queuePipelineManager;
    }

    public void setQueuePipelineManager(PipelineManager<CaseFile, CaseFilePipelineContext> queuePipelineManager)
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
