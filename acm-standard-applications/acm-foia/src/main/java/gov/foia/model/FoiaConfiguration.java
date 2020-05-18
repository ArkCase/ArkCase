package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.plugins.businessprocess.model.SystemConfiguration;

public class FoiaConfiguration extends SystemConfiguration
{
    private Integer maxDaysInBillingQueue;
    private Integer maxDaysInHoldQueue;
    private Boolean holdedAndAppealedRequestsDueDateUpdateEnabled;
    private Integer requestExtensionWorkingDays;
    private Boolean dashboardBannerEnabled;
    private Boolean notificationGroupsEnabled;
    private Boolean requestExtensionWorkingDaysEnabled;
    private Boolean expediteWorkingDaysEnabled;
    private Integer expediteWorkingDays;
    private Boolean purgeRequestWhenInHoldEnabled;
    private Boolean moveToBillingQueueEnabled;
    private Boolean limitedDeliveryToSpecificPageCountEnabled;
    private Integer limitedDeliveryToSpecificPageCount;
    private Boolean requireTimesheetToCompleteStandardRequest;
    private Boolean requireTimesheetToCompleteDeniedRequest;
    private Boolean provideReasonToHoldRequestEnabled;
    private Boolean redirectFunctionalityCalculationEnabled;
    private Boolean feeWaivedRequestsEnabled;
    private Boolean feeWaivedAppealsEnabled;
    private Boolean litigationRequestsEnabled;
    private Boolean litigationAppealsEnabled;
    private Boolean automaticCreationOfRequestWhenAppealIsRemandedEnabled;

    public Integer getMaxDaysInBillingQueue()
    {
        return maxDaysInBillingQueue;
    }

    public void setMaxDaysInBillingQueue(Integer maxDaysInBillingQueue)
    {
        this.maxDaysInBillingQueue = maxDaysInBillingQueue;
    }

    public Integer getMaxDaysInHoldQueue()
    {
        return maxDaysInHoldQueue;
    }

    public void setMaxDaysInHoldQueue(Integer maxDaysInHoldQueue)
    {
        this.maxDaysInHoldQueue = maxDaysInHoldQueue;
    }

    public Boolean getHoldedAndAppealedRequestsDueDateUpdateEnabled()
    {
        return holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public void setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean holdedAndAppealedRequestsDueDateUpdateEnabled)
    {
        this.holdedAndAppealedRequestsDueDateUpdateEnabled = holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public Integer getRequestExtensionWorkingDays()
    {
        return requestExtensionWorkingDays;
    }

    public void setRequestExtensionWorkingDays(Integer requestExtensionWorkingDays)
    {
        this.requestExtensionWorkingDays = requestExtensionWorkingDays;
    }

    public Boolean getDashboardBannerEnabled()
    {
        return dashboardBannerEnabled;
    }

    public void setDashboardBannerEnabled(Boolean dashboardBannerEnabled)
    {
        this.dashboardBannerEnabled = dashboardBannerEnabled;
    }

    public Boolean getNotificationGroupsEnabled()
    {
        return notificationGroupsEnabled;
    }

    public void setNotificationGroupsEnabled(Boolean notificationGroupsEnabled)
    {
        this.notificationGroupsEnabled = notificationGroupsEnabled;
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

    public Boolean getPurgeRequestWhenInHoldEnabled()
    {
        return purgeRequestWhenInHoldEnabled;
    }

    public void setPurgeRequestWhenInHoldEnabled(Boolean purgeRequestWhenInHoldEnabled)
    {
        this.purgeRequestWhenInHoldEnabled = purgeRequestWhenInHoldEnabled;
    }

    public Boolean getMoveToBillingQueueEnabled()
    {
        return moveToBillingQueueEnabled;
    }

    public void setMoveToBillingQueueEnabled(Boolean moveToBillingQueueEnabled)
    {
        this.moveToBillingQueueEnabled = moveToBillingQueueEnabled;
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

    public Boolean getRedirectFunctionalityCalculationEnabled()
    {
        return redirectFunctionalityCalculationEnabled;
    }

    public void setRedirectFunctionalityCalculationEnabled(Boolean redirectFunctionalityCalculationEnabled)
    {
        this.redirectFunctionalityCalculationEnabled = redirectFunctionalityCalculationEnabled;
    }

    public Boolean getFeeWaivedRequestsEnabled()
    {
        return feeWaivedRequestsEnabled;
    }

    public void setFeeWaivedRequestsEnabled(Boolean feeWaivedRequestsEnabled)
    {
        this.feeWaivedRequestsEnabled = feeWaivedRequestsEnabled;
    }

    public Boolean getFeeWaivedAppealsEnabled()
    {
        return feeWaivedAppealsEnabled;
    }

    public void setFeeWaivedAppealsEnabled(Boolean feeWaivedAppealsEnabled)
    {
        this.feeWaivedAppealsEnabled = feeWaivedAppealsEnabled;
    }

    public Boolean getLitigationRequestsEnabled()
    {
        return litigationRequestsEnabled;
    }

    public void setLitigationRequestsEnabled(Boolean litigationRequestsEnabled)
    {
        this.litigationRequestsEnabled = litigationRequestsEnabled;
    }

    public Boolean getLitigationAppealsEnabled()
    {
        return litigationAppealsEnabled;
    }

    public void setLitigationAppealsEnabled(Boolean litigationAppealsEnabled)
    {
        this.litigationAppealsEnabled = litigationAppealsEnabled;
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
