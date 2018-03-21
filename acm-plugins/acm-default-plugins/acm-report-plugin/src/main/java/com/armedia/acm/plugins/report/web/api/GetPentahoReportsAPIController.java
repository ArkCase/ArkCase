/**
 * 
 */
package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.PentahoFilePropertiesService;
import com.armedia.acm.plugins.report.service.ReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@Controller

@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class GetPentahoReportsAPIController
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ReportService reportService;
    private PentahoFilePropertiesService pentahoFilePropertiesService;

    @RequestMapping(value = "/get/pentaho", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<Report> getPentahoReports(Authentication auth) throws Exception
    {
        LOG.info("Retrieving Pentaho reports.");

        List<Report> retval = pentahoFilePropertiesService.getPentahoReports();

        LOG.info("Retrieved " + retval.size() + " Pentaho reports.");

        return retval;
    }

    @RequestMapping(value = "/pentaho", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<Report> getPentahoReportsPaged(
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            Authentication auth) throws Exception
    {
        LOG.info("Retrieving Pentaho reports.");

        List<Report> retval = pentahoFilePropertiesService.getPentahoReportsPaged(startRow, maxRows, sortDirection);

        LOG.info("Retrieved {} Pentaho reports.", retval.size());

        return retval;
    }

    @RequestMapping(value = "/pentaho", params = { "fn" }, method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<Report> getPentahoReportsByMatchingName(
            @PathVariable(value = "fn") String filterName,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            Authentication auth) throws Exception
    {
        LOG.info("Retrieving Pentaho reports.");

        List<Report> retval = pentahoFilePropertiesService.getPentahoReportsByMatchingName(filterName, startRow, maxRows, sortDirection);

        LOG.info("Retrieved {} Pentaho reports.", retval.size());

        return retval;
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }

    public void setPentahoFilePropertiesService(PentahoFilePropertiesService pentahoFilePropertiesService)
    {
        this.pentahoFilePropertiesService = pentahoFilePropertiesService;
    }

    public PentahoFilePropertiesService getPentahoFilePropertiesService()
    {
        return pentahoFilePropertiesService;
    }
}
