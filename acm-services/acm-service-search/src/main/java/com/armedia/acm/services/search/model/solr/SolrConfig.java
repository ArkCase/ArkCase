package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

public class SolrConfig implements InitializingBean
{
    private static final Logger LOGGER = LogManager.getLogger(SolrConfig.class);

    @JsonProperty("solr.host")
    @Value("${solr.host}")
    private String host;

    @JsonProperty("solr.port")
    @Value("${solr.port}")
    private Integer port;

    @JsonProperty("solr.contextRoot")
    @Value("${solr.contextRoot}")
    private String contextRoot;

    @JsonProperty("solr.clientType")
    @Value("${solr.clientType}")
    private SolrClientType clientType;

    @JsonProperty("solr.protocol")
    @Value("${solr.protocol}")
    private String protocol;

    @JsonProperty("solr.advancedsearch.core")
    @Value("${solr.advancedsearch.core}")
    private String advancedSearchCore;

    @JsonProperty("solr.omitHeader")
    @Value("${solr.omitHeader}")
    private boolean omitHeader;

    @JsonProperty("solr.updateHandler")
    @Value("${solr.updateHandler}")
    private String updateHandler;

    @JsonProperty("solr.contentFileHandler")
    @Value("${solr.contentFileHandler}")
    private String contentFileHandler;

    @JsonProperty("solr.searchHandler")
    @Value("${solr.searchHandler}")
    private String searchHandler;

    @JsonProperty("solr.enableBatchUpdateBasedOnLastModified")
    @Value("${solr.enableBatchUpdateBasedOnLastModified}")
    private Boolean enableBatchUpdateBasedOnLastModified;

    @JsonProperty("solr.batchUpdateBatchSize")
    @Value("${solr.batchUpdateBatchSize}")
    private Integer batchUpdateBatchSize;

    @JsonProperty("solr.enableContentFileIndexing")
    @Value("${solr.enableContentFileIndexing}")
    private Boolean enableContentFileIndexing;

    @JsonProperty("solr.suggestHandler")
    @Value("${solr.suggestHandler}")
    private String suggestHandler;

    // Cloud only
    @JsonProperty("solr.parallelUpdates")
    @Value("${solr.parallelUpdates}")
    private boolean parallelUpdates;

    @JsonProperty("solr.zkHosts")
    @Value("${solr.zkHosts}")
    private String zkHosts;

    /**
     * Defines the connection timeout in milliseconds to connnect to
     * a Solr server.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    @JsonProperty("solr.connectionTimeout")
    @Value("${solr.connectionTimeout}")
    private int connectionTimeout;

    /**
     * Defines the socket timeout in milliseconds which will be the read timeout.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */

    @JsonProperty("solr.socketTimeout")
    @Value("${solr.socketTimeout}")
    private int socketTimeout;

    @Override
    public void afterPropertiesSet()
    {
        validate();
    }

    /**
     * Validate Solr configuration
     */
    protected void validate()
    {
        Assert.hasText(getProtocol(), "Invalid Solr configuration, no protocol specified");
        Assert.hasText(getHost(), "Invalid Solr configuration, no host specified");
        Assert.hasText(getContextRoot(), "Invalid Solr configuration, no context specified");
        Assert.hasText(getAdvancedSearchCore(), "Invalid Solr configuration, no AdvancedSearch Core/Collection specified");
        Assert.hasText(getSearchHandler(), "Invalid Solr configuration, no search handler specified");
        Assert.hasText(getUpdateHandler(), "Invalid Solr configuration, no update handler specified");
        Assert.hasText(getSuggestHandler(), "Invalid Solr configuration, no suggest handler specified");
        Assert.hasText(getContentFileHandler(), "Invalid Solr configuration, no content file specified");
        Assert.notNull(getClientType(), "Invalid Solr configuration, no client type specified");
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getContextRoot()
    {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot)
    {
        this.contextRoot = contextRoot;
    }

    public String getAdvancedSearchCore()
    {
        return advancedSearchCore;
    }

    public void setAdvancedSearchCore(String advancedSearchCore)
    {
        this.advancedSearchCore = advancedSearchCore;
    }

    public String getUpdateHandler()
    {
        return updateHandler;
    }

    public void setUpdateHandler(String updateHandler)
    {
        this.updateHandler = updateHandler;
    }

    public String getContentFileHandler()
    {
        return contentFileHandler;
    }

    public void setContentFileHandler(String contentFileHandler)
    {
        this.contentFileHandler = contentFileHandler;
    }

    public String getSearchHandler()
    {
        return searchHandler;
    }

    public void setSearchHandler(String searchHandler)
    {
        this.searchHandler = searchHandler;
    }

    public Boolean getEnableBatchUpdateBasedOnLastModified()
    {
        return enableBatchUpdateBasedOnLastModified;
    }

    public void setEnableBatchUpdateBasedOnLastModified(Boolean enableBatchUpdateBasedOnLastModified)
    {
        this.enableBatchUpdateBasedOnLastModified = enableBatchUpdateBasedOnLastModified;
    }

    public Integer getBatchUpdateBatchSize()
    {
        return batchUpdateBatchSize;
    }

    public void setBatchUpdateBatchSize(Integer batchUpdateBatchSize)
    {
        this.batchUpdateBatchSize = batchUpdateBatchSize;
    }

    public Boolean getEnableContentFileIndexing()
    {
        return enableContentFileIndexing;
    }

    public void setEnableContentFileIndexing(Boolean enableContentFileIndexing)
    {
        this.enableContentFileIndexing = enableContentFileIndexing;
    }

    public String getSuggestHandler()
    {
        return suggestHandler;
    }

    public void setSuggestHandler(String suggestHandler)
    {
        this.suggestHandler = suggestHandler;
    }

    public SolrClientType getClientType()
    {
        return clientType;
    }

    public void setClientType(SolrClientType clientType)
    {
        this.clientType = clientType;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public boolean isOmitHeader()
    {
        return omitHeader;
    }

    public void setOmitHeader(boolean omitHeader)
    {
        this.omitHeader = omitHeader;
    }

    public boolean isParallelUpdates()
    {
        return parallelUpdates;
    }

    public void setParallelUpdates(boolean parallelUpdates)
    {
        this.parallelUpdates = parallelUpdates;
    }

    public String getZkHosts()
    {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts)
    {
        this.zkHosts = zkHosts;
    }

    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }
}
