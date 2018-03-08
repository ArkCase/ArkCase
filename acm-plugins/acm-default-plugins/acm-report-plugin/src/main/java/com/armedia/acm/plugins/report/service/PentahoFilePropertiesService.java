package com.armedia.acm.plugins.report.service;

import com.armedia.acm.plugins.report.model.PentahoReportFiles;
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
public class PentahoFilePropertiesService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PentahoFilePropertiesService.class);
    private PentahoReportFiles pentahoReportFiles;
    private ResponseEntity<PentahoReportFiles> response;
    private String pentahoUrl;
    private String pentahoPort;
    private String filePropertiesApi;

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

        } catch (ScheduleReportException e)
        {
            LOGGER.error("File Properties encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
        }

        return pentahoReportFiles;
    }

    public String buildFilePropertiesUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getFilePropertiesApi();
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

    public String getFilePropertiesApi()
    {
        return filePropertiesApi;
    }

    public void setFilePropertiesApi(String filePropertiesApi)
    {
        this.filePropertiesApi = filePropertiesApi;
    }
}
