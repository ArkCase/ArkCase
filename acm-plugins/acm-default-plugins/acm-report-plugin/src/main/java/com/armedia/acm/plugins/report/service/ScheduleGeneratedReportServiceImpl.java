package com.armedia.acm.plugins.report.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.report.model.FileProperties;
import com.armedia.acm.plugins.report.model.ReportFiles;
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
public class ScheduleGeneratedReportServiceImpl implements AcmSchedulableBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleGeneratedReportServiceImpl.class);
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private DownloadGeneratedReportRestService downloadService;
    private FilePropertiesRestService filePropertiesRestService;
    private RemoveGeneratedReportRestService removeGeneratedReportRestService;
    private UploadGeneratedReportService uploadService;
    private ReportFiles reportFiles;
    private String pentahoUser;
    private String pentahoPassword;

    @Override
    public void executeTask()
    {
        //create credential for auth
        createCredentialHeaders();
        restTemplate = new RestTemplate();

        //get generate report metadata
        reportFiles = getFilePropertiesRestService().consumeXML(headers, restTemplate);
        if (reportFiles != null && reportFiles.getFilePropertiesList() != null)
        {
            reportFiles.getFilePropertiesList().forEach(fileProperties ->
            {
                try
                {
                    //download generated report
                    LOGGER.info("Downloading report [{}] from Pentaho", fileProperties.getName());
                    getDownloadService().downloadReport(headers, restTemplate, fileProperties.getName());

                    //upload report to REPs
                    EcmFile reportFile = uploadReportToArkCase(fileProperties);

                    //remove generated report by file id
                    getRemoveGeneratedReportRestService().removeReport(headers, restTemplate, fileProperties.getId());

                    LOGGER.info("Successfully uploaded scheduled report [{}] to REPS", reportFile.getFileName());
                } catch (Exception e)
                {
                    LOGGER.error("Failed to upload generated report to REPS", e);
                }
            });
        } else
        {
            LOGGER.info("Did not find any scheduled report output files to download");
        }
    }

    private EcmFile uploadReportToArkCase(FileProperties fileProperties) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        byte[] data = getDownloadService().getResponse().getBody();
        InputStream inputStream = new ByteArrayInputStream(data);
        return getUploadService().uploadReport(inputStream, fileProperties);
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

    public FilePropertiesRestService getFilePropertiesRestService()
    {
        return filePropertiesRestService;
    }

    public void setFilePropertiesRestService(FilePropertiesRestService filePropertiesRestService)
    {
        this.filePropertiesRestService = filePropertiesRestService;
    }

    public DownloadGeneratedReportRestService getDownloadService()
    {
        return downloadService;
    }

    public void setDownloadService(DownloadGeneratedReportRestService downloadService)
    {
        this.downloadService = downloadService;
    }

    public RemoveGeneratedReportRestService getRemoveGeneratedReportRestService()
    {
        return removeGeneratedReportRestService;
    }

    public void setRemoveGeneratedReportRestService(RemoveGeneratedReportRestService removeGeneratedReportRestService)
    {
        this.removeGeneratedReportRestService = removeGeneratedReportRestService;
    }

    public UploadGeneratedReportService getUploadService()
    {
        return uploadService;
    }

    public void setUploadService(UploadGeneratedReportService uploadService)
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
