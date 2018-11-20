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

public class FoiaConfiguration
{
    private Integer maxDaysInBillingQueue;
    private Integer maxDaysInHoldQueue;
    private Boolean holdedAndAppealedRequestsDueDateUpdateEnabled;
    private Integer requestExtensionWorkingDays;
    private Boolean dashboardBannerEnabled;
    private Boolean receivedDateEnabled;

    public Integer getMaxDaysInBillingQueue() {
        return maxDaysInBillingQueue;
    }

    public void setMaxDaysInBillingQueue(Integer maxDaysInBillingQueue) {
        this.maxDaysInBillingQueue = maxDaysInBillingQueue;
    }

    public Integer getMaxDaysInHoldQueue() {
        return maxDaysInHoldQueue;
    }

    public void setMaxDaysInHoldQueue(Integer maxDaysInHoldQueue) {
        this.maxDaysInHoldQueue = maxDaysInHoldQueue;
    }

    public Boolean getHoldedAndAppealedRequestsDueDateUpdateEnabled() {
        return holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public void setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean holdedAndAppealedRequestsDueDateUpdateEnabled) {
        this.holdedAndAppealedRequestsDueDateUpdateEnabled = holdedAndAppealedRequestsDueDateUpdateEnabled;
    }

    public Integer getRequestExtensionWorkingDays() {
        return requestExtensionWorkingDays;
    }

    public void setRequestExtensionWorkingDays(Integer requestExtensionWorkingDays) {
        this.requestExtensionWorkingDays = requestExtensionWorkingDays;
    }

    public Boolean getDashboardBannerEnabled() {
        return dashboardBannerEnabled;
    }

    public void setDashboardBannerEnabled(Boolean dashboardBannerEnabled) {
        this.dashboardBannerEnabled = dashboardBannerEnabled;
    }

    public Boolean getReceivedDateEnabled() {
        return receivedDateEnabled;
    }

    public void setReceivedDateEnabled(Boolean receivedDateEnabled) {
        this.receivedDateEnabled = receivedDateEnabled;
    }
}
