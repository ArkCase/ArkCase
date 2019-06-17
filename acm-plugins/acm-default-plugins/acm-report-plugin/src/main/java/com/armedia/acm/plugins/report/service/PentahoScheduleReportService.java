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

import org.apache.commons.ssl.Base64;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by dwu on 6/11/2017.
 */
public class PentahoScheduleReportService implements ScheduleReportService
{
    private static final Logger LOGGER = LogManager.getLogger(PentahoScheduleReportService.class);
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private ResponseEntity<String> response;
    private PentahoReportsConfig reportsConfig;

    private void createCredentialHeaders()
    {
        String plainCreds = reportsConfig.getServerUser() + ":" + reportsConfig.getServerPassword();
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
    }

    @Override
    public void scheduleReport(String jsonString) throws ScheduleReportException
    {
        try
        {
            LOGGER.debug("Start scheduling the selected report process ....");
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonString, headers);

            response = restTemplate.exchange(buildScheduleUrl(),
                    HttpMethod.POST, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        }
        catch (ScheduleReportException e)
        {
            LOGGER.debug("Scheduling report encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String retrieveSchedules() throws ScheduleReportException
    {
        try
        {
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            response = restTemplate.exchange(buildRetrieveSchedulesUrl(),
                    HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

            return response.getBody();
        }
        catch (ScheduleReportException e)
        {
            LOGGER.error("Retrieve schedules rest call error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deleteSchedule(String scheduleId) throws ScheduleReportException
    {
        try
        {
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject deleteJson = new JSONObject();
            deleteJson.put("jobId", scheduleId);
            HttpEntity<String> httpEntity = new HttpEntity<>(deleteJson.toString(), headers);

            response = restTemplate.exchange(buildDeleteScheduleUrl(),
                    HttpMethod.DELETE, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

            return response.getBody();
        }
        catch (ScheduleReportException e)
        {
            LOGGER.error("Delete schedule rest call error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String buildDeleteScheduleUrl()
    {
        return reportsConfig.getServerInternalUrl() +
                ((reportsConfig.getServerInternalPort() != null) ? ":" + reportsConfig.getServerInternalPort() : "")
                + reportsConfig.getDeleteScheduleApi();
    }

    public String buildRetrieveSchedulesUrl()
    {
        return reportsConfig.getServerInternalUrl() +
                ((reportsConfig.getServerInternalPort() != null) ? ":" + reportsConfig.getServerInternalPort() : "")
                + reportsConfig.getRetrieveSchedulesApi();
    }

    public String buildScheduleUrl()
    {

        return reportsConfig.getServerInternalUrl() +
                ((reportsConfig.getServerInternalPort() != null) ? ":" + reportsConfig.getServerInternalPort() : "")
                + reportsConfig.getScheduleApi();
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
