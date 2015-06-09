package com.armedia.acm.plugins.admin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;

@RequestMapping("/plugin/admin")
public class AdminUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    
    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAdminPage()
    {
        ModelAndView retval = new ModelAndView();
        
        // Add Frevvo form URLs
        retval.addObject("plainConfigurationFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.PLAIN_CONFIGURATION));
        
        retval.setViewName("admin");
        return retval;
    }

    @RequestMapping(value = "/locks", method = RequestMethod.GET)
    public ModelAndView showLocksPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("adminLocks");
        return retval;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView showDashboardPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("adminDashboard");
        return retval;
    }

	public FormUrl getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(FormUrl formUrl) {
		this.formUrl = formUrl;
	}
    
    

}
