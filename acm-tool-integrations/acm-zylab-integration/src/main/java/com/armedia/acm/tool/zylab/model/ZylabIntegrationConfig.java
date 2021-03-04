package com.armedia.acm.tool.zylab.model;

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

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on January, 2021
 */
@JsonSerialize(as = ZylabIntegrationConfig.class)
public class ZylabIntegrationConfig
{

    @JsonProperty("zylabIntegration.enabled")
    @Value("${zylabIntegration.enabled}")
    private Boolean enabled;

    @JsonProperty("zylabIntegration.defaultMatterTemplateId")
    @Value("${zylabIntegration.defaultMatterTemplateId}")
    private Long defaultMatterTemplateId;

    @JsonProperty("zylabIntegration.url")
    @Value("${zylabIntegration.url}")
    private String baseUrl;

    @JsonProperty("zylabIntegration.simpleResourcePath")
    @Value("${zylabIntegration.simpleResourcePath}")
    private String simpleResourcePath;

    @JsonProperty("zylabIntegration.documentReviewPath")
    @Value("${zylabIntegration.documentReviewPath}")
    private String documentReviewPath;

    @JsonProperty("zylabIntegration.matterDashboardPath")
    @Value("${zylabIntegration.matterDashboardPath}")
    private String matterDashboardPath;

    @JsonProperty("zylabIntegration.createMatterPath")
    @Value("${zylabIntegration.createMatterPath}")
    private String createMatterPath;

    @JsonProperty("zylabIntegration.downloadProductionPath")
    @Value("${zylabIntegration.downloadProductionPath}")
    private String downloadProductionPath;

    @JsonProperty("zylabIntegration.openMatterPath")
    @Value("${zylabIntegration.openMatterPath}")
    private String openMatterPath;

    @JsonProperty("zylabIntegration.matterReportsPath")
    @Value("${zylabIntegration.matterReportsPath}")
    private String matterReportsPath;

    @JsonProperty("zylabIntegration.getMatterTemplatesPath")
    @Value("${zylabIntegration.getMatterTemplatesPath}")
    private String getMatterTemplatesPath;

    @JsonProperty("zylabIntegration.viewDocumentPath")
    @Value("${zylabIntegration.viewDocumentPath}")
    private String viewDocumentPath;

    public Boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public Long getDefaultMatterTemplateId()
    {
        return defaultMatterTemplateId;
    }

    public void setDefaultMatterTemplateId(Long defaultMatterTemplateId)
    {
        this.defaultMatterTemplateId = defaultMatterTemplateId;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getSimpleResourcePath()
    {
        return simpleResourcePath;
    }

    public void setSimpleResourcePath(String simpleResourcePath)
    {
        this.simpleResourcePath = simpleResourcePath;
    }

    public String getDocumentReviewPath()
    {
        return documentReviewPath;
    }

    public void setDocumentReviewPath(String documentReviewPath)
    {
        this.documentReviewPath = documentReviewPath;
    }

    public String getMatterDashboardPath()
    {
        return matterDashboardPath;
    }

    public void setMatterDashboardPath(String matterDashboardPath)
    {
        this.matterDashboardPath = matterDashboardPath;
    }

    public String getCreateMatterPath()
    {
        return createMatterPath;
    }

    public void setCreateMatterPath(String createMatterPath)
    {
        this.createMatterPath = createMatterPath;
    }

    public String getDownloadProductionPath()
    {
        return downloadProductionPath;
    }

    public void setDownloadProductionPath(String downloadProductionPath)
    {
        this.downloadProductionPath = downloadProductionPath;
    }

    public String getOpenMatterPath()
    {
        return openMatterPath;
    }

    public void setOpenMatterPath(String openMatterPath)
    {
        this.openMatterPath = openMatterPath;
    }

    public String getMatterReportsPath()
    {
        return matterReportsPath;
    }

    public void setMatterReportsPath(String matterReportsPath)
    {
        this.matterReportsPath = matterReportsPath;
    }

    public String getViewDocumentPath()
    {
        return viewDocumentPath;
    }

    public void setViewDocumentPath(String viewDocumentPath)
    {
        this.viewDocumentPath = viewDocumentPath;
    }

    public String getGetMatterTemplatesPath()
    {
        return getMatterTemplatesPath;
    }

    public void setGetMatterTemplatesPath(String getMatterTemplatesPath)
    {
        this.getMatterTemplatesPath = getMatterTemplatesPath;
    }
}
