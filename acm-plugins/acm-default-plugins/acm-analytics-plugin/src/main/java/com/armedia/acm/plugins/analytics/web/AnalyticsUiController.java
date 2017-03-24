package com.armedia.acm.plugins.analytics.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author sasko.tanaskoski
 *
 */
@RequestMapping("/plugin/analytics")
public class AnalyticsUiController
{

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAnalyticsPage(Authentication auth)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("analytics");
        // retval.addObject("reportUrlsMap", getReportService().getAcmReportsAsMap(reports));

        return retval;
    }
}