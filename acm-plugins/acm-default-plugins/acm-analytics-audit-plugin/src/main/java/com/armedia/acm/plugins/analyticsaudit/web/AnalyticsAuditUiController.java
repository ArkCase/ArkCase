package com.armedia.acm.plugins.analyticsaudit.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author sasko.tanaskoski
 *
 */
@RequestMapping("/plugin/analytics-audit")
public class AnalyticsAuditUiController
{

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAnalyticsAuditPage(Authentication auth)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("analytics-audit");

        return retval;
    }
}