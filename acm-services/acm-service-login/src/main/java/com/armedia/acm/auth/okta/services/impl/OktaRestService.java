package com.armedia.acm.auth.okta.services.impl;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import com.armedia.acm.auth.okta.model.OktaConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class OktaRestService
{
    private static final Logger LOGGER = LogManager.getLogger(OktaRestService.class);
    private RestTemplate restTemplate;
    private OktaConfig oktaConfig;

    public <T> ResponseEntity<T> doRestCall(String apiPath, HttpMethod httpMethod, Class<T> responseClass, String body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SSWS " + oktaConfig.getToken());

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        LOGGER.trace("Sending Okta request: [url: {}, entity: {}, method: {}]", oktaConfig.getIdpUrl() + apiPath, entity, httpMethod);
        return restTemplate.exchange(oktaConfig.getIdpUrl() + apiPath, httpMethod, entity, responseClass);
    }

    public <T> ResponseEntity<T> doRestHref(String href, HttpMethod httpMethod, Class<T> responseClass, String body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SSWS " + oktaConfig.getToken());

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(href, httpMethod, entity, responseClass);
    }

    public RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public OktaConfig getOktaConfig()
    {
        return oktaConfig;
    }

    public void setOktaConfig(OktaConfig oktaConfig)
    {
        this.oktaConfig = oktaConfig;
    }
}
