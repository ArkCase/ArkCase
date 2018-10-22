package gov.foia.model;

public class BasicTimeToCompleteConfiguration
{
    private Integer fulfill;
    private Integer approve;
    private Integer generalCounsel;
    private Integer billing;
    private Integer release;
    private Integer deadlineIndicator;
    private Integer totalTimeToComplete;

    public Integer getFulfill()
    {
        return fulfill;
    }

    public void setFulfill(Integer fulfill)
    {
        this.fulfill = fulfill;
    }

    public Integer getApprove()
    {
        return approve;
    }

    public void setApprove(Integer approve)
    {
        this.approve = approve;
    }

    public Integer getGeneralCounsel()
    {
        return generalCounsel;
    }

    public void setGeneralCounsel(Integer generalCounsel)
    {
        this.generalCounsel = generalCounsel;
    }

    public Integer getBilling()
    {
        return billing;
    }

    public void setBilling(Integer billing)
    {
        this.billing = billing;
    }

    public Integer getRelease()
    {
        return release;
    }

    public void setRelease(Integer release)
    {
        this.release = release;
    }

    public Integer getDeadlineIndicator()
    {
        return deadlineIndicator;
    }

    public void setDeadlineIndicator(Integer deadlineIndicator)
    {
        this.deadlineIndicator = deadlineIndicator;
    }

    public Integer getTotalTimeToComplete()
    {
        return totalTimeToComplete;
    }

    public void setTotalTimeToComplete(Integer totalTimeToComplete)
    {
        this.totalTimeToComplete = totalTimeToComplete;
    }
}
