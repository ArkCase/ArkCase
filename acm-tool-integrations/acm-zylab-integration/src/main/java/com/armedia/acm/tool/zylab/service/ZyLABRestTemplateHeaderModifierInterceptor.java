package com.armedia.acm.tool.zylab.service;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;

public class ZyLABRestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor
{

    private static final String ZyLAB_REQUEST_VERIFICATION_HEADER = "X-RequestVerificationToken-V2";
    private static final String ZyLAB_REQUEST_VERIFICATION_COOKIE = "__RequestVerificationToken_V2";
    private transient final Logger log = LogManager.getLogger(getClass());
    private RestTemplate zylabRestTemplate;
    private ZylabIntegrationConfig zylabIntegrationConfig;

    /**
     *
     * Intercepts all REST requests from Arkcase to ZyLAB and inserts an additional CSRF token for all non GET requests
     * in order to comply with ZyLAB's APIs. The token is provided by creating a simple GET request to a lightweight
     * resource in ZyLAB's application and the token returned is added both as header and as cookie to the original
     * POST request.
     *
     * @param request
     *            – the request, containing method, URI, and headers
     * @param body
     *            – the body of the request
     * @param execution
     *            – the request execution
     * @return the response
     * @throws IOException
     *             – in case of I/O
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        if (request.getMethod() != HttpMethod.GET)
        {
            String antiForgeryToken = getAntiForgeryToken(request);
            // Add anti forgery token both as header and as cookie. Both are essential for successful POST requests
            request.getHeaders().add(ZyLAB_REQUEST_VERIFICATION_HEADER, antiForgeryToken);
            request.getHeaders().add(HttpHeaders.COOKIE, ZyLAB_REQUEST_VERIFICATION_COOKIE + "=" + antiForgeryToken);
        }
        return execution.execute(request, body);
    }

    private String getAntiForgeryToken(HttpRequest request)
    {
        String simpleResourceUrl = zylabIntegrationConfig.getBaseUrl() + zylabIntegrationConfig.getSimpleResourcePath();

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);

        ResponseEntity<String> response = zylabRestTemplate.exchange(simpleResourceUrl, HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        String setCookieValue = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        if (setCookieValue == null)
        {
            // The cookie might be missing due to a ZyLAB configuration to not request the anti forgery tokens
            log.warn("No CSRF token returned from ZyLAB despite a successful GET request");
            return "";
        }
        else
        {
            return Arrays.stream(setCookieValue.split(";"))
                    .filter(cookie -> cookie.startsWith(ZyLAB_REQUEST_VERIFICATION_COOKIE))
                    .findFirst()
                    .orElse("")
                    .replace(ZyLAB_REQUEST_VERIFICATION_COOKIE + "=", "");
        }
    }

    public RestTemplate getZylabRestTemplate()
    {
        return zylabRestTemplate;
    }

    public void setZylabRestTemplate(RestTemplate zylabRestTemplate)
    {
        this.zylabRestTemplate = zylabRestTemplate;
    }

    public ZylabIntegrationConfig getZylabIntegrationConfig()
    {
        return zylabIntegrationConfig;
    }

    public void setZylabIntegrationConfig(ZylabIntegrationConfig zylabIntegrationConfig)
    {
        this.zylabIntegrationConfig = zylabIntegrationConfig;
    }
}
