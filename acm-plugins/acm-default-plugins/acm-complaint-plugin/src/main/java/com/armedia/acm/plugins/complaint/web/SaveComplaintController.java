package com.armedia.acm.plugins.complaint.web;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;

@RequestMapping("/plugin/complaint")
public class SaveComplaintController
{


    private Logger log = LoggerFactory.getLogger(getClass());

    private SaveComplaintTransaction complaintTransaction;
    private SaveComplaintEventPublisher eventPublisher;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView complaint()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaint");
        retval.addObject("complaint", new Complaint());

        return retval;
    }

    //@RequestMapping(value = "/wizard", method = RequestMethod.POST)
    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView saveComplaint(
            @Valid Complaint complaint,
            BindingResult bindingResult,
            Authentication authentication)
    {
        // auditing and exception handling are handled here; transactions must be handled in the service layer.
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");

        boolean isInsert = complaint.getComplaintId() == null;

        log.info("Complaint ID: " + complaint.getComplaintId());

        if ( bindingResult.hasErrors() )
        {
            getEventPublisher().publishComplaintEvent(
                    complaint, authentication, isInsert, false);
            retval.addObject("complaint", complaint);
            retval.addObject("succeeded", false);
            retval.addObject("errors", bindingResult.getAllErrors());
            return retval;
        }

        try
        {
            Complaint saved = getComplaintTransaction().saveComplaint(complaint, authentication);

            retval.addObject("complaint", saved);
            retval.addObject("succeeded", true);

            getEventPublisher().publishComplaintEvent(saved, authentication, isInsert, true);

            return retval;

        } catch ( MuleException | TransactionException e)
        {
            log.error("Could not save complaint: " + e.getMessage(), e);
            // TODO: return the current complaint from the db, since the update failed
            retval.addObject("complaint", complaint);
            retval.addObject("succeeded", false);
            retval.addObject("errors", Arrays.asList(new ObjectError("complaint", e.getMessage())));

            getEventPublisher().publishComplaintEvent(
                    complaint, authentication, isInsert, false);

            return retval;
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
}
