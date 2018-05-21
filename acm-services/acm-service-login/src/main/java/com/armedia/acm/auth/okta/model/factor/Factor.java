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
import com.armedia.acm.auth.okta.model.ProviderType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Factor extends ErrorResponse
{
    private String id;
    private FactorType factorType;
    private ProviderType provider;
    private FactorStatus status;
    private Date created;
    private Date lastUpdated;
    private FactorProfile profile;

    @JsonProperty("_links")
    private Map links;

    @JsonProperty("_embedded")
    private Map embedded;

    private String factorSummary;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public FactorType getFactorType()
    {
        return factorType;
    }

    public void setFactorType(FactorType factorType)
    {
        this.factorType = factorType;
    }

    public ProviderType getProvider()
    {
        return provider;
    }

    public void setProvider(ProviderType provider)
    {
        this.provider = provider;
    }

    public FactorStatus getStatus()
    {
        return status;
    }

    public void setStatus(FactorStatus status)
    {
        this.status = status;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public FactorProfile getProfile()
    {
        return profile;
    }

    public void setProfile(FactorProfile profile)
    {
        this.profile = profile;
    }

    public Map getLinks()
    {
        return links;
    }

    public void setLinks(Map links)
    {
        this.links = links;
    }

    public Map getEmbedded()
    {
        return embedded;
    }

    public void setEmbedded(Map embedded)
    {
        this.embedded = embedded;
    }

    public String getFactorSummary()
    {
        return factorSummary;
    }

    public void setFactorSummary(String factorSummary)
    {
        this.factorSummary = factorSummary;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("factorType", factorType)
                .append("provider", provider)
                .append("status", status)
                .append("created", created)
                .append("lastUpdated", lastUpdated)
                .append("profile", profile)
                .append("links", links)
                .append("embedded", embedded)
                .append("factorSummary", factorSummary)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Factor factor = (Factor) o;

        return new EqualsBuilder()
                .append(id, factor.id)
                .append(factorType, factor.factorType)
                .append(provider, factor.provider)
                .append(status, factor.status)
                .append(created, factor.created)
                .append(lastUpdated, factor.lastUpdated)
                .append(profile, factor.profile)
                .append(links, factor.links)
                .append(embedded, factor.embedded)
                .append(factorSummary, factor.factorSummary)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(factorType)
                .append(provider)
                .append(status)
                .append(created)
                .append(lastUpdated)
                .append(profile)
                .append(links)
                .append(embedded)
                .append(factorSummary)
                .toHashCode();
    }
}
