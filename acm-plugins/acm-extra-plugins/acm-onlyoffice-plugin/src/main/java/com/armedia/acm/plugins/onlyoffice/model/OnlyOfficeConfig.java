package com.armedia.acm.plugins.onlyoffice.model;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import org.springframework.beans.factory.annotation.Value;

public class OnlyOfficeConfig
{
    @Value("${onlyoffice.plugin.files.docservice.url.api}")
    private String documentServerUrlApi;

    @Value("${onlyoffice.plugin.arkcase.baseurl}")
    private String arkcaseBaseUrl;

    @Value("${onlyoffice.plugin.enabled}")
    private Boolean pluginEnabled;

    @Value("${onlyoffice.plugin.jwt.inbound.enabled}")
    private Boolean inboundVerifyEnabled;

    @Value("${onlyoffice.plugin.jwt.inbound.key}")
    private String jwtInboundKey;

    @Value("${onlyoffice.plugin.jwt.inbound.truststore}")
    private String jwtInboundTrustStore;

    @Value("${onlyoffice.plugin.history.folder_path}")
    private String historyFolderPath;

    @Value("${onlyoffice.plugin.history.date_format}")
    private String documentHistoryDateFormat;

    @Value("${onlyoffice.plugin.jwt.outbound.algorithm}")
    private String jwtOutboundAlgorithm;

    @Value("${onlyoffice.plugin.jwt.outbound.key}")
    private String jwtOutboundKey;

    @Value("${onlyoffice.plugin.jwt.outbound.keystore}")
    private String jwtOutboundKeyStore;

    @Value("${onlyoffice.plugin.jwt.outbound.certificate_alias}")
    private String jwtOutboundCertificateAlias;

    @Value("${onlyoffice.plugin.jwt.outbound.enabled}")
    private Boolean outboundSignEnabled;

    @Value("${onlyoffice.plugin.config.view.type}")
    private String configViewType;

    @Value("${onlyoffice.plugin.config.view.height}")
    private String configViewHeight;

    @Value("${onlyoffice.plugin.config.view.width}")
    private String configViewWidth;

    private static final String ONLY_OFFICE_PLUGIN_NAME = "ONLY_OFFICE";

    public String getDocumentServerUrlApi()
    {
        return documentServerUrlApi;
    }

    public void setDocumentServerUrlApi(String documentServerUrlApi)
    {
        this.documentServerUrlApi = documentServerUrlApi;
    }

    public String getArkcaseBaseUrl()
    {
        return arkcaseBaseUrl;
    }

    public void setArkcaseBaseUrl(String arkcaseBaseUrl)
    {
        this.arkcaseBaseUrl = arkcaseBaseUrl;
    }

    public Boolean isPluginEnabled()
    {
        return pluginEnabled;
    }

    public void setPluginEnabled(Boolean pluginEnabled)
    {
        this.pluginEnabled = pluginEnabled;
    }

    public Boolean isInboundVerifyEnabled()
    {
        return inboundVerifyEnabled;
    }

    public void setInboundVerifyEnabled(Boolean inboundVerifyEnabled)
    {
        this.inboundVerifyEnabled = inboundVerifyEnabled;
    }

    public String getJwtInboundKey()
    {
        return jwtInboundKey;
    }

    public void setJwtInboundKey(String jwtInboundKey)
    {
        this.jwtInboundKey = jwtInboundKey;
    }

    public String getJwtInboundTrustStore()
    {
        return jwtInboundTrustStore;
    }

    public void setJwtInboundTrustStore(String jwtInboundTrustStore)
    {
        this.jwtInboundTrustStore = jwtInboundTrustStore;
    }

    public String getHistoryFolderPath()
    {
        return historyFolderPath;
    }

    public void setHistoryFolderPath(String historyFolderPath)
    {
        this.historyFolderPath = historyFolderPath;
    }

    public String getDocumentHistoryDateFormat()
    {
        return documentHistoryDateFormat;
    }

    public void setDocumentHistoryDateFormat(String documentHistoryDateFormat)
    {
        this.documentHistoryDateFormat = documentHistoryDateFormat;
    }

    public String getJwtOutboundAlgorithm()
    {
        return jwtOutboundAlgorithm;
    }

    public void setJwtOutboundAlgorithm(String jwtOutboundAlgorithm)
    {
        this.jwtOutboundAlgorithm = jwtOutboundAlgorithm;
    }

    public String getJwtOutboundKey()
    {
        return jwtOutboundKey;
    }

    public void setJwtOutboundKey(String jwtOutboundKey)
    {
        this.jwtOutboundKey = jwtOutboundKey;
    }

    public String getJwtOutboundKeyStore()
    {
        return jwtOutboundKeyStore;
    }

    public void setJwtOutboundKeyStore(String jwtOutboundKeyStore)
    {
        this.jwtOutboundKeyStore = jwtOutboundKeyStore;
    }

    public String getJwtOutboundCertificateAlias()
    {
        return jwtOutboundCertificateAlias;
    }

    public void setJwtOutboundCertificateAlias(String jwtOutboundCertificateAlias)
    {
        this.jwtOutboundCertificateAlias = jwtOutboundCertificateAlias;
    }

    public Boolean isOutboundSignEnabled()
    {
        return outboundSignEnabled;
    }

    public void setOutboundSignEnabled(Boolean outboundSignEnabled)
    {
        this.outboundSignEnabled = outboundSignEnabled;
    }

    public String getConfigViewType()
    {
        return configViewType;
    }

    public void setConfigViewType(String configViewType)
    {
        this.configViewType = configViewType;
    }

    public String getConfigViewHeight()
    {
        return configViewHeight;
    }

    public void setConfigViewHeight(String configViewHeight)
    {
        this.configViewHeight = configViewHeight;
    }

    public String getConfigViewWidth()
    {
        return configViewWidth;
    }

    public void setConfigViewWidth(String configViewWidth)
    {
        this.configViewWidth = configViewWidth;
    }

    public static String getOnlyOfficePluginName()
    {
        return ONLY_OFFICE_PLUGIN_NAME;
    }
}
