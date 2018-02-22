package com.armedia.acm.auth.okta.model.factor;

import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FactorVerifyResult extends ErrorResponse
{
    private FactorResult factorResult;
    private String factorMessage;
    private Map _links;

    public FactorVerifyResult()
    {
    }

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

    public Map get_links()
    {
        return _links;
    }

    public void set_links(Map _links)
    {
        this._links = _links;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("factorResult", factorResult)
                .append("factorMessage", factorMessage)
                .append("_links", _links)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FactorVerifyResult that = (FactorVerifyResult) o;

        return new EqualsBuilder()
                .append(factorResult, that.factorResult)
                .append(factorMessage, that.factorMessage)
                .append(_links, that._links)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(factorResult)
                .append(factorMessage)
                .append(_links)
                .toHashCode();
    }
}
