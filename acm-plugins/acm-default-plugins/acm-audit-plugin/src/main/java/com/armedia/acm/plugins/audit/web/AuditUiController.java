package com.armedia.acm.plugins.audit.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/audit")
public class AuditUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAuditPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("audit");
        return retval;
    }
}
