package com.armedia.acm.portalgateway.model;

import com.armedia.acm.core.DynamicApplicationConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = PortalUserConfig.class)
public class PortalUserConfig implements DynamicApplicationConfig
{

    @JsonProperty("portal.authenticatedMode")
    @Value("${portal.authenticatedMode}")
    private Boolean authenticatedMode;

    @JsonProperty("portal.responseInstallment.numOfAvailableDays")
    @Value("${portal.responseInstallment.numOfAvailableDays}")
    private Integer numOfAvailableDays;

    @JsonProperty("portal.responseInstallment.maxDownloadAttempts")
    @Value("${portal.responseInstallment.maxDownloadAttempts}")
    private Integer maxDownloadAttempts;


    public Boolean getAuthenticatedMode()
    {
        return authenticatedMode;
    }

    public void setAuthenticatedMode(Boolean authenticatedMode)
    {
        this.authenticatedMode = authenticatedMode;
    }

    public Integer getNumOfAvailableDays()
    {
        return numOfAvailableDays;
    }

    public void setNumOfAvailableDays(Integer numOfAvailableDays)
    {
        this.numOfAvailableDays = numOfAvailableDays;
    }

    public Integer getMaxDownloadAttempts()
    {
        return maxDownloadAttempts;
    }

    public void setMaxDownloadAttempts(Integer maxDownloadAttempts)
    {
        this.maxDownloadAttempts = maxDownloadAttempts;
    }

}
