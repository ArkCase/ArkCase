package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ensure that Consultation container exists.
        */
public class ConsultationContainerHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("Consultation entering ConsultationContainerHandler : [{}]", entity);
        if (entity.getContainer() == null)
        {
            AcmContainer container = new AcmContainer();
            entity.setContainer(container);
        }

        if (entity.getContainer().getContainerObjectType() == null)
        {
            entity.getContainer().setContainerObjectType(entity.getObjectType());
        }
        if (entity.getContainer().getContainerObjectTitle() == null)
        {
            entity.getContainer().setContainerObjectTitle(entity.getConsultationNumber());
        }

        if (entity.getContainer().getFolder() == null)
        {
            AcmFolder folder = new AcmFolder();
            folder.setName("ROOT");
            folder.setParticipants(getFileParticipantService().getFolderParticipantsFromAssignedObject(entity.getParticipants()));

            entity.getContainer().setFolder(folder);
            entity.getContainer().setAttachmentFolder(folder);
        }

        log.trace("Consultation exiting ConsultationContainerHandler : [{}]", entity);
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
