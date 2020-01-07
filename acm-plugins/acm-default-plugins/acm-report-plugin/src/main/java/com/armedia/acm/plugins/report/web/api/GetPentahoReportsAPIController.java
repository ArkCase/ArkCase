/**
 * 
 */
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
import com.armedia.acm.plugins.report.service.PentahoFilePropertiesService;
import com.armedia.acm.plugins.report.service.ReportService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
    private final Logger LOG = LogManager.getLogger(getClass());

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
            @RequestParam(value = "fn") String filterName,
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

    public PentahoFilePropertiesService getPentahoFilePropertiesService()
    {
        return pentahoFilePropertiesService;
    }

    public void setPentahoFilePropertiesService(PentahoFilePropertiesService pentahoFilePropertiesService)
    {
        this.pentahoFilePropertiesService = pentahoFilePropertiesService;
    }
}
