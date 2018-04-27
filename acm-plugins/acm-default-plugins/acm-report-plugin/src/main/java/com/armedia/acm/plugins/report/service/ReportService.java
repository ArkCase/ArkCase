package com.armedia.acm.plugins.report.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.report.model.Report;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public interface ReportService
{

    public List<Report> getPentahoReports() throws Exception, MuleException;

    public List<Report> getAcmReports();

    public List<Report> getAcmReports(String userId);

    public Map<String, String> getAcmReportsAsMap(List<Report> reports);

    public boolean saveReports(List<Report> reports) throws AcmEncryptionException;

    public Map<String, List<String>> getReportToGroupsMap();

    public List<String> getReportToGroups(String sortDirection, Integer startRow, Integer maxRows, String filterQuery) throws IOException;

    public List<String> getReportToGroupsPaged(String sortDirection, Integer startRow, Integer maxRows) throws IOException;

    public List<String> getReportToGroupsByName(String sortDirection, Integer startRow, Integer maxRows, String filterQuery)
            throws IOException;

    public boolean saveReportToGroupsMap(Map<String, List<String>> reportToGroupsMap, Authentication auth);

    public List<String> saveAdhocGroupsToReport(String reportName, List<String> adhocGroups, Authentication auth);

    public List<String> removeAdhocGroupsToReport(String reportName, List<String> adhocGroups, Authentication auth);

    public List<Report> sync() throws Exception;

    public String buildGroupsForReportSolrQuery(Boolean authorized, String reportId, String filterQuery) throws AcmEncryptionException;
}
