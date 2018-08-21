package com.armedia.acm.plugins.complaint.pipeline.postsave;

import static com.armedia.acm.plugins.complaint.model.CloseComplaintConstants.CLOSE_COMPLAINT_DOCUMENT;

import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintEvent;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CreateTaskCloseComplaintHandler
        implements ApplicationEventPublisherAware, PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        Complaint complaint = ctx.getComplaint();
        UploadedFiles uploadedFiles = new UploadedFiles();
        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(complaint.getContainer().getId(),
                complaint.getContainer().getAttachmentFolder().getId(), CLOSE_COMPLAINT_DOCUMENT);

        uploadedFiles.setPdfRendition(existing);

        CloseComplaintEvent event = new CloseComplaintEvent(complaint.getComplaintNumber(),
                complaint.getComplaintId(),
                entity, uploadedFiles, mode, ctx.getAuthentication().getName(), ctx.getIpAddress(),
                true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
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

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
