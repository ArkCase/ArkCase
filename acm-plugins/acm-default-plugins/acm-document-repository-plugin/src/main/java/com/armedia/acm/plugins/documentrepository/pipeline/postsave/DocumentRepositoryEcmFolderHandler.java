package com.armedia.acm.plugins.documentrepository.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Alfresco folder on saving a DocumentRepository.
 */
public class DocumentRepositoryEcmFolderHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * CMIS service.
     */
    private EcmFileService ecmFileService;

    @Override
    public void execute(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {

        if (entity.getEcmFolderPath() != null)
        {
            try
            {
                String folderId = ecmFileService.createFolder(entity.getEcmFolderPath());
                entity.getContainer().getFolder().setCmisFolderId(folderId);
            } catch (AcmCreateObjectFailedException e)
            {
                throw new PipelineProcessException(e);
            }

        } else
        {
            log.info("There is no need to create folder");
        }
    }

    @Override
    public void rollback(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        // TODO: implement CMIS folder deletion
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
