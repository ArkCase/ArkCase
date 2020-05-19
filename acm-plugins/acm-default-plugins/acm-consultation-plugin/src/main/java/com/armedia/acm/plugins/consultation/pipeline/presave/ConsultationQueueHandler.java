package com.armedia.acm.plugins.consultation.pipeline.presave;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsultationQueueHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmQueueDao acmQueueDao;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        String queueName = pipelineContext.getEnqueueName();
        AcmQueue queue = getAcmQueueDao().findByName(queueName);

        entity.setQueue(queue);

        entity.setStatus("ACTIVE");

        log.debug("Set consultation queue to {}", queue.getName());
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
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
