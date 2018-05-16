package com.armedia.acm.auth.okta.model.factor;

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

import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FactorVerifyResult extends ErrorResponse
{
    private FactorResult factorResult;
    private String factorMessage;

    @JsonProperty("_links")
    private Map links;

    public FactorResult getFactorResult()
    {
        return factorResult;
    }

    public void setFactorResult(FactorResult factorResult)
    {
        this.factorResult = factorResult;
    }

    public String getFactorMessage()
    {
        return factorMessage;
    }

    public void setFactorMessage(String factorMessage)
    {
        this.factorMessage = factorMessage;
    }

    public Map getLinks()
    {
        return links;
    }

    public void setLinks(Map links)
    {
        this.links = links;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("factorResult", factorResult)
                .append("factorMessage", factorMessage)
                .append("links", links)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        FactorVerifyResult that = (FactorVerifyResult) o;

        return new EqualsBuilder()
                .append(factorResult, that.factorResult)
                .append(factorMessage, that.factorMessage)
                .append(links, that.links)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(factorResult)
                .append(factorMessage)
                .append(links)
                .toHashCode();
    }
}
