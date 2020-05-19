package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

public class ConsultationFileStateHandler
        implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{

    private AcmUserActionExecutor userActionExecutor;
    private ConsultationDao consultationDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");

        // Get Consultation depends on the Consultation ID
        Consultation consultation = getConsultationDao().find(form.getConsultationId());

        // Update Status to "IN APPROVAL"

        if(ctx.getPropertyValue("changeConsultationStatusFlow").equals(false)){
            consultation.setStatus(form.getStatus());
        }
        else if (!consultation.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            consultation.setStatus("IN APPROVAL");

        }
        Consultation updatedConsultations = getConsultationDao().save(consultation);

        ctx.setConsultation(updatedConsultations);

    }

    @Override
    public void rollback(ChangeConsultationStatus entity, ConsultationPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
