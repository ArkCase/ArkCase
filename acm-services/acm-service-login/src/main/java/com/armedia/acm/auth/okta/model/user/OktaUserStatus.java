package com.armedia.acm.auth.okta.model.user;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum OktaUserStatus
{
    STAGED("STAGED"), PROVISIONED("PROVISIONED"), ACTIVE("ACTIVE"), RECOVERY("RECOVERY"), LOCKED_OUT("LOCKED_OUT"), PASSWORD_EXPIRED("PASSWORD_EXPIRED"), SUSPENDED("SUSPENDED"), DEPROVISIONED("DEPROVISIONED");

    private String status;

    OktaUserStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("status", status)
                .toString();
    }
}
