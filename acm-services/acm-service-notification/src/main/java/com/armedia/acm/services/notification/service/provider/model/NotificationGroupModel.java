package com.armedia.acm.services.notification.service.provider.model;

public class NotificationGroupModel
{
    private String objectNumber;
    private String objectTitle;
    private String assignee;

    public String getObjectNumber()
    {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber)
    {
        this.objectNumber = objectNumber;
    }

    public String getObjectTitle()
    {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle)
    {
        this.objectTitle = objectTitle;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }
}
