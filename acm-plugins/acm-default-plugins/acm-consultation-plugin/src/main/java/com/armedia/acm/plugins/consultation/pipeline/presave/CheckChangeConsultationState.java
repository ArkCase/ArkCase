package com.armedia.acm.plugins.consultation.pipeline.presave;

import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckChangeConsultationState implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private Logger LOG = LogManager.getLogger(getClass());

    private ConsultationDao consultationDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        if (form == null)
        {
            throw new PipelineProcessException("Cannot un marshall Close Consultation Form.");
        }

        // Get Consultation depends on the Consultation ID
        Consultation consultation = getConsultationDao().find(form.getConsultationId());

        if (consultation == null)
        {
            throw new PipelineProcessException(String.format("Cannot find consultation file by given consultationId=%d", form.getConsultationId()));
        }

        // Skip if the consultation is already closed or in "in approval" and if it's not edit mode
        if (("IN APPROVAL".equals(consultation.getStatus())) && !"edit".equals(mode))
        {
            LOG.info("The consultation is already in '[{}]' mode. No further action will be taken.", consultation.getStatus());
            message = String.format("The consultation is already in '%s' mode. No further action will be taken.", consultation.getStatus());
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
    }

    @Override
    public void rollback(ChangeConsultationStatus entity, ConsultationPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
