package com.armedia.acm.plugins.complaint.pipeline.presave;

import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class CheckCloseComplaintRequest implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private AcmUserActionExecutor userActionExecutor;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ComplaintDao complaintDao;
    private HttpServletRequest request;

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        String mode = (String) pipelineContext.getPropertyValue("mode");
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
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }
}
