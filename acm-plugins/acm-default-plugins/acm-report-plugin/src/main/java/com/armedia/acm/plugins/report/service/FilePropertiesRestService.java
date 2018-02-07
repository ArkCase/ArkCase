package com.armedia.acm.plugins.report.service;

import com.armedia.acm.plugins.report.model.ReportFiles;
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
public class FilePropertiesRestService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FilePropertiesRestService.class);
    private ReportFiles reportFiles;
    ResponseEntity<ReportFiles> response;
    private String pentahoUrl;
    private String pentahoPort;
    private String filePropertiesApi;

    public ReportFiles consumeXML(HttpHeaders headers, RestTemplate restTemplate)
    {
        try
        {
            LOGGER.debug("Start getting generated report metadata process ....");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            HttpEntity<String> request = new HttpEntity<>(headers);

            response = restTemplate.exchange(buildFilePropertiesUrl(),
                    HttpMethod.GET, request, ReportFiles.class);
            reportFiles = response.getBody();

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        } catch (ScheduleReportException e)
        {
            LOGGER.error("File Properties encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
        }

        return reportFiles;
    }

    public String buildFilePropertiesUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getFilePropertiesApi();
    }

    public ReportFiles getReportFiles()
    {
        return reportFiles;
    }

    public void setReportFiles(ReportFiles reportFiles)
    {
        this.reportFiles = reportFiles;
    }

    public ResponseEntity<ReportFiles> getResponse()
    {
        return response;
    }

    public void setResponse(ResponseEntity<ReportFiles> response)
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
