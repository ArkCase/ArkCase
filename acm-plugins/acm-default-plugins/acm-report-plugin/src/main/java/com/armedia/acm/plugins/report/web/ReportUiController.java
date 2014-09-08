package com.armedia.acm.plugins.report.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.report.config.ReportUrl;
import com.armedia.acm.web.AcmPageDescriptor;

@RequestMapping("/plugin/report")
public class ReportUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptor;
	private ReportUrl reportUrl;
	private static final String COMPLAINT_REPORT = "COMPLAINT_REPORT";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showReportPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("report");
        retval.addObject("pageDescriptor", getPageDescriptor());
        log.debug("Report url: " + reportUrl.getNewReportUrl(COMPLAINT_REPORT));
        retval.addObject("complaintReportUrl", reportUrl.getNewReportUrl(COMPLAINT_REPORT));
        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

	public ReportUrl getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(ReportUrl reportUrl) {
		this.reportUrl = reportUrl;
	}
}
