package com.armedia.acm.pentaho.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
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
