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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.report.model.PentahoFileProperties;
import com.armedia.acm.plugins.report.model.PentahoReportFiles;

import org.apache.commons.ssl.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dwu on 6/9/2017.
 */
public class PentahoScheduleGeneratedReportServiceImpl
{
    private static final Logger LOGGER = LogManager.getLogger(PentahoScheduleGeneratedReportServiceImpl.class);
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private PentahoDownloadGeneratedReportService downloadService;
    private PentahoFilePropertiesService pentahoFilePropertiesService;
    private PentahoRemoveGeneratedReportService pentahoRemoveGeneratedReportService;
    private PentahoUploadGeneratedReportService uploadService;
    private PentahoReportsConfig reportsConfig;

    public void executeTask()
    {
        // create credential for auth
        createCredentialHeaders();
        restTemplate = new RestTemplate();

        // get generate report metadata
        PentahoReportFiles pentahoReportFiles = getPentahoFilePropertiesService().consumeXML(headers, restTemplate);

        if (pentahoReportFiles != null && pentahoReportFiles.getPentahoFilePropertiesList() != null)
        {
            pentahoReportFiles.getPentahoFilePropertiesList().forEach(pentahoFileProperties -> {
                try
                {
                    // download generated report
                    LOGGER.info("Downloading report [{}] from Pentaho", pentahoFileProperties.getName());
                    getDownloadService().downloadReport(headers, restTemplate, pentahoFileProperties.getName());

                    // upload report to ArkCase
                    EcmFile reportFile = uploadReportToArkCase(pentahoFileProperties);

                    // remove generated report by file id
                    getPentahoRemoveGeneratedReportService().removeReport(headers, restTemplate, pentahoFileProperties.getId());

                    LOGGER.info("Successfully uploaded scheduled report [{}] to ArkCase", reportFile.getFileName());
                }
                catch (Exception e)
                {
                    LOGGER.error("Failed to upload generated report to ArkCase", e);
                }
            });
        }
        else
        {
            LOGGER.info("Did not find any scheduled report output files to download");
        }
    }

    private EcmFile uploadReportToArkCase(PentahoFileProperties pentahoFileProperties)
            throws AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        Resource resource = getDownloadService().getResponse().getBody();
        InputStream inputStream;
        try
        {
            inputStream = resource.getInputStream();
        }
        catch (IOException e)
        {
            throw new AcmCreateObjectFailedException("FILE", "Failed to read the report input stream for uploading.", e);
        }
        return getUploadService().uploadReport(inputStream, pentahoFileProperties);
    }

    private void createCredentialHeaders()
    {
        String plainCreds = reportsConfig.getServerUser() + ":" + reportsConfig.getServerPassword();
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
    }

    public PentahoFilePropertiesService getPentahoFilePropertiesService()
    {
        return pentahoFilePropertiesService;
    }

    public void setPentahoFilePropertiesService(PentahoFilePropertiesService pentahoFilePropertiesService)
    {
        this.pentahoFilePropertiesService = pentahoFilePropertiesService;
    }

    public PentahoDownloadGeneratedReportService getDownloadService()
    {
        return downloadService;
    }

    public void setDownloadService(PentahoDownloadGeneratedReportService downloadService)
    {
        this.downloadService = downloadService;
    }

    public PentahoRemoveGeneratedReportService getPentahoRemoveGeneratedReportService()
    {
        return pentahoRemoveGeneratedReportService;
    }

    public void setPentahoRemoveGeneratedReportService(PentahoRemoveGeneratedReportService pentahoRemoveGeneratedReportService)
    {
        this.pentahoRemoveGeneratedReportService = pentahoRemoveGeneratedReportService;
    }

    public PentahoUploadGeneratedReportService getUploadService()
    {
        return uploadService;
    }

    public void setUploadService(PentahoUploadGeneratedReportService uploadService)
    {
        this.uploadService = uploadService;
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
