package com.armedia.acm.plugins.report.service;

import java.util.List;

import org.mule.api.MuleException;

import com.armedia.acm.plugins.report.model.Report;

/**
 * @author riste.tutureski
 *
 */
public interface ReportService {
	
	public List<Report> getPentahoReports() throws Exception, MuleException;
	public List<Report> getAcmReports();
	public boolean saveReports(List<Report> reports);
	
}
