package com.armedia.acm.plugins.report.service;

import java.util.List;
import java.util.Map;

import org.mule.api.MuleException;

import com.armedia.acm.plugins.report.model.Report;
import org.springframework.security.core.Authentication;

/**
 * @author riste.tutureski
 *
 */
public interface ReportService {
	
	public List<Report> getPentahoReports() throws Exception, MuleException;
	public List<Report> getAcmReports();
	public boolean saveReports(List<Report> reports);
    public Map<String, List<String>> getReportToGroupsMap();
    public boolean saveReportToGroupsMap(Map<String, List<String>> reportToGroupsMap, Authentication auth);
}
