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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class SaveComplaintDetailsAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintDao complaintDao;
    private SaveComplaintEventPublisher eventPublisher;
    private SaveComplaintTransaction complaintTransaction;

    /**
     * The JSON request should include the fields incidentDate, complaintType, priority, complaintTitle, and details.
     * Spring MVC will create a Complaint POJO with only these fields set.  This method looks up the complaint from
     * the database, sets the above fields, and saves the revised complaint.
     *
     * @param complaintId
     * @param in
     * @param auth
     * @return
     */
    @RequestMapping(
            value = "/details/{complaintId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint updateComplaintDetails(
            @PathVariable("complaintId") Long complaintId,
            @RequestBody Complaint in,
            Authentication auth
    )
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a complaint to update: id = '" + complaintId + "'");
        }

        in.setComplaintId(complaintId);

        try
        {
            Complaint fromDb = getComplaintDao().find(Complaint.class, complaintId);
            fromDb.setIncidentDate(in.getIncidentDate());
            fromDb.setComplaintTitle(in.getComplaintTitle());
            fromDb.setDetails(in.getDetails());
            fromDb.setPriority(in.getPriority());
            fromDb.setComplaintType(in.getComplaintType());

            Complaint saved = getComplaintTransaction().saveComplaint(fromDb, auth);

            getEventPublisher().publishComplaintEvent(saved, auth, false, true);

            return saved;
        }
        catch (PersistenceException | TransactionException e)
        {
            getEventPublisher().publishComplaintEvent(in, auth, false, false);
            log.error("Could not save complaint: " + e.getMessage(), e);
            // TODO: send proper HTTP response code
        }
        catch (MuleException pe)
        {
            getEventPublisher().publishComplaintEvent(in, auth, false, false);
            log.error("Could not save complaint: " + pe.getMessage(), pe);
            // TODO: send proper HTTP response code
        }

        return in;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public SaveComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(SaveComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public SaveComplaintTransaction getComplaintTransaction()
    {
        return complaintTransaction;
    }

    public void setComplaintTransaction(SaveComplaintTransaction complaintTransaction)
    {
        this.complaintTransaction = complaintTransaction;
    }
}
