package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class ComplaintWorkflowAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ComplaintEventPublisher eventPublisher;
    private AcmSpringMvcErrorManager errorManager;

    @RequestMapping(method = RequestMethod.POST, value = "/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint startApprovalWorkflow(
            @RequestBody Complaint in,
            Authentication auth,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Got a complaint: " + in + "; complaint ID: '" + in.getComplaintId() + "'");
            log.debug("complaint type: " + in.getComplaintType());
        }

         Long complaintId = in.getComplaintId();
        boolean isNew = (null == complaintId) || (0 == complaintId);
        if ( isNew )
        {
            getErrorManager().sendErrorResponse(HttpStatus.BAD_REQUEST, "You must save the complaint first.", response);
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress);

        return in;

    }




    public ComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public AcmSpringMvcErrorManager getErrorManager()
    {
        return errorManager;
    }

    public void setErrorManager(AcmSpringMvcErrorManager errorManager)
    {
        this.errorManager = errorManager;
    }
}
