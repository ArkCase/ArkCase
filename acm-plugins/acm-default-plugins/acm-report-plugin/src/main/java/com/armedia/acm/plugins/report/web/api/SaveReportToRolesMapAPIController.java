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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class SaveReportToRolesMapAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());
    private ReportService reportService;

    @RequestMapping(value = "/reporttorolesmap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveReportToRolesMap(@RequestBody Map<String, List<String>> reportToRolesMap, Authentication auth)
    {
        LOG.debug("Saving reports to roles map ...");

        boolean retval = getReportService().saveReportToRolesMap(reportToRolesMap, auth);
        LOG.debug("Successfuly saved ? " + retval);

        return retval;
    }

    @RequestMapping(value = "/{reportName:.+}/roles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> addRolesToReport(@PathVariable("reportName") String reportName, @RequestBody List<Object> roles,
            Authentication auth) throws Exception
    {
        LOG.debug("Saving roles to report [{}]", reportName);

        return getReportService().saveRolesToReport(reportName, roles, auth);
    }

    @RequestMapping(value = "/{reportName:.+}/roles", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Object> removeRolesToReport(@PathVariable("reportName") String reportName, @RequestBody List<Object> roles,
            Authentication auth) throws Exception
    {
        LOG.debug("Saving roles to report [{}]", reportName);

        return getReportService().removeRolesToReport(reportName, roles, auth);
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
