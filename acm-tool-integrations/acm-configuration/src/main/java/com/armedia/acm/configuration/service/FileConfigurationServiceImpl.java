package com.armedia.acm.configuration.service;

import com.armedia.acm.configuration.model.ConfigurationClientConfig;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class FileConfigurationServiceImpl implements FileConfigurationService
{

    private RestTemplate configRestTemplate;

    private ConfigurationClientConfig configurationClientConfig;

    private String customFilesLocation;

    private static final String BRANDING_LOCATION = "branding";

    private static final Logger log = LogManager.getLogger(FileConfigurationServiceImpl.class);

    @Override
    public void moveFileToConfiguration(InputStreamResource file, String filePath)
    {
        try
        {
            log.debug("Sending the file to the config server repository []", filePath);
            configRestTemplate.exchange(
                    configurationClientConfig.getUpdateFilePropertiesEndpoint(), HttpMethod.POST,
                    prepareFileProperties(file, filePath),
                    HttpEntity.class);
        }
        catch (RestClientException e)
        {
            log.warn("Failed to update file due to {}", e.getMessage());
            throw new ConfigurationPropertyException("Failed to update configuration");
        }
    }

    @Override
    public void getFileFromConfiguration(String fileName, String customFilesLocation) throws IOException
    {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Object> entity = new HttpEntity<>("body", headers);

        ResponseEntity<Resource> exchange = configRestTemplate.exchange(
                configurationClientConfig.getConfigurationUrl() + "/" + configurationClientConfig.getDefaultApplicationName() + "/"
                        + configurationClientConfig.getActiveProfile() + "/*/" + BRANDING_LOCATION + "/" + fileName,
                HttpMethod.GET, entity,
                Resource.class);

        File logoFile = new File(customFilesLocation + "/" + BRANDING_LOCATION + "/" + fileName);

        FileUtils.copyInputStreamToFile(exchange.getBody().getInputStream(), logoFile);

    }

    @JmsListener(destination = "VirtualTopic.ConfigFileUpdated", containerFactory = "jmsTopicListenerContainerFactory")
    public void downloadFileFromConfiguration(Message message) throws IOException
    {
        getFileFromConfiguration(message.getPayload().toString(), customFilesLocation);
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> prepareFileProperties(InputStreamResource file, String fileName)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("Content-disposition", "form-data; name=file; filename=" + file);
        HttpEntity<Resource[]> doc = new HttpEntity(file, headerMap);

        LinkedMultiValueMap<String, Object> multipartReqMap = setStringObjectLinkedMultiValueMap(fileName, doc);

        HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);

        log.debug("HttpEntity for updating config file is created");

        return reqEntity;
    }

    private LinkedMultiValueMap<String, Object> setStringObjectLinkedMultiValueMap(String fileName, HttpEntity<Resource[]> doc)
    {
        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
        multipartReqMap.add("file", doc);
        multipartReqMap.add("fileName", fileName);
        return multipartReqMap;
    }

    public void setCustomFilesLocation(String customFilesLocation)
    {
        this.customFilesLocation = customFilesLocation;
    }

    public void setConfigurationClientConfig(ConfigurationClientConfig configurationClientConfig)
    {
        this.configurationClientConfig = configurationClientConfig;
    }

    public void setConfigRestTemplate(RestTemplate configRestTemplate)
    {
        this.configRestTemplate = configRestTemplate;
    }
}
