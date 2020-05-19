package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class ConsultationUploadAttachmentsHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        List<AcmMultipartFile> files = null;

        if(pipelineContext.hasProperty("attachmentFiles"))
        {
            files = (List<AcmMultipartFile>)pipelineContext.getPropertyValue("attachmentFiles");
        }

        if (files != null)
        {
            for (AcmMultipartFile file : files)
            {
                if (file != null)
                {

                    String folderId = entity.getContainer().getAttachmentFolder() == null
                            ? entity.getContainer().getFolder().getCmisFolderId()
                            : entity.getContainer().getAttachmentFolder().getCmisFolderId();

                    log.debug("Uploading document for Consultation [{}] as [{}]", entity.getId(), file.getOriginalFilename());

                    try
                    {
                        getEcmFileService().upload(file.getOriginalFilename(), file.getType(), "Document", file.getInputStream(), file.getContentType(),
                                file.getOriginalFilename(), pipelineContext.getAuthentication(),
                                folderId, entity.getObjectType(), entity.getId());
                    }
                    catch (AcmCreateObjectFailedException | AcmUserActionFailedException | IOException e)
                    {
                        log.error(String.format("Could not upload attachment files for Consultation: %s", entity.getConsultationNumber()));
                    }
                }
            }
        }
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
