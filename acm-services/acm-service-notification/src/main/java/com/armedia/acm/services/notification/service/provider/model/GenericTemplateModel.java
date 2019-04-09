package com.armedia.acm.services.notification.service.provider.model;

public class GenericTemplateModel
{
    private String objectNumber;
    private String objectTitle;

    private String otherObjectValue;

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

    public String getOtherObjectValue() { return otherObjectValue; }

    public void setOtherObjectValue(String otherObjectValue) { this.otherObjectValue = otherObjectValue; }
}
