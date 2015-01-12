/**
 * 
 */
package com.armedia.acm.plugins.report.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;

/**
 * @author riste.tutureski
 *
 */
@Controller

@RequestMapping( { "/api/v1/plugin/report", "/api/latest/plugin/report"} )
public class SaveReportsAPIController {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ReportService reportService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	@ResponseBody
	public List<Report> saveReports(@RequestBody List<Report> reports, Authentication auth) throws Exception
	{
		LOG.info("Saving reports on ACM side.");
		
		boolean success = false;
		try 
		{
			success = getReportService().saveReports(reports);
		} 
		catch (Exception e) 
		{
			throw e;
		}	
		
		if (success == false)
		{
			throw new RuntimeException("The reports are not successfully saved.");
		}
		
		return reports;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
}
