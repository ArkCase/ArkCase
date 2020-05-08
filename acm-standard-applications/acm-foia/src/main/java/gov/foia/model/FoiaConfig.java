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

    @JsonProperty("expediteWorkingDaysEnabled")
    @Value("${expediteWorkingDaysEnabled}")
    private Boolean expediteWorkingDaysEnabled;

    @JsonProperty("expediteWorkingDays")
    @Value("${expediteWorkingDays}")
    private Integer expediteWorkingDays;

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

    @JsonProperty("limitedDeliveryToSpecificPageCountEnabled")
    @Value("${limitedDeliveryToSpecificPageCountEnabled}")
    private Boolean limitedDeliveryToSpecificPageCountEnabled;

    @JsonProperty("limitedDeliveryToSpecificPageCount")
    @Value("${limitedDeliveryToSpecificPageCount}")
    private Integer limitedDeliveryToSpecificPageCount;

    @JsonProperty("requireTimesheetToCompleteStandardRequest")
    @Value("${requireTimesheetToCompleteStandardRequest}")
    private Boolean requireTimesheetToCompleteStandardRequest;

    @JsonProperty("requireTimesheetToCompleteDeniedRequest")
    @Value("${requireTimesheetToCompleteDeniedRequest}")
    private Boolean requireTimesheetToCompleteDeniedRequest;

    @JsonProperty("provideReasonToHoldRequestEnabled")
    @Value("${provideReasonToHoldRequestEnabled}")
    private Boolean provideReasonToHoldRequestEnabled;

    @JsonProperty("redirectFunctionalityCalculationEnabled")
    @Value("${redirectFunctionalityCalculationEnabled}")
    private Boolean redirectFunctionalityCalculationEnabled;

    @JsonProperty("automaticCreationOfRequestWhenAppealIsRemandedEnabled")
    @Value("${automaticCreationOfRequestWhenAppealIsRemandedEnabled}")
    private Boolean automaticCreationOfRequestWhenAppealIsRemandedEnabled;

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

    public Boolean getExpediteWorkingDaysEnabled()
    {
        return expediteWorkingDaysEnabled;
    }

    public void setExpediteWorkingDaysEnabled(Boolean expediteWorkingDaysEnabled)
    {
        this.expediteWorkingDaysEnabled = expediteWorkingDaysEnabled;
    }

    public Integer getExpediteWorkingDays()
    {
        return expediteWorkingDays;
    }

    public void setExpediteWorkingDays(Integer expediteWorkingDays)
    {
        this.expediteWorkingDays = expediteWorkingDays;
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

    public Boolean getLimitedDeliveryToSpecificPageCountEnabled()
    {
        return limitedDeliveryToSpecificPageCountEnabled;
    }

    public void setLimitedDeliveryToSpecificPageCountEnabled(Boolean limitedDeliveryToSpecificPageCountEnabled)
    {
        this.limitedDeliveryToSpecificPageCountEnabled = limitedDeliveryToSpecificPageCountEnabled;
    }

    public Integer getLimitedDeliveryToSpecificPageCount()
    {
        return limitedDeliveryToSpecificPageCount;
    }

    public void setLimitedDeliveryToSpecificPageCount(Integer limitedDeliveryToSpecificPageCount)
    {
        this.limitedDeliveryToSpecificPageCount = limitedDeliveryToSpecificPageCount;
    }

    public Boolean getRequireTimesheetToCompleteStandardRequest()
    {
        return requireTimesheetToCompleteStandardRequest;
    }

    public void setRequireTimesheetToCompleteStandardRequest(Boolean requireTimesheetToCompleteStandardRequest)
    {
        this.requireTimesheetToCompleteStandardRequest = requireTimesheetToCompleteStandardRequest;
    }

    public Boolean getRequireTimesheetToCompleteDeniedRequest()
    {
        return requireTimesheetToCompleteDeniedRequest;
    }

    public void setRequireTimesheetToCompleteDeniedRequest(Boolean requireTimesheetToCompleteDeniedRequest)
    {
        this.requireTimesheetToCompleteDeniedRequest = requireTimesheetToCompleteDeniedRequest;
    }

    /**
     * @return the provideReasonToHoldRequestEnabled
     */
    public Boolean getProvideReasonToHoldRequestEnabled()
    {
        return provideReasonToHoldRequestEnabled;
    }

    /**
     * @param provideReasonToHoldRequestEnabled
     *            the provideReasonToHoldRequestEnabled to set
     */
    public void setProvideReasonToHoldRequestEnabled(Boolean provideReasonToHoldRequestEnabled)
    {
        this.provideReasonToHoldRequestEnabled = provideReasonToHoldRequestEnabled;
    }

    public Boolean getRedirectFunctionalityCalculationEnabled()
    {
        return redirectFunctionalityCalculationEnabled;
    }

    public void setRedirectFunctionalityCalculationEnabled(Boolean redirectFunctionalityCalculationEnabled)
    {
        this.redirectFunctionalityCalculationEnabled = redirectFunctionalityCalculationEnabled;
    }

    public Boolean getAutomaticCreationOfRequestWhenAppealIsRemandedEnabled()
    {
        return automaticCreationOfRequestWhenAppealIsRemandedEnabled;
    }

    public void setAutomaticCreationOfRequestWhenAppealIsRemandedEnabled(Boolean automaticCreationOfRequestWhenAppealIsRemandedEnabled)
    {
        this.automaticCreationOfRequestWhenAppealIsRemandedEnabled = automaticCreationOfRequestWhenAppealIsRemandedEnabled;
    }
}
