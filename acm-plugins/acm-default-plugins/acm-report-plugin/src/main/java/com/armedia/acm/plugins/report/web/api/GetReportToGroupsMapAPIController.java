package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.report.service.ReportService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class GetReportToGroupsMapAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private ReportService reportService;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/reporttogroupsmap", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<String>> getReportToGroupsMap()
    {
        LOG.debug("Getting report to groups map ...");
        Map<String, List<String>> retval = getReportService().getReportToGroupsMap();
        if (null == retval)
        {
            LOG.warn("Properties not available..");
        }
        LOG.debug("Reports to groups map : " + retval.toString());
        return retval;
    }

    @RequestMapping(value = "/reportstogroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getReportsPaged(
            @RequestParam(value = "sortBy", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws IOException
    {
        LOG.debug("Getting reports ...");

        List<String> retval = getReportService().getReportToGroupsPaged(sortDirection, startRow, maxRows);
        if (null == retval)
        {
            LOG.warn("Properties not available..");
        }
        LOG.debug("Reports to groups map : " + retval.toString());
        return retval;
    }

    @RequestMapping(value = "/reportstogroups", params = { "fq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getReportsByName(
            @RequestParam(value = "fq") String filterQuery,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws IOException
    {
        LOG.debug("Getting reports ...");

        List<String> retval = getReportService().getReportToGroupsByName(sortDirection, startRow, maxRows, filterQuery);
        if (null == retval)
        {
            LOG.warn("Properties not available..");
        }
        LOG.debug("Reports to groups map : " + retval.toString());
        return retval;
    }

    @RequestMapping(value = "/{reportId:.+}/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForReport(@PathVariable("reportId") String reportId,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws MuleException, AcmEncryptionException
    {
        LOG.debug("Taking groups from Solr for specific report");

        String solrQuery = reportService.buildGroupsForReportSolrQuery(authorized, reportId, "");

        LOG.debug("Returning groups for report [{}]", reportId);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @RequestMapping(value = "/{reportId:.+}/groups", params = {
            "fq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findGroupsForReport(@PathVariable("reportId") String reportId,
            @RequestParam(value = "fq") String filterQuery,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            Authentication auth) throws MuleException, AcmEncryptionException
    {
        LOG.debug("Taking groups from Solr for specific report");

        String solrQuery = reportService.buildGroupsForReportSolrQuery(authorized, reportId, filterQuery);

        LOG.debug("Returning groups for report [{}]", reportId);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, solrQuery, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }
}
