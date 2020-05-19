package com.armedia.acm.plugins.consultation.pipeline.postsave;

import static com.armedia.acm.auth.AuthenticationUtils.getUserIpAddress;

import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusEvent;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CreateTaskChangeConsultationHandler
        implements ApplicationEventPublisherAware, PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        Consultation consultation = ctx.getConsultation();
        UploadedFiles uploadedFile = new UploadedFiles();

        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(consultation.getContainer().getId(),
                consultation.getContainer().getAttachmentFolder().getId(), ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS);

        uploadedFile.setPdfRendition(existing);

        ChangeConsultationStatusEvent event = new ChangeConsultationStatusEvent(consultation.getConsultationNumber(), consultation.getId(), form,
                uploadedFile, mode, ctx.getAuthentication().getName(), getUserIpAddress(), true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
