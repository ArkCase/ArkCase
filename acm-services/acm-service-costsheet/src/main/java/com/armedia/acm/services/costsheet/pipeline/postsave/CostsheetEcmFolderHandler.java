package com.armedia.acm.services.costsheet.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Alfresco folder on saving a Costsheet.
 */
public class CostsheetEcmFolderHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
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
    public void execute(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("Costsheet entering CostsheetEcmFolderHandler : [{}]", entity);
        if (entity.getEcmFolderPath() != null)
        {
            try
            {
                String folderId = ecmFileService.createFolder(entity.getEcmFolderPath());
                entity.getContainer().getFolder().setCmisFolderId(folderId);
            }
            catch (AcmCreateObjectFailedException e)
            {
                throw new PipelineProcessException(e);
            }

        }
        else
        {
            log.info("There is no need to create folder");
        }
        log.trace("Costsheet exiting CostsheetEcmFolderHandler : [{}]", entity);
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
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
