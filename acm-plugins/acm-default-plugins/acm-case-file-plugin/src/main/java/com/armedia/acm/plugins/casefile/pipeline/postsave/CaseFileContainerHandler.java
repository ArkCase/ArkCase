package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Ensure that Case File container exists..
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileContainerHandler implements PipelineHandler<CaseFile>
{
    @Override
    public void execute(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity.getContainer() == null)
        {
            AcmContainer container = new AcmContainer();
            entity.setContainer(container);
            entity.getContainer().setContainerObjectType(entity.getObjectType());
            entity.getContainer().setContainerObjectTitle(entity.getCaseNumber());
        }

        if (entity.getContainer().getFolder() == null)
        {
            AcmFolder folder = new AcmFolder();
            folder.setName("ROOT");
            entity.getContainer().setFolder(folder);
            entity.getContainer().setAttachmentFolder(folder);
        }
    }

    @Override
    public void rollback(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
