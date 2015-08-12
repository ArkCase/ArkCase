package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Alfresco folder on saving a Complaint.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintEcmFolderHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    /**
     * CMIS service.
     */
    private EcmFileService ecmFileService;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
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
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
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
