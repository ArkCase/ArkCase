package com.armedia.acm.services.notification.service.provider.model;

public class BillingTemplateModel
{
    private final String amount;
    private final String token;
    private final String fileId;
    private final String objectId;
    private final String objectType;

    public BillingTemplateModel(String amount, String token, String fileId, String objectId, String objectType)
    {
        this.amount = amount;
        this.token = token;
        this.fileId = fileId;
        this.objectId = objectId;
        this.objectType = objectType;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getToken()
    {
        return token;
    }

    public String getFileId()
    {
        return fileId;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getObjectType()
    {
        return objectType;
    }
}
