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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

public class PentahoReportUrl implements ReportUrl
{
    private PentahoReportsConfig reportsConfig;
    private Logger log = LogManager.getLogger(getClass());

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
        String serverFormUrl = reportsConfig.getServerUrl();
        builder.append(serverFormUrl);
        Integer serverFormPort = reportsConfig.getServerPort();
        if (serverFormPort != null)
        {
            builder.append(":").append(serverFormPort);
        }
        String pathStr = reportsConfig.getReports().get(reportName);
        builder.append(pathStr);
        String path = builder.toString();
        log.debug("getReportUrlPath(): [{}]", path);
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
        String serverFormUrl = reportsConfig.getServerUrl();
        String serverFormPort = String.valueOf(reportsConfig.getServerPort());
        for (Map.Entry<String, String> entry : reportsConfig.getReports().entrySet())
        {
            String keyStr = entry.getKey();

            ReportName enumName = ReportName.valueOf(keyStr);
            urlsMap.put(enumName.getDisplayName(), formulateUrl(serverFormUrl, serverFormPort, entry.getValue()));
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

        String serverFormUrl = reportsConfig.getServerInternalUrl();

        builder.append(serverFormUrl);

        Integer serverFormPort = reportsConfig.getServerInternalPort();
        if (serverFormPort != null)
        {
            builder.append(":").append(serverFormPort);
        }

        String pentahoReportsUrl = reportsConfig.getReportsUrl();
        builder.append(pentahoReportsUrl);

        String url = builder.toString();
        log.debug("getReportsUrl(): [{}]", url);
        return url;
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
