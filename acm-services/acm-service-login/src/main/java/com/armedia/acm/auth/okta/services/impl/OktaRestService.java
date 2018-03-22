package com.armedia.acm.auth.okta.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class OktaRestService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OktaRestService.class);
    private String idpUrl;
    private String token;
    private RestTemplate restTemplate;

    public <T> ResponseEntity<T> doRestCall(String apiPath, HttpMethod httpMethod, Class<T> responseClass, String body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SSWS " + token);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        LOGGER.trace("Sending Okta request: [url: {}, entity: {}, method: {}]", idpUrl + apiPath, entity, httpMethod);
        return restTemplate.exchange(idpUrl + apiPath, httpMethod, entity, responseClass);
    }

    public <T> ResponseEntity<T> doRestHref(String href, HttpMethod httpMethod, Class<T> responseClass, String body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SSWS " + token);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(href, httpMethod, entity, responseClass);
    }

    public String getIdpUrl()
    {
        return idpUrl;
    }

    public void setIdpUrl(String idpUrl)
    {
        this.idpUrl = idpUrl;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
}