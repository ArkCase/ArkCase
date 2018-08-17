package com.armedia.acm.plugins.casefile.pipeline.postsave;

import static com.armedia.acm.auth.AuthenticationUtils.getUserIpAddress;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseFileStatusEvent;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;

public class CreateTaskChangeCaseFileHandler
        implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        CaseFile caseFile = ctx.getCaseFile();
        FrevvoUploadedFiles frevvoUploadedFile = new FrevvoUploadedFiles();

        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(caseFile.getContainer().getId(),
                caseFile.getContainer().getAttachmentFolder().getId(), ChangeCaseStateContants.CHANGE_CASE_STATUS);

        frevvoUploadedFile.setPdfRendition(existing);

        ChangeCaseFileStatusEvent event = new ChangeCaseFileStatusEvent(caseFile.getCaseNumber(), caseFile.getId(), form,
                frevvoUploadedFile, mode, ctx.getAuthentication().getName(), getUserIpAddress(), true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(ChangeCaseStatus form, CaseFilePipelineContext ctx)
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

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
