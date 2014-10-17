package com.armedia.acm.plugins.complaint.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;


@RequestMapping("/plugin/complaint")
public class ComplaintUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AuthenticationTokenService authenticationTokenService;
	private FormUrl formUrl;
	
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openComplaints(Authentication auth) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaint");

        String token = this.authenticationTokenService.getTokenForAuthentication(auth);
        retval.addObject("token", token);
        
        // Frevvo form URLs
        retval.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        retval.addObject("closeComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CLOSE_COMPLAINT));

        return retval;
    }

    @RequestMapping(value = "/{complaintId}", method = RequestMethod.GET)
    public ModelAndView openComplaint(Authentication auth, @PathVariable(value = "complaintId") Long complaintId
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaint");
        retval.addObject("complaintId", complaintId);

        String token = this.authenticationTokenService.getTokenForAuthentication(auth);
        retval.addObject("token", token);
        
        // Frevvo form URLs
        retval.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        retval.addObject("closeComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CLOSE_COMPLAINT));
        
        log.debug("Security token: " + token);
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openComplaintWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintWizard");

        // Frevvo form URLs
        retval.addObject("newComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.COMPLAINT));

        return retval;

    }

    @RequestMapping(value = "/old", method = RequestMethod.GET)
    public ModelAndView openComplaintList(Authentication auth,
                                          @RequestParam(value = "initId", required = false) Integer initId
            ,@RequestParam(value = "initTab", required = false) String initTab
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintList");
        retval.addObject("initId",  initId);
        retval.addObject("initTab",  initTab);

        String token = this.authenticationTokenService.getTokenForAuthentication(auth);
        retval.addObject("token", token);
        retval.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));

        log.debug("Security token: " + token);
        return retval;
    }

    @RequestMapping(value = "/old/{complaintId}", method = RequestMethod.GET)
    public ModelAndView openComplaintDetail(@PathVariable(value = "complaintId") Long complaintId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("complaintList");
        retval.addObject("complaintId", complaintId);
        return retval;
    }

	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;
	}

	public FormUrl getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(FormUrl formUrl) {
		this.formUrl = formUrl;
	}

}
