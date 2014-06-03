package com.armedia.acm.plugins.complaint.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.web.AcmPageDescriptor;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Arrays;

@RequestMapping("/plugin/complaint")
public class ComplaintUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SaveComplaintTransaction complaintTransaction;
    private SaveComplaintEventPublisher eventPublisher;
    private AcmPageDescriptor pageDescriptorWizard;
    private AcmPageDescriptor pageDescriptorList;
    private AcmPageDescriptor pageDescriptorDetail;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listComplaint()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintList");
        //retval.addObject("complaint", new Complaint());
        retval.addObject("pageDescriptor",  getPageDescriptorList());


        return retval;
    }

    @RequestMapping(value = "/{complaintId}", method = RequestMethod.GET)
    public ModelAndView showComplaint(@PathVariable(value = "complaintId") Long complaintId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintDetail");
        //retval.addObject("complaint", new Complaint());
        retval.addObject("complaintId", complaintId);
        retval.addObject("pageDescriptor",  getPageDescriptorDetail());


        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView saveComplaint(
            @Valid Complaint complaint,
            BindingResult bindingResult,
            Authentication authentication)
    {
        // auditing and exception handling are handled here; transactions must be handled in the service layer.
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");
        retval.addObject("pageDescriptor",  getPageDescriptorWizard());

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

    public AcmPageDescriptor getPageDescriptorWizard() {
        return pageDescriptorWizard;
    }

    public void setPageDescriptorWizard(AcmPageDescriptor pageDescriptorWizard) {
        this.pageDescriptorWizard = pageDescriptorWizard;
    }

    public AcmPageDescriptor getPageDescriptorList() {
        return pageDescriptorList;
    }

    public void setPageDescriptorList(AcmPageDescriptor pageDescriptorList) {
        this.pageDescriptorList = pageDescriptorList;
    }

    public AcmPageDescriptor getPageDescriptorDetail() {
        return pageDescriptorDetail;
    }

    public void setPageDescriptorDetail(AcmPageDescriptor pageDescriptorDetail) {
        this.pageDescriptorDetail = pageDescriptorDetail;
    }
}
