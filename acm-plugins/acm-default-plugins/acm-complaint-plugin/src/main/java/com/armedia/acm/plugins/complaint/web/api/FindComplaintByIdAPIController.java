package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindComplaintByIdAPIController
{
    private ComplaintDao complaintDao;
    private ComplaintEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/byId/{complaintId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint findComplaintById(
            @PathVariable("complaintId") Long complaintId,
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding complaint by id '" + complaintId + "'");
        }

        try
        {
            Complaint found = getComplaintDao().find(complaintId);

            if ( found == null )
            {
                throw new AcmObjectNotFoundException("complaint", complaintId, "Object Not Found", null);
            }

            raiseEvent(authentication, session, found, true);

            return found;
        }
        catch (PersistenceException | TransactionException e)
        {
            // make a fake complaint so the event will have the desired complaint id and object type
            Complaint fakeComplaint = new Complaint();
            fakeComplaint.setComplaintId(complaintId);
            raiseEvent(authentication, session, fakeComplaint, false);

            throw new AcmUserActionFailedException("find", "complaint", complaintId, e.getMessage(), e);
        }

    }

    protected void raiseEvent(Authentication authentication, HttpSession session, Complaint found, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishFindComplaintByIdEvent(found, authentication, ipAddress, succeeded);
    }


    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
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
