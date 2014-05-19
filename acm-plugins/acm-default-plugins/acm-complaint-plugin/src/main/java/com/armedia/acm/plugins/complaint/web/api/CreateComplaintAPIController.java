package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class CreateComplaintAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SaveComplaintTransaction complaintTransaction;
    private SaveComplaintEventPublisher eventPublisher;
    private ComplaintDao complaintDao;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint createComplaint(
            @RequestBody Complaint in,
            Authentication auth
    )
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a complaint: " + in +"; complaint ID: '" + in.getComplaintId() + "'");
            log.trace("complaint type: " + in.getComplaintType());
        }

        boolean isInsert = in.getComplaintId() == null;

        try
        {
            Complaint saved = getComplaintTransaction().saveComplaint(in, auth);

            getEventPublisher().publishComplaintEvent(saved, auth, isInsert, true);

            return saved;

        } catch ( MuleException | TransactionException e)
        {
            log.error("Could not save complaint: " + e.getMessage(), e);
            getEventPublisher().publishComplaintEvent(in, auth, isInsert, false);

            if ( !isInsert )
            {
                Complaint existing = getComplaintDao().find(Complaint.class, in.getComplaintId());
                // TODO: REST-based exception handling (e.g. send a proper http response code)
                return existing;
            }

            return in;
        }

    }

    public SaveComplaintTransaction getComplaintTransaction()
    {
        return complaintTransaction;
    }

    public void setComplaintTransaction(SaveComplaintTransaction complaintTransaction)
    {
        this.complaintTransaction = complaintTransaction;
    }

    public SaveComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(SaveComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
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
