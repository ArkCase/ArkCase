package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import org.springframework.beans.factory.annotation.Value;

public class FoiaConfig
{
    @JsonProperty("maxDaysInHoldQueue")
    @Value("${maxDaysInHoldQueue}")
    private Integer maxDaysInHoldQueue;

    @JsonProperty("dashboard.banner.enable")
    @Value("${dashboard.banner.enable}")
    private Boolean dashboardBannerEnabled;

    @JsonProperty("requestExtensionWorkingDaysEnabled")
    @Value("${requestExtensionWorkingDaysEnabled}")
    private Boolean requestExtensionWorkingDaysEnabled;

    @JsonProperty("maxDaysInBillingQueue")
    @Value("${maxDaysInBillingQueue}")
    private Integer maxDaysInBillingQueue;

    @JsonProperty("receivedDateEnabled")
    @Value("${receivedDateEnabled}")
    private Boolean receivedDateEnabled;

    @JsonProperty("holdedAndAppealedRequestsDueDateUpdateEnabled")
    @Value("${holdedAndAppealedRequestsDueDateUpdateEnabled}")
    private Boolean holdedAndAppealedRequestsDueDateUpdateEnabled;

    @JsonProperty("purgeRequestWhenInHoldEnabled")
    @Value("${purgeRequestWhenInHoldEnabled}")
    private Boolean purgeRequestWhenInHoldEnabled;

    @JsonProperty("request.extensionWorkingDays")
    @Value("${request.extensionWorkingDays}")
    private Integer requestExtensionWorkingDays;

    @JsonProperty("notification.groups.enabled")
    @Value("${notification.groups.enabled}")
    private Boolean notificationGroupsEnabled;

    @JsonProperty("moveToBillingQueueEnabled")
     @Value("${moveToBillingQueueEnabled}")
    private Boolean moveToBillingQueueEnabled;



    public Integer getMaxDaysInHoldQueue()
    {
        return maxDaysInHoldQueue;
    }

    public void setMaxDaysInHoldQueue(Integer maxDaysInHoldQueue)
    {
        this.maxDaysInHoldQueue = maxDaysInHoldQueue;
    }

    public Boolean getDashboardBannerEnabled()
    {
        return dashboardBannerEnabled;
    }

    public void setDashboardBannerEnabled(Boolean dashboardBannerEnabled)
    {
        this.dashboardBannerEnabled = dashboardBannerEnabled;
    }

    public Boolean getRequestExtensionWorkingDaysEnabled()
    {
        return requestExtensionWorkingDaysEnabled;
    }

    public void setRequestExtensionWorkingDaysEnabled(Boolean requestExtensionWorkingDaysEnabled)
    {
        this.requestExtensionWorkingDaysEnabled = requestExtensionWorkingDaysEnabled;
    }

    public Integer getMaxDaysInBillingQueue()
    {
        return maxDaysInBillingQueue;
    }

    public void setMaxDaysInBillingQueue(Integer maxDaysInBillingQueue)
    {
        this.maxDaysInBillingQueue = maxDaysInBillingQueue;
    }

    public Boolean getReceivedDateEnabled()
    {
        return receivedDateEnabled;
    }

    public void setReceivedDateEnabled(Boolean receivedDateEnabled)
    {
        this.receivedDateEnabled = receivedDateEnabled;
    }

    public Boolean getHoldedAndAppealedRequestsDueDateUpdateEnabled()
    {
        return holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public void setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean holdedAndAppealedRequestsDueDateUpdateEnabled)
    {
        this.holdedAndAppealedRequestsDueDateUpdateEnabled = holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public Boolean getPurgeRequestWhenInHoldEnabled()
    {
        return purgeRequestWhenInHoldEnabled;
    }

    public void setPurgeRequestWhenInHoldEnabled(Boolean purgeRequestWhenInHoldEnabled)
    {
        this.purgeRequestWhenInHoldEnabled = purgeRequestWhenInHoldEnabled;
    }

    public Integer getRequestExtensionWorkingDays()
    {
        return requestExtensionWorkingDays;
    }

    public void setRequestExtensionWorkingDays(Integer requestExtensionWorkingDays)
    {
        this.requestExtensionWorkingDays = requestExtensionWorkingDays;
    }

    public Boolean getNotificationGroupsEnabled()
    {
        return notificationGroupsEnabled;
    }

    public void setNotificationGroupsEnabled(Boolean notificationGroupsEnabled)
    {
        this.notificationGroupsEnabled = notificationGroupsEnabled;
    }

    public Boolean getMoveToBillingQueueEnabled()
    {
        return moveToBillingQueueEnabled;
    }

    public void setMoveToBillingQueueEnabled(Boolean moveToBillingQueueEnabled)
    {
        this.moveToBillingQueueEnabled = moveToBillingQueueEnabled;
    }
}
