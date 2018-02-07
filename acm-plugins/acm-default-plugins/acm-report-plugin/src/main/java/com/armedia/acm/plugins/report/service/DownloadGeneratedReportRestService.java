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
 * Created by dwu on 6/9/2017.
 */
public class DownloadGeneratedReportRestService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadGeneratedReportRestService.class);
    private ResponseEntity<byte[]> response;
    private String pentahoUrl;
    private String pentahoPort;
    private String downloadApi;

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
                    HttpMethod.GET, request, byte[].class, "1");

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        } catch (ScheduleReportException e)
        {
            LOGGER.error("download encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String buildDownloadUrl(String fileName)
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getDownloadApi().replace("{reportFileName}", fileName);
    }

    public ResponseEntity<byte[]> getResponse()
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

    public String getDownloadApi()
    {
        return downloadApi;
    }

    public void setDownloadApi(String downloadApi)
    {
        this.downloadApi = downloadApi;
    }
}
