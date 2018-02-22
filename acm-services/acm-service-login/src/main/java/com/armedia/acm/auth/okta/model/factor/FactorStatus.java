package com.armedia.acm.auth.okta.model.factor;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum FactorStatus
{
    NOT_SETUP("NOT_SETUP"), PENDING_ACTIVATION("PENDING_ACTIVATION"), ENROLLED("ENROLLED"), ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), EXPIRED("EXPIRED");

    private String status;

    FactorStatus(String status)
    {
        this.status = status;
    }

    public static boolean isActive(FactorStatus factorStatus)
    {
        return !NOT_SETUP.equals(factorStatus) && !PENDING_ACTIVATION.equals(factorStatus) && !INACTIVE.equals(factorStatus) && !EXPIRED.equals(factorStatus);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("status", status)
                .toString();
    }
}
