package com.armedia.acm.plugins.report.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.report.model.PentahoReportFiles;
import com.armedia.acm.plugins.report.model.Report;
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
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final Logger LOGGER = LoggerFactory.getLogger(PentahoFilePropertiesService.class);
    private PentahoReportFiles pentahoReportFiles;
    private ResponseEntity<PentahoReportFiles> response;
    private String pentahoUrl;
    private String pentahoPort;
    private String filePropertiesApi;
    private PropertyFileManager propertyFileManager;
    private ReportServiceImpl reportService;
    private String reportsPropertyFileLocation;

    public List<Report> getPentahoReports() throws Exception
    {
        List<Report> retval = new ArrayList<>();
        List<Report> reports = null;

        try
        {
            reports = getReportService().getPentahoReports();
        }
        catch (Exception e)
        {
            throw e;
        }

        if (reports != null)
        {
            for (Report report : reports)
            {
                if (!report.isFolder())
                {
                    String acmReportProperty = null;

                    try
                    {
                        acmReportProperty = getPropertyFileManager().load(getReportsPropertyFileLocation(), report.getPropertyName(), null);
                    }
                    catch (Exception e)
                    {
                        LOG.warn("Cannot find property in the report properties file.");
                    }

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

    public List<Report> getPentahoReportsPaged(Integer start, Integer maxRows, String sortDirection) throws Exception
    {
        List<Report> foundReports = getPentahoReports();
        if (sortDirection.contains("DESC"))
        {
            foundReports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER).reversed());
        }
        else
        {
            foundReports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER));
        }

        return foundReports.stream().skip(start).limit(maxRows).collect(Collectors.toList());
    }

    public List<Report> getPentahoReportsByMatchingName(String filterName, Integer start, Integer maxRows, String sortDirection)
            throws Exception
    {
        List<Report> foundReports = getPentahoReports();

        foundReports = foundReports.stream().filter(report -> report.getPropertyName().toLowerCase().contains(filterName.toLowerCase()))
                .collect(Collectors.toList());

        if (sortDirection.contains("DESC"))
        {
            foundReports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER).reversed());
        }
        else
        {
            foundReports.sort(Comparator.comparing(Report::getPropertyName, String.CASE_INSENSITIVE_ORDER));
        }

        return foundReports.stream().skip(start).limit(maxRows).collect(Collectors.toList());
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
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "")
                + getFilePropertiesApi();
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

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public void setReportService(ReportServiceImpl reportService)
    {
        this.reportService = reportService;
    }

    public ReportServiceImpl getReportService()
    {
        return reportService;
    }

    public String getReportsPropertyFileLocation()
    {
        return reportsPropertyFileLocation;
    }

    public void setReportsPropertyFileLocation(String reportsPropertyFileLocation)
    {
        this.reportsPropertyFileLocation = reportsPropertyFileLocation;
    }
}
