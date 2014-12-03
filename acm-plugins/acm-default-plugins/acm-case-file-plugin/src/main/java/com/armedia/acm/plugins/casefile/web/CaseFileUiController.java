package com.armedia.acm.plugins.casefile.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;

/**
 * Created by jwu on 8/28/14.
 */
@RequestMapping("/plugin/casefile")
public class CaseFileUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
	private FormUrl formUrl;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openComplaints(Authentication auth) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("casefile");
        
        retval.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        retval.addObject("changeCaseStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CASE_STATUS));
        
        return retval;
    }

    @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
    public ModelAndView openComplaint(Authentication auth, @PathVariable(value = "caseId") Long caseId
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("casefile");
        retval.addObject("caseId", caseId);
        
        retval.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        retval.addObject("changeCaseStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CASE_STATUS));
        
        return retval;
    }
    
    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openCaseFileWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("casefileWizard");

        // Frevvo form URLs
        retval.addObject("newCaseFileFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CASE_FILE));

        return retval;

    }

	public FormUrl getFormUrl() {
		return formUrl;
	}
	
	public void setFormUrl(FormUrl formUrl) {
		this.formUrl = formUrl;
	}
	
}
