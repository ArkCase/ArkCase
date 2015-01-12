/**
 * 
 */
package com.armedia.acm.plugins.report.web.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;

/**
 * @author riste.tutureski
 *
 */
@Controller

@RequestMapping( { "/api/v1/plugin/report", "/api/latest/plugin/report"} )
public class GetPentahoReportsAPIController {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private String reportsPropertyFileLocation;
	private ReportService reportService;
	private PropertyFileManager propertyFileManager;
	
	@RequestMapping(value = "/get/pentaho", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	@ResponseBody
	public List<Report> getPentahoReports(Authentication auth) throws Exception
	{
		LOG.info("Retrieving Pentaho reports.");
		
		List<Report> retval = new ArrayList<Report>();
		List<Report> reports = null;
		
		try 
		{
			reports = getReportService().getPentahoReports();
		} 
		catch (Exception e) 
		{
			throw e;
		}	
		
		if (reports != null)
		{			
			for (Report report : reports)
			{
				if (!report.isFolder())
				{
					String acmReportProperty = null;

					try
					{
						acmReportProperty = getPropertyFileManager().load(getReportsPropertyFileLocation(), report.getPropertyName(), null);
					}
					catch(Exception e)
					{
						LOG.warn("Cannot find property in the report properties file.");
					}
					
					if (acmReportProperty != null)
					{
						report.setInjected(true);
					}
					
					retval.add(report);
				}
			}
		}
		
		LOG.info("Retrived " + retval.size() + " Pentaho reports.");
		
		return retval;
	}
	
	public String getReportsPropertyFileLocation() {
		return reportsPropertyFileLocation;
	}

	public void setReportsPropertyFileLocation(String reportsPropertyFileLocation) {
		this.reportsPropertyFileLocation = reportsPropertyFileLocation;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}
}
