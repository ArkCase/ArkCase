package gov.foia.model;

public class FoiaConfiguration
{
    private Integer maxDaysInBillingQueue;
    private Integer maxDaysInHoldQueue;
    private Boolean holdedAndAppealedRequestsDueDateUpdateEnabled;
    private Integer requestExtensionWorkingDays;
    private Boolean dashboardBannerEnabled;

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
}
