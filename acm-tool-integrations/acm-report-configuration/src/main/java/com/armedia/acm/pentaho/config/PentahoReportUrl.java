package com.armedia.acm.pentaho.config;

import com.armedia.acm.report.config.ReportUrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class PentahoReportUrl implements ReportUrl
{
    private static final String REPORT_SERVER_URL = "PENTAHO_SERVER_URL";
    private static final String REPORT_SERVER_PORT = "PENTAHO_SERVER_PORT";
    private static final String PENTAHO_SERVER_INTERNAL_URL = "PENTAHO_SERVER_INTERNAL_URL";
    private static final String PENTAHO_SERVER_INTERNAL_PORT = "PENTAHO_SERVER_INTERNAL_PORT";
    private static final String REPORTS_URL = "PENTAHO_REPORTS_URL";
    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * List of form-specific properties.
     */
    private Map<String, Object> reportsProperties;
    private Properties reportServerConfigurationProperties;

    public Map<String, Object> getReportsProperties()
    {
        return reportsProperties;
    }

    public void setReportsProperties(Map<String, Object> reportProperties)
    {
        this.reportsProperties = reportProperties;
    }

    @Override
    public String getNewReportUrl(String reportName)
    {
        return getReportUrlPath(reportName);
    }

    /**
     * Url path for create a new complaint report form in Pentaho.
     *
     * @return
     */
    public String getReportUrlPath(String reportName)
    {
        StringBuilder builder = new StringBuilder();
        String serverFormUrl = getReportServerConfigurationProperties().get(REPORT_SERVER_URL).toString();
        builder.append(serverFormUrl);
        String serverFormPort = getReportServerConfigurationProperties().get(REPORT_SERVER_PORT).toString();
        if (serverFormPort != null && !serverFormPort.trim().isEmpty())
        {
            builder.append(":").append(serverFormPort);
        }
        String pathStr = getReportsProperties().get(reportName).toString();
        builder.append(pathStr);
        String path = builder.toString();
        log.debug("getReportUrlPath(): " + path);
        return path;
    }

    /**
     * This method get all the report urls in the properties file and
     * return a sorted map.
     *
     * @return
     */
    @Override
    public Map<String, String> getNewReportUrlList()
    {
        Map<String, String> urlsMap = new TreeMap<>();
        String serverFormUrl = getReportServerConfigurationProperties().get(REPORT_SERVER_URL).toString();
        String serverFormPort = getReportServerConfigurationProperties().get(REPORT_SERVER_PORT).toString();

        for (Map.Entry<String, Object> entry : getReportsProperties().entrySet())
        {
            String keyStr = entry.getKey();

            ReportName enumName = ReportName.valueOf(keyStr);
            if (null != enumName)
            {
                urlsMap.put(enumName.getDisplayName(), formulateUrl(serverFormUrl, serverFormPort, entry.getValue()));
            }

        }

        return urlsMap;
    }

    private String formulateUrl(String host, String port, Object path)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(host);
        if (port != null && !port.trim().isEmpty())
        {
            builder.append(":");
            builder.append(port);
        }
        builder.append(path);
        return builder.toString();
    }

    public String getReportsUrl()
    {
        StringBuilder builder = new StringBuilder();

        String serverFormUrl = getReportServerConfigurationProperties().get(PENTAHO_SERVER_INTERNAL_URL).toString();

        builder.append(serverFormUrl);

        String serverFormPort = getReportServerConfigurationProperties().get(PENTAHO_SERVER_INTERNAL_PORT).toString();
        if (serverFormPort != null && !serverFormPort.trim().isEmpty())
        {
            builder.append(":").append(serverFormPort);
        }

        String pentahoReportsUrl = getReportServerConfigurationProperties().get(REPORTS_URL).toString();
        builder.append(pentahoReportsUrl);

        String url = builder.toString();
        log.debug("getReportsUrl(): " + url);

        return url;
    }

    public Properties getReportServerConfigurationProperties()
    {
        return reportServerConfigurationProperties;
    }

    public void setReportServerConfigurationProperties(
            Properties reportServerConfigurationProperties)
    {
        this.reportServerConfigurationProperties = reportServerConfigurationProperties;
    }
}
