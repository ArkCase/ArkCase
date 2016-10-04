package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieve the list of reports a particular user has access to.
 * <p/>
 * It takes in consideration the reports to groups mapping set using admin console
 * Created by Petar Ilin <petar.ilin@armedia.com> on 03.10.2016.
 */
@Controller
@RequestMapping({"/api/v1/plugin/report", "/api/latest/plugin/report"})
public class GetAuthorizedReportsAPIController
{

    /**
     * Logger instance.
     */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Reports service instance.
     */
    private ReportService reportService;

    @RequestMapping(value = "/authorized", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map<String, String> getAuthorizedReports(Authentication auth)
    {
        String userid = auth.getName();
        LOG.debug("User [{}] is retrieving the list of reports it has access to", userid);

        List<Report> authorizedReports = getReportService().getAcmReports(userid);
        Map<String, String> reports = new HashMap<>();

        if (authorizedReports != null)
        {
            authorizedReports.forEach(r -> reports.put(r.getPropertyName(), r.getPropertyPath()));
        }

        return reports;
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }
}
