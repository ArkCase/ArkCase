package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.model.Complaint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping( { "/api/v1/complaint", "/api/latest/complaint"})
public class CreateComplaintAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint createComplaint(
            Complaint in,
            Authentication auth
    )
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a complaint: " + in);
        }
        return in;
    }
}
