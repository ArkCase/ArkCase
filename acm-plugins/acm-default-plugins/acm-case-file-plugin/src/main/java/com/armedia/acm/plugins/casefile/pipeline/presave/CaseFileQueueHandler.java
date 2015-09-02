package com.armedia.acm.plugins.casefile.pipeline.presave;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 9/1/15.
 */
public class CaseFileQueueHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    private AcmQueueDao acmQueueDao;
    private transient final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        String queueName = pipelineContext.getEnqueueName();
        AcmQueue queue = getAcmQueueDao().findByName(queueName);

        entity.setQueue(queue);

        entity.setStatus("ACTIVE");

        log.debug("Set case file queue to {}", queue.getName());
    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }
}
