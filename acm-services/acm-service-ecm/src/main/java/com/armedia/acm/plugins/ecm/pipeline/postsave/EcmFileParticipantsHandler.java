package com.armedia.acm.plugins.ecm.pipeline.postsave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
public class EcmFileParticipantsHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private EcmFileParticipantService fileParticipantService;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity.getParticipants().size() == 0)
        {
            getFileParticipantService().setFileParticipantsFromParentFolder(entity);
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService participantService)
    {
        this.fileParticipantService = participantService;
    }

}
