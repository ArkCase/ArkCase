package com.armedia.acm.plugins.documentrepository.pipeline.postsave;

import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Ensure that Document Repository container exists.
 */
public class DocumentRepositoryContainerHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{
    @Override
    public void execute(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        if (entity.getContainer() == null)
        {
            AcmContainer container = new AcmContainer();
            entity.setContainer(container);
            entity.getContainer().setContainerObjectType(entity.getObjectType());
            entity.getContainer().setContainerObjectTitle(entity.getName());
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
    public void rollback(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
