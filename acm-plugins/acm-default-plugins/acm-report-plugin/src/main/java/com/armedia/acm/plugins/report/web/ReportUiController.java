package com.armedia.acm.plugins.report.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;

@RequestMapping("/plugin/report")
public class ReportUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
	private ReportService reportService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showReportPage(Authentication auth)
    {
    	List<Report> reports = getReportService().getAcmReports(auth.getName());
    	log.debug("Reports: " + reports);
    	
        ModelAndView retval = new ModelAndView();
        retval.setViewName("report");
        retval.addObject("reportUrlsMap", getReportService().getAcmReportsAsMap(reports));
        
        return retval;
    }

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

}
