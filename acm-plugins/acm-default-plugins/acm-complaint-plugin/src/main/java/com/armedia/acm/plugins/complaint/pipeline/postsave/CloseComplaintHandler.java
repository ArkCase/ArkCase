package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CloseComplaintHandler
        implements ApplicationEventPublisherAware, PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private ComplaintDao complaintDao;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(CloseComplaintRequest entity, CloseComplaintPipelineContext pipelineContext)
    {
        String mode = (String) pipelineContext.getPropertyValue("mode");
        Complaint complaint = complaintDao.find(entity.getComplaintId());
        if (!complaint.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            complaint.setStatus("IN APPROVAL");
            Complaint updatedComplaint = getComplaintDao().save(complaint);

            pipelineContext.setComplaint(updatedComplaint);

            ComplaintUpdatedEvent complaintUpdatedEvent = new ComplaintUpdatedEvent(updatedComplaint);
            complaintUpdatedEvent.setSucceeded(true);
            pipelineContext.addProperty("complaintUpdated", complaintUpdatedEvent);
            getApplicationEventPublisher().publishEvent(complaintUpdatedEvent);
        }
    }

    @Override
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }
}
