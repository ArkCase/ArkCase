package com.armedia.acm.plugins.complaint.pipeline.presave;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Ensure that Complaint container exists.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintContainerHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity.getContainer() == null)
        {
            AcmContainer container = new AcmContainer();
            entity.setContainer(container);
            entity.getContainer().setContainerObjectType(entity.getObjectType());
            entity.getContainer().setContainerObjectTitle(entity.getComplaintNumber());
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
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
