package com.armedia.acm.plugins.report.web.api;

/*-
 * #%L
 * ACM Default Plugin: report
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class GetAuthorizedReportsAPIController
{

    /**
     * Logger instance.
     */
    private final Logger LOG = LogManager.getLogger(getClass());

    /**
     * Reports service instance.
     */
    private ReportService reportService;

    @RequestMapping(value = "/authorized", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Map<String, String> getAuthorizedReports(Authentication auth)
    {
        String userid = auth.getName();
        LOG.debug("User [{}] is retrieving the list of reports they have access to", userid);

        Map<String, String> reports = new HashMap<>();

        List<Report> authorizedReports = getReportService().getAcmReports(userid);
        authorizedReports.forEach(r -> reports.put(r.getPropertyName(), r.getPropertyPath()));

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
