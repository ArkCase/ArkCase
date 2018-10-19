package gov.foia.model;

public class QueueTimeToComplete
{

    private RequestTimeToCompleteConfiguration request;
    private AppealTimeToCompleteConfiguration appeal;

    public RequestTimeToCompleteConfiguration getRequest()
    {
        return request;
    }

    public void setRequest(RequestTimeToCompleteConfiguration request)
    {
        this.request = request;
    }

    public AppealTimeToCompleteConfiguration getAppeal()
    {
        return appeal;
    }

    public void setAppeal(AppealTimeToCompleteConfiguration appeal)
    {
        this.appeal = appeal;
    }
}
