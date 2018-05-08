package com.armedia.acm.auth.okta.model.factor;

import com.fasterxml.jackson.annotation.JsonRootName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonRootName("verify")
public class FactorVerification
{
    private String passCode;
    private String nextPassCode;

    public String getPassCode()
    {
        return passCode;
    }

    public void setPassCode(String passCode)
    {
        this.passCode = passCode;
    }

    public String getNextPassCode()
    {
        return nextPassCode;
    }

    public void setNextPassCode(String nextPassCode)
    {
        this.nextPassCode = nextPassCode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("passCode", passCode)
                .append("nextPassCode", nextPassCode)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        FactorVerification that = (FactorVerification) o;

        return new EqualsBuilder()
                .append(passCode, that.passCode)
                .append(nextPassCode, that.nextPassCode)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(passCode)
                .append(nextPassCode)
                .toHashCode();
    }
}
