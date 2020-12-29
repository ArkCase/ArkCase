package gov.foia.model;

import com.armedia.acm.plugins.task.model.AcmTask;

public class FOIATaskRequestModel
{
    private AcmTask task;
    private FOIARequest request;

    public FOIARequest getRequest()
    {
        return request;
    }

    public void setRequest(FOIARequest request)
    {
        this.request = request;
    }

    public AcmTask getTask()
    {
        return task;
    }

    public void setTask(AcmTask task)
    {
        this.task = task;
    }
}
