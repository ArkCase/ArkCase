package com.armedia.acm.plugins.complaint.pipeline.presave;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class CheckCloseComplaintRequest implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private ComplaintDao complaintDao;

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        if (form == null)
        {
            message = "Cannot unmarshall Close Complaint Form.";
        }

        Complaint complaint = getComplaintDao().find(form.getComplaintId());
        if (complaint == null)
        {
            message = "Cannot find complaint by given complaintId=" + form.getComplaintId();
        }

        if (("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())) && !"edit".equals(mode))
        {
            message = "The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.";
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
    }

    @Override
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
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
}
