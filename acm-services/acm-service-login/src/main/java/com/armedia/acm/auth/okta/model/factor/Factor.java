package com.armedia.acm.auth.okta.model.factor;

import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Map _links;
    private Map _embedded;
    private String factorSummary;

    public Factor()
    {
    }

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

    public Map get_links()
    {
        return _links;
    }

    public void set_links(Map _links)
    {
        this._links = _links;
    }

    public Map get_embedded()
    {
        return _embedded;
    }

    public void set_embedded(Map _embedded)
    {
        this._embedded = _embedded;
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
                .append("_links", _links)
                .append("_embedded", _embedded)
                .append("factorSummary", factorSummary)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Factor factor = (Factor) o;

        return new EqualsBuilder()
                .append(id, factor.id)
                .append(factorType, factor.factorType)
                .append(provider, factor.provider)
                .append(status, factor.status)
                .append(created, factor.created)
                .append(lastUpdated, factor.lastUpdated)
                .append(profile, factor.profile)
                .append(_links, factor._links)
                .append(_embedded, factor._embedded)
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
                .append(_links)
                .append(_embedded)
                .append(factorSummary)
                .toHashCode();
    }
}
