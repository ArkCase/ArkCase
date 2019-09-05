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

import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.ScheduleReportException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Created by dwu on 6/9/2017.
 */
public class PentahoDownloadGeneratedReportService
{
    private static final Logger LOGGER = LogManager.getLogger(PentahoDownloadGeneratedReportService.class);
    private ResponseEntity<Resource> response;
    private PentahoReportsConfig reportsConfig;

    public void downloadReport(HttpHeaders headers, RestTemplate restTemplate, String fileName)
            throws ScheduleReportException
    {
        try
        {
            LOGGER.debug("Start download generated report process ....");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<String> request = new HttpEntity<>(headers);

            response = restTemplate.exchange(
                    buildDownloadUrl(fileName),
                    HttpMethod.GET, request, Resource.class, "1");

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        }
        catch (ScheduleReportException e)
        {
            LOGGER.error("download encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String buildDownloadUrl(String fileName)
    {
        return reportsConfig.getServerInternalUrl()
                + ((reportsConfig.getServerInternalPort() != null ? ":" + reportsConfig.getServerInternalPort() : ""))
                + reportsConfig.getDownloadApi().replace("{reportFileName}", fileName);
    }

    public ResponseEntity<Resource> getResponse()
    {
        return response;
    }

    public PentahoReportsConfig getReportsConfig()
    {
        return reportsConfig;
    }

    public void setReportsConfig(PentahoReportsConfig reportsConfig)
    {
        this.reportsConfig = reportsConfig;
    }
}
