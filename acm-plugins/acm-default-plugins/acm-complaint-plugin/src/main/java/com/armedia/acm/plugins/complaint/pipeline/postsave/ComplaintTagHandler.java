package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;
import com.armedia.acm.services.tag.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComplaintTagHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{

    private final Logger log = LogManager.getLogger(getClass());
    private TagService tagService;
    private AssociatedTagService associatedTagService;

    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity.getTag() != null)
        {
            try {
                log.debug("Creating Tag and AssociatedTag object for Complaint with id: '{}' and number: '{}'", entity.getComplaintId(), entity.getComplaintNumber());
                String tagName = entity.getTag();
                AcmTag complaintTag = getTagService().saveTag(tagName, tagName, tagName);
                getAssociatedTagService().saveAssociateTag("COMPLAINT", entity.getComplaintId(), entity.getComplaintTitle(), complaintTag);
            } catch (Exception e)
            {
                throw new PipelineProcessException("Unable to create Associated Tag for Complaint with number: " + entity.getComplaintNumber());
            }
        }
    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        //
    }

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

    public AssociatedTagService getAssociatedTagService()
    {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService)
    {
        this.associatedTagService = associatedTagService;
    }
}
