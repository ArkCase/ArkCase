package com.armedia.acm.plugins.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationProperties
{

    @JsonProperty("application.properties.idleLimit")
    @Value("${application.properties.idleLimit}")
    private Integer idleLimit;

    @JsonProperty("application.properties.idlePull")
    @Value("${application.properties.idlePull}")
    private Integer idlePull;

    @JsonProperty("application.properties.idleConfirm")
    @Value("${application.properties.idleConfirm}")
    private Integer idleConfirm;

    @JsonProperty("application.properties.displayUserName")
    @Value("${application.properties.displayUserName}")
    private String displayUserName;

    @JsonProperty("application.properties.historyDays")
    @Value("${application.properties.historyDays}")
    private Integer historyDays;

    public Integer getIdleLimit()
    {
        return idleLimit;
    }

    public void setIdleLimit(Integer idleLimit)
    {
        this.idleLimit = idleLimit;
    }

    public String getDisplayUserName()
    {
        return displayUserName;
    }

    public void setDisplayUserName(String displayUserName)
    {
        this.displayUserName = displayUserName;
    }

    public Integer getHistoryDays()
    {
        return historyDays;
    }

    public void setHistoryDays(Integer historyDays)
    {
        this.historyDays = historyDays;
    }

    public Integer getIdlePull()
    {
        return idlePull;
    }

    public void setIdlePull(Integer idlePull)
    {
        this.idlePull = idlePull;
    }

    public Integer getIdleConfirm()
    {
        return idleConfirm;
    }

    public void setIdleConfirm(Integer idleConfirm)
    {
        this.idleConfirm = idleConfirm;
    }
}
