package com.armedia.acm.plugins.report.service;

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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.report.model.PentahoReportFiles;
import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.ScheduleReportException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dwu on 6/9/2017.
 */
public class PentahoFilePropertiesService
{
    private static final Logger LOGGER = LogManager.getLogger(PentahoFilePropertiesService.class);
    private PentahoReportFiles pentahoReportFiles;
    private ResponseEntity<PentahoReportFiles> response;
    private ReportService reportService;
    private ConfigurationPropertyService configurationPropertyService;
    private PentahoReportsConfig reportsConfig;

    public List<Report> getPentahoReports() throws Exception
    {
        List<Report> retval = new ArrayList<>();
        List<Report> reports = getReportService().getPentahoReports();

        if (reports != null)
        {
            for (Report report : reports)
            {
                if (!report.isFolder())
                {
                    String acmReportProperty = (String) configurationPropertyService.getProperty(report.getPropertyName());
                    if (acmReportProperty != null)
                    {
                        report.setInjected(true);
                    }

                    retval.add(report);
                }
            }
        }
        return retval;
    }

    private List<Report> getSortedPentahoReportsPaged(List<Report> reports, Integer start, Integer maxRows, String sortDirection)
    {
        if (sortDirection.contains("DESC"))
        {
            reports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER).reversed());
        }
        else
        {
            reports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER));
        }

        return reports.stream().skip(start).limit(maxRows).collect(Collectors.toList());
    }

    public List<Report> getPentahoReportsPaged(Integer start, Integer maxRows, String sortDirection) throws Exception
    {
        return getSortedPentahoReportsPaged(getPentahoReports(), start, maxRows, sortDirection);
    }

    public List<Report> getPentahoReportsByMatchingName(String filterName, Integer start, Integer maxRows, String sortDirection)
            throws Exception
    {
        List<Report> reports = getPentahoReports().stream()
                .filter(report -> report.getPropertyName().toLowerCase().contains(filterName.toLowerCase()))
                .collect(Collectors.toList());

        return getSortedPentahoReportsPaged(reports, start, maxRows, sortDirection);
    }

    public PentahoReportFiles consumeXML(HttpHeaders headers, RestTemplate restTemplate)
    {
        try
        {
            LOGGER.debug("Start getting generated report metadata process ....");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            HttpEntity<String> request = new HttpEntity<>(headers);

            response = restTemplate.exchange(buildFilePropertiesUrl(),
                    HttpMethod.GET, request, PentahoReportFiles.class);
            pentahoReportFiles = response.getBody();

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        }
        catch (ScheduleReportException e)
        {
            LOGGER.error("File Properties encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
        }

        return pentahoReportFiles;
    }

    public String buildFilePropertiesUrl()
    {
        return reportsConfig.getServerInternalUrl()
                + (reportsConfig.getServerInternalPort() != null ? ":" + reportsConfig.getServerInternalPort() : "")
                + reportsConfig.getFilePropertiesApi();
    }

    public PentahoReportFiles getPentahoReportFiles()
    {
        return pentahoReportFiles;
    }

    public void setPentahoReportFiles(PentahoReportFiles pentahoReportFiles)
    {
        this.pentahoReportFiles = pentahoReportFiles;
    }

    public ResponseEntity<PentahoReportFiles> getResponse()
    {
        return response;
    }

    public void setResponse(ResponseEntity<PentahoReportFiles> response)
    {
        this.response = response;
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }

    public PentahoReportsConfig getReportsConfig()
    {
        return reportsConfig;
    }

    public void setReportsConfig(PentahoReportsConfig reportsConfig)
    {
        this.reportsConfig = reportsConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
