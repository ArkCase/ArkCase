package com.armedia.acm.plugins.report.service;

import com.armedia.acm.plugins.report.model.ScheduleReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Created by dwu on 6/10/2017.
 */
public class RemoveGeneratedReportRestService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveGeneratedReportRestService.class);
    private ResponseEntity<String> response;
    private String pentahoUrl;
    private String pentahoPort;
    private String removeFileApi;

    public void removeReport(HttpHeaders headers, RestTemplate restTemplate, String id) throws ScheduleReportException
    {
        try
        {
            LOGGER.debug("Start deleting file by id process ....");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<String> request = new HttpEntity<>(id, headers);

            response = restTemplate.exchange(buildRemoveFileUrl(),
                    HttpMethod.PUT, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        } catch (ScheduleReportException e)
        {
            LOGGER.error("File Properties encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String buildRemoveFileUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getRemoveFileApi();
    }

    public ResponseEntity<String> getResponse()
    {
        return response;
    }

    public String getPentahoUrl()
    {
        return pentahoUrl;
    }

    public void setPentahoUrl(String pentahoUrl)
    {
        this.pentahoUrl = pentahoUrl;
    }

    public String getPentahoPort()
    {
        return pentahoPort;
    }

    public void setPentahoPort(String pentahoPort)
    {
        this.pentahoPort = pentahoPort;
    }

    public String getRemoveFileApi()
    {
        return removeFileApi;
    }

    public void setRemoveFileApi(String removeFileApi)
    {
        this.removeFileApi = removeFileApi;
    }
}
