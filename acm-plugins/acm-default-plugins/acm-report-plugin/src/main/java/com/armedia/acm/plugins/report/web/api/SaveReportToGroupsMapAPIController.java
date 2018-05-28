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

import com.armedia.acm.plugins.report.service.ReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class SaveReportToGroupsMapAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private ReportService reportService;

    @RequestMapping(value = "/reporttogroupsmap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveReportToGroupsMap(@RequestBody Map<String, List<String>> reportToGroupsMap, Authentication auth)
    {
        LOG.debug("Saving reports to groups map ...");

        boolean retval = getReportService().saveReportToGroupsMap(reportToGroupsMap, auth);
        LOG.debug("Successfuly saved ? " + retval);

        return retval;
    }

    @RequestMapping(value = "/{reportName:.+}/groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> addGroupsToReport(@PathVariable("reportName") String reportName, @RequestBody List<String> adhocGroups,
            Authentication auth)
    {
        LOG.debug("Saving adhoc groups to report [{}]", reportName);

        return getReportService().saveGroupsToReport(reportName, adhocGroups, auth);
    }

    @RequestMapping(value = "/{reportName:.+}/groups", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> removeGroupsToReport(@PathVariable("reportName") String reportName, @RequestBody List<String> adhocGroups,
            Authentication auth)
    {
        LOG.debug("Saving adhoc groups to report [{}]", reportName);

        return getReportService().removeGroupsToReport(reportName, adhocGroups, auth);
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
