/**
 * 
 */
package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping( { "/api/v1/plugin/report", "/api/latest/plugin/report"} )
public class SyncReportsAPIController
{
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ReportService reportService;
	
	@RequestMapping(value = "/sync", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	@ResponseBody
	public List<Report> syncReports() throws Exception
	{
		LOG.info("Sync reports on ACM side.");

		List<Report> reports = new ArrayList<>();
		try 
		{
			reports = getReportService().sync();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("The reports are not successfully synced.");
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
