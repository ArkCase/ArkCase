package com.armedia.acm.plugins.report.web;

import java.util.Map;
import java.util.TreeMap;

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
	private static final String BILLING_REPORT = "BILLING_REPORT";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showReportPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("report");
        retval.addObject("pageDescriptor", getPageDescriptor());
        retval.addObject("reportUrlsMap", getReportUrlsMap());
        
        return retval;
    }

    /**
     * Private method to organize the report urls and stores them into a map.
     * 
     * @return
     */
    private Map<String, String> getReportUrlsMap() {
        Map<String,String> urlsMap = new TreeMap<String, String>();
        urlsMap.put("Complaint Report",  reportUrl.getNewReportUrl(COMPLAINT_REPORT));
        urlsMap.put("Billing Report", reportUrl.getNewReportUrl(BILLING_REPORT));
        
        log.debug("Report urls: " + urlsMap);

        return urlsMap;
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
