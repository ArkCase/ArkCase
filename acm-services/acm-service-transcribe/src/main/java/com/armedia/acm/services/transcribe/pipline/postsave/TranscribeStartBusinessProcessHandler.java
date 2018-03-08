package com.armedia.acm.services.transcribe.pipline.postsave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeStartBusinessProcessHandler implements PipelineHandler<Transcribe, TranscribePipelineContext>
{
    @Override
    public void execute(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO: Implement starting of the business process functionality
    }

    @Override
    public void rollback(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
