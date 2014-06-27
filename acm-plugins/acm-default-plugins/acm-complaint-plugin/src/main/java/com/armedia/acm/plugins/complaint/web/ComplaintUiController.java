package com.armedia.acm.plugins.complaint.web;

import com.armedia.acm.web.AcmPageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/complaint")
public class ComplaintUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPageDescriptor pageDescriptorWizard;
    private AcmPageDescriptor pageDescriptorList;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openComplaintList()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintList");
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/{complaintId}", method = RequestMethod.GET)
    public ModelAndView openComplaintDetail(@PathVariable(value = "complaintId") Long complaintId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintList");
        retval.addObject("complaintId", complaintId);
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openComplaintWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");
        retval.addObject("pageDescriptor",  getPageDescriptorWizard());
        return retval;

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

}
