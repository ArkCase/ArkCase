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

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.armedia.acm.services.users.service.AcmOAuth2AccessTokenService;
import com.armedia.acm.tool.zylab.model.CreateMatterRequest;
import com.armedia.acm.tool.zylab.model.MatterDTO;
import com.armedia.acm.tool.zylab.model.MatterTemplateDTO;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class ZylabRestClientImpl implements ZylabRestClient
{

    public static final String ZYLAB_REVIEW_CLIENT_VERSION_HEADER = "X-ZyReviewClientVersion";
    private transient final Logger log = LogManager.getLogger(getClass());
    private ZylabIntegrationConfig zylabIntegrationConfig;
    private AcmOAuth2AccessTokenService acmOAuth2AccessTokenService;
    private RestTemplate zylabRestTemplate;

    @Override
    public MatterDTO createMatter(CreateMatterRequest createMatterRequest)
    {
        if (createMatterRequest == null || StringUtils.isBlank(createMatterRequest.getMatterName())
                || createMatterRequest.getMatterTemplateId() < 1)
        {
            log.error("Create Matter Request is invalid");
            throw new IllegalStateException("Create Matter Data needed");
        }

        return getAcmOAuth2AccessTokenService().executeAuthenticatedRemoteAction(zylabIntegrationConfig.getoAuth2Credentials(),
                accessToken -> {
                    String createMatterURL = zylabIntegrationConfig.getBaseUrl() + zylabIntegrationConfig.getCreateMatterPath();
                    HttpHeaders headers = createZylabCommonHeaders(accessToken.getValue());
                    ResponseEntity<MatterDTO> response = zylabRestTemplate.exchange(createMatterURL, HttpMethod.POST,
                            new HttpEntity<>(createMatterRequest, headers), MatterDTO.class);
                    return response.getBody();
                });
    }

    @Override
    public List<MatterTemplateDTO> getMatterTemplates()
    {
        return getAcmOAuth2AccessTokenService().executeAuthenticatedRemoteAction(zylabIntegrationConfig.getoAuth2Credentials(),
                accessToken -> {
                    String getAllMatterTemplatesURL = zylabIntegrationConfig.getBaseUrl()
                            + zylabIntegrationConfig.getGetMatterTemplatesPath();
                    HttpHeaders headers = ZylabRestClientImpl.this.createZylabCommonHeaders(accessToken.getValue());
                    ResponseEntity<MatterTemplateDTO[]> response = zylabRestTemplate.exchange(getAllMatterTemplatesURL, HttpMethod.GET,
                            new HttpEntity<>(headers), MatterTemplateDTO[].class);
                    return Arrays.asList(response.getBody());
                });
    }

    @Override
    public InputStream getProductionFiles(long matterId, String productionKey)
    {
        return getAcmOAuth2AccessTokenService().executeAuthenticatedRemoteAction(zylabIntegrationConfig.getoAuth2Credentials(),
                accessToken -> {
                    String downloadProductionURL = zylabIntegrationConfig.getBaseUrl() + zylabIntegrationConfig.getDownloadProductionPath();

                    Map<String, String> uriParameters = new HashMap<>();
                    uriParameters.put("matterId", String.valueOf(matterId));
                    uriParameters.put("productionKey", productionKey);

                    UriComponents uriComponents = UriComponentsBuilder
                            .fromUriString(downloadProductionURL)
                            .build()
                            .expand(uriParameters);

                    HttpHeaders headers = createZylabCommonHeaders(accessToken.getValue());

                    ResponseEntity<InputStream> response = zylabRestTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET,
                            new HttpEntity<>(headers), InputStream.class);

                    return response.getBody();
                });
    }

    private HttpHeaders createZylabCommonHeaders(String bearerToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(ZYLAB_REVIEW_CLIENT_VERSION_HEADER, "Arkcase");
        return headers;
    }

    public ZylabIntegrationConfig getZylabIntegrationConfig()
    {
        return zylabIntegrationConfig;
    }

    public void setZylabIntegrationConfig(ZylabIntegrationConfig zylabIntegrationConfig)
    {
        this.zylabIntegrationConfig = zylabIntegrationConfig;
    }

    public AcmOAuth2AccessTokenService getAcmOAuth2AccessTokenService()
    {
        return acmOAuth2AccessTokenService;
    }

    public void setAcmOAuth2AccessTokenService(AcmOAuth2AccessTokenService acmOAuth2AccessTokenService)
    {
        this.acmOAuth2AccessTokenService = acmOAuth2AccessTokenService;
    }

    public RestTemplate getZylabRestTemplate()
    {
        return zylabRestTemplate;
    }

    public void setZylabRestTemplate(RestTemplate zylabRestTemplate)
    {
        this.zylabRestTemplate = zylabRestTemplate;
    }
}
