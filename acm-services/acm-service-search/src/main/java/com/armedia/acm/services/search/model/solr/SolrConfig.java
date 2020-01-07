package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class SolrConfig
{
    @JsonProperty("solr.host")
    @Value("${solr.host}")
    private String host;

    @JsonProperty("solr.port")
    @Value("${solr.port}")
    private Integer port;

    @JsonProperty("solr.contextRoot")
    @Value("${solr.contextRoot}")
    private String contextRoot;

    @JsonProperty("solr.quicksearch.core")
    @Value("${solr.quicksearch.core}")
    private String quickSearchCore;

    @JsonProperty("solr.advancedsearch.core")
    @Value("${solr.advancedsearch.core}")
    private String advancedSearchCore;

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

    public String getQuickSearchCore()
    {
        return quickSearchCore;
    }

    public void setQuickSearchCore(String quickSearchCore)
    {
        this.quickSearchCore = quickSearchCore;
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
}
