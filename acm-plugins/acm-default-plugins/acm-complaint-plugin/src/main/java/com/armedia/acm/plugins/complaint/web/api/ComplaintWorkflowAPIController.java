package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
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
    private SaveComplaintEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, value = "/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint startApprovalWorkflow(
            @RequestBody Complaint in,
            Authentication auth,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("Got a complaint: " + in + "; complaint ID: '" + in.getComplaintId() + "'");
            log.trace("complaint type: " + in.getComplaintType());
        }

        boolean isNew = in.getComplaintId() == null;

        if ( !isNew )
        {
            sendErrorResponse(HttpStatus.BAD_REQUEST, "You must save the complaint first.", response);
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress);

        return in;

    }


    public void sendErrorResponse(HttpStatus httpStatus, String message, HttpServletResponse response) throws IOException
    {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        byte[] bytes = message.getBytes();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    public SaveComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(SaveComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
