package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.pipeline.postsave.CaseFileRulesHandler;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

/**
 * Created by armdev on 8/26/15.
 */
public class QueueCaseServiceImpl implements QueueCaseService
{
    private PipelineManager<CaseFile, CaseFilePipelineContext> queuePipelineManager;
    private CaseFileDao caseFileDao;

    private UserTrackerService userTrackerService;
    private AcmQueueDao acmQueueDao;

    private CaseFileRulesHandler rulesHandler;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile enqueue(Long caseFileId, String queueName, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        // somehow the normal find and save DAO methods aren't working for me here. Changes to CaseFile itself
        // don't get persisted. But if I skip detach and add persist and flush, all seems well.
        CaseFile caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        getCaseFileDao().getEm().refresh(caseFile);

        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        if (caseFile.getQueue() != null)
        {
            ctx.setQueueName(caseFile.getQueue().getName());
        }
        ctx.setEnqueueName(queueName);
        ctx.setAuthentication(auth);
        ctx.setIpAddress(ipAddress);

        return getQueuePipelineManager().executeOperation(caseFile, ctx, () ->
        {
            CaseFile merged = getCaseFileDao().getEm().merge(caseFile);
            getCaseFileDao().getEm().persist(merged);

            getCaseFileDao().getEm().flush();
            log.debug("Case file state: {}, queue: {}", merged.getStatus(),
                    merged.getQueue() == null ? "null" : merged.getQueue().getName());
            return merged;
        });

    }

    @Override
    public CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        CaseFile caseFile;

        try
        {
            caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        } catch (EntityNotFoundException e)
        {
            // try and flush our SQL in case we are trying to operate on a brand new object
            getCaseFileDao().getEm().flush();
            caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        }

        // this version of enqueue is to be called from Activiti processes that do their own orchestration, so
        // we will not execute a pipeline here.
        AcmQueue queue = getAcmQueueDao().findByName(queueName);
        caseFile.setQueue(queue);

        caseFile = getCaseFileDao().save(caseFile);

        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        rulesHandler.execute(caseFile, ctx);

        // flush in case another handler needs to see our changes
        getCaseFileDao().getEm().flush();

        log.debug("Case file state: {}, queue: {}", caseFile.getStatus(),
                caseFile.getQueue() == null ? "null" : caseFile.getQueue().getName());

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

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }

    public void setRulesHandler(CaseFileRulesHandler rulesHandler)
    {
        this.rulesHandler = rulesHandler;
    }
}
