package com.armedia.acm.quartz.scheduler;

/*-
 * #%L
 * ACM Tool Integrations: Quartz Scheduler
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

public class AcmJobConfig
{
    @JsonProperty("name")
    private String name;

    @JsonProperty("class")
    private String clazz;

    @JsonProperty("startDelayInSeconds")
    private int startDelay;

    @JsonProperty("repeatIntervalInSeconds")
    private int repeatInterval;

    @JsonProperty("cronExpression")
    private String cronExpression;

    @JsonProperty("requestsRecovery")
    private Boolean requestsRecovery;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getClazz()
    {
        return clazz;
    }

    public void setClazz(String clazz)
    {
        this.clazz = clazz;
    }

    public int getStartDelay()
    {
        return startDelay;
    }

    public void setStartDelay(int startDelay)
    {
        this.startDelay = startDelay;
    }

    public int getRepeatInterval()
    {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval)
    {
        this.repeatInterval = repeatInterval;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public Boolean getRequestsRecovery()
    {
        return requestsRecovery;
    }

    public void setRequestsRecovery(Boolean requestsRecovery)
    {
        this.requestsRecovery = requestsRecovery;
    }
}
