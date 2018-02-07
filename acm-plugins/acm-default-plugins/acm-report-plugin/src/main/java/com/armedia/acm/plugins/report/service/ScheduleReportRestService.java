package com.armedia.acm.plugins.report.service;

import com.armedia.acm.plugins.report.model.ScheduleReportException;
import org.apache.commons.ssl.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by dwu on 6/11/2017.
 */
public class ScheduleReportRestService implements ScheduleReportService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleReportRestService.class);
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private ResponseEntity<String> response;
    private String pentahoUser;
    private String pentahoPassword;
    private String pentahoUrl;
    private String pentahoPort;
    private String scheduleApi;
    private String retrieveSchedulesApi;
    private String deleteScheduleApi;

    private void createCredentialHeaders()
    {
        String plainCreds = getPentahoUser() + ":" + getPentahoPassword();
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
    }

    @Override
    public void scheduleReport(String jsonString) throws ScheduleReportException
    {
        try
        {
            LOGGER.debug("Start scheduling the selected report process ....");
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonString, headers);

            response = restTemplate.exchange(buildScheduleUrl(),
                    HttpMethod.POST, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

        } catch (ScheduleReportException e)
        {
            LOGGER.debug("Scheduling report encountered error. May be Rest call connection or file not found error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String retrieveSchedules() throws ScheduleReportException
    {
        try
        {
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            response = restTemplate.exchange(buildRetrieveSchedulesUrl(),
                    HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

            return response.getBody();
        } catch (ScheduleReportException e)
        {
            LOGGER.error("Retrieve schedules rest call error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deleteSchedule(String scheduleId) throws ScheduleReportException
    {
        try
        {
            createCredentialHeaders();
            restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject deleteJson = new JSONObject();
            deleteJson.put("jobId", scheduleId);
            HttpEntity<String> httpEntity = new HttpEntity<>(deleteJson.toString(), headers);

            response = restTemplate.exchange(buildDeleteScheduleUrl(),
                    HttpMethod.DELETE, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new ScheduleReportException(response.getStatusCode().toString());
            }

            return response.getBody();
        } catch (ScheduleReportException e)
        {
            LOGGER.error("Delete schedule rest call error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String buildDeleteScheduleUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getDeleteScheduleApi();
    }

    public String buildRetrieveSchedulesUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getRetrieveSchedulesApi();
    }

    public String buildScheduleUrl()
    {
        return getPentahoUrl() + ((getPentahoPort() != null && !getPentahoPort().isEmpty()) ? ":" + getPentahoPort() : "") + getScheduleApi();
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

    public String getScheduleApi()
    {
        return scheduleApi;
    }

    public void setScheduleApi(String scheduleApi)
    {
        this.scheduleApi = scheduleApi;
    }

    public String getRetrieveSchedulesApi()
    {
        return retrieveSchedulesApi;
    }

    public void setRetrieveSchedulesApi(String retrieveSchedulesApi)
    {
        this.retrieveSchedulesApi = retrieveSchedulesApi;
    }

    public String getDeleteScheduleApi()
    {
        return deleteScheduleApi;
    }

    public void setDeleteScheduleApi(String deleteScheduleApi)
    {
        this.deleteScheduleApi = deleteScheduleApi;
    }
}
