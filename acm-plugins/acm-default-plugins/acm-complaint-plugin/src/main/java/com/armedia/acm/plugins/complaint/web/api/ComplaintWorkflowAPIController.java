package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class ComplaintWorkflowAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ComplaintEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, value = "/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint startApprovalWorkflow(
            @RequestBody Complaint in,
            Authentication auth,
            HttpServletResponse response,
            HttpSession session
    ) throws AcmUserActionFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Got a complaint: " + in + "; complaint ID: '" + in.getComplaintId() + "'");
            log.debug("complaint type: " + in.getComplaintType());
        }

        Long complaintId = in.getComplaintId();
        boolean isNew = (null == complaintId) || (0 == complaintId);

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        if ( isNew )
        {
            getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress, false);
            throw new AcmUserActionFailedException(
                    "start approval process",
                    "task",
                    complaintId,
                    "You must save the complaint first",
                    null);
        }


        getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress, true);

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

}
