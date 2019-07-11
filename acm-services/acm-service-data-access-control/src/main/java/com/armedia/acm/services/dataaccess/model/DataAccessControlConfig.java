package com.armedia.acm.services.dataaccess.model;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import org.springframework.beans.factory.annotation.Value;

public class DataAccessControlConfig
{
    @JsonProperty("dac.batchUpdateBasedOnLastModifiedEnabled")
    @Value("${dac.batchUpdateBasedOnLastModifiedEnabled}")
    private Boolean batchUpdateBasedOnLastModifiedEnabled;

    @JsonProperty("dac.batchUpdateBatchSize")
    @Value("${dac.batchUpdateBatchSize}")
    private Integer batchUpdateBatchSize;

    @JsonProperty("dac.enableDocumentACL")
    @Value("${dac.enableDocumentACL}")
    private Boolean enableDocumentACL;

    @JsonProperty("dac.fallbackExpression.getObject")
    @Value("${dac.fallbackExpression.getObject}")
    private String fallbackGetObjectExpression;

    @JsonProperty("dac.fallbackExpression.editObject")
    @Value("${dac.fallbackExpression.editObject}")
    private String fallbackEditObjectExpression;

    @JsonProperty("dac.fallbackExpression.insertObject")
    @Value("${dac.fallbackExpression.insertObject}")
    private String fallbackInsertObjectExpression;

    @JsonProperty("dac.fallbackExpression.deleteObject")
    @Value("${dac.fallbackExpression.deleteObject}")
    private String fallbackDeleteObjectExpression;

    @JsonProperty("dac.includeDenyAccessFilter")
    @Value("${dac.includeDenyAccessFilter}")
    private Boolean includeDenyAccessFilter;

    public Boolean getBatchUpdateBasedOnLastModifiedEnabled()
    {
        return batchUpdateBasedOnLastModifiedEnabled;
    }

    public void setBatchUpdateBasedOnLastModifiedEnabled(Boolean batchUpdateBasedOnLastModifiedEnabled)
    {
        this.batchUpdateBasedOnLastModifiedEnabled = batchUpdateBasedOnLastModifiedEnabled;
    }

    public Integer getBatchUpdateBatchSize()
    {
        return batchUpdateBatchSize;
    }

    public void setBatchUpdateBatchSize(Integer batchUpdateBatchSize)
    {
        this.batchUpdateBatchSize = batchUpdateBatchSize;
    }

    public Boolean getEnableDocumentACL()
    {
        return enableDocumentACL;
    }

    public void setEnableDocumentACL(Boolean enableDocumentACL)
    {
        this.enableDocumentACL = enableDocumentACL;
    }

    public String getFallbackGetObjectExpression()
    {
        return fallbackGetObjectExpression;
    }

    public void setFallbackGetObjectExpression(String fallbackGetObjectExpression)
    {
        this.fallbackGetObjectExpression = fallbackGetObjectExpression;
    }

    public String getFallbackEditObjectExpression()
    {
        return fallbackEditObjectExpression;
    }

    public void setFallbackEditObjectExpression(String fallbackEditObjectExpression)
    {
        this.fallbackEditObjectExpression = fallbackEditObjectExpression;
    }

    public String getFallbackInsertObjectExpression()
    {
        return fallbackInsertObjectExpression;
    }

    public void setFallbackInsertObjectExpression(String fallbackInsertObjectExpression)
    {
        this.fallbackInsertObjectExpression = fallbackInsertObjectExpression;
    }

    public String getFallbackDeleteObjectExpression()
    {
        return fallbackDeleteObjectExpression;
    }

    public void setFallbackDeleteObjectExpression(String fallbackDeleteObjectExpression)
    {
        this.fallbackDeleteObjectExpression = fallbackDeleteObjectExpression;
    }

    public Boolean getIncludeDenyAccessFilter()
    {
        return includeDenyAccessFilter;
    }

    public void setIncludeDenyAccessFilter(Boolean includeDenyAccessFilter)
    {
        this.includeDenyAccessFilter = includeDenyAccessFilter;
    }
}
