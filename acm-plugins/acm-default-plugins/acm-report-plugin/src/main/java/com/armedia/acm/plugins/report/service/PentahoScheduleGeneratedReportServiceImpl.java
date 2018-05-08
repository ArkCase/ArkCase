package com.armedia.acm.plugins.report.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.report.model.PentahoFileProperties;
import com.armedia.acm.plugins.report.model.PentahoReportFiles;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.apache.commons.ssl.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by dwu on 6/9/2017.
 */
public class PentahoScheduleGeneratedReportServiceImpl implements AcmSchedulableBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PentahoScheduleGeneratedReportServiceImpl.class);
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private PentahoDownloadGeneratedReportService downloadService;
    private PentahoFilePropertiesService pentahoFilePropertiesService;
    private PentahoRemoveGeneratedReportService pentahoRemoveGeneratedReportService;
    private PentahoUploadGeneratedReportService uploadService;
    private PentahoReportFiles pentahoReportFiles;
    private String pentahoUser;
    private String pentahoPassword;

    @Override
    public void executeTask()
    {
        // create credential for auth
        createCredentialHeaders();
        restTemplate = new RestTemplate();

        // get generate report metadata
        pentahoReportFiles = getPentahoFilePropertiesService().consumeXML(headers, restTemplate);
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
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        byte[] data = getDownloadService().getResponse().getBody();
        InputStream inputStream = new ByteArrayInputStream(data);
        return getUploadService().uploadReport(inputStream, pentahoFileProperties);
    }

    private void createCredentialHeaders()
    {
        String plainCreds = getPentahoUser() + ":" + getPentahoPassword();
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

    public String getPentahoUser()
    {
        return pentahoUser;
    }

    public void setPentahoUser(String pentahoUser)
    {
        this.pentahoUser = pentahoUser;
    }

    public String getPentahoPassword()
    {
        return pentahoPassword;
    }

    public void setPentahoPassword(String pentahoPassword)
    {
        this.pentahoPassword = pentahoPassword;
    }
}
