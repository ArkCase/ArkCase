package com.armedia.acm.plugins.complaint.web;

import com.armedia.acm.plugins.complaint.model.Complaint;
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


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView complaint()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");
        retval.addObject("complaint", new Complaint());

        return retval;
    }

    @RequestMapping(method= RequestMethod.POST)
    public ModelAndView saveComplaint(
            @Valid Complaint complaint,
            BindingResult bindingResult,
            Authentication authentication)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");

        log.info("Complaint ID: " + complaint.getId());

        if ( bindingResult.hasErrors() )
        {
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

            return retval;

        } catch ( MuleException | TransactionException e)
        {
            log.error("Could not save complaint: " + e.getMessage(), e);
            retval.addObject("complaint", complaint);
            retval.addObject("succeeded", false);
            retval.addObject("errors", Arrays.asList(new ObjectError("complaint", e.getMessage())));
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
}
