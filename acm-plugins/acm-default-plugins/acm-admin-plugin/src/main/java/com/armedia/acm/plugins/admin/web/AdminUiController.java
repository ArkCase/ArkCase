package com.armedia.acm.plugins.admin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/admin")
public class AdminUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAdminPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("admin");
        return retval;
    }

    @RequestMapping(value = "/access", method = RequestMethod.GET)
    public ModelAndView showAccessPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("adminAccess");
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

}
