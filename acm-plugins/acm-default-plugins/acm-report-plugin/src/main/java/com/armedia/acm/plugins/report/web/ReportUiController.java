package com.armedia.acm.plugins.report.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.report.config.ReportUrl;

@RequestMapping("/plugin/report")
public class ReportUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
	private ReportUrl reportUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showReportPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("report");
        retval.addObject("reportUrlsMap", reportUrl.getNewReportUrlList());
        
        return retval;
    }

	public ReportUrl getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(ReportUrl reportUrl) {
		this.reportUrl = reportUrl;
	}
}
