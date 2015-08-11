package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Alfresco folder on saving a Case File.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileEcmFolderHandler implements PipelineHandler<CaseFile>
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        CaseFilePipelineContext context = (CaseFilePipelineContext) pipelineContext;

        if (entity.getEcmFolderPath() != null)
        {
            try
            {
                String folderId = context.getEcmFileService().createFolder(entity.getEcmFolderPath());
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
    public void rollback(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
